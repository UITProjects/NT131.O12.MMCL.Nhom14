import socket

from share import *
from time import sleep
from threading import Thread
import share
from share import share_lock
from cipher_module import *


def listen_master():
    master_socket = socket.socket(socket.AF_INET, type=socket.SOCK_STREAM)
    master_socket.connect(("servernhung", 2509))

    master_socket.settimeout(120)

    while True:
        try:
            with share_lock:
                is_device_alive = share.device_alive
            if is_device_alive is False:
                master_socket.send(encrypt(False.to_bytes(length=1, byteorder="little", signed=False)))
                sleep(2)
                continue
            else:
                master_socket.send(encrypt(True.to_bytes(length=1, byteorder="little", signed=False)))
                command_code_bytes = receive(16, master_socket)
                command_code_bytes = decrypt(command_code_bytes)
        except:
            break
        if command_code_bytes == b"":
            try:
                break
            except socket.error:
                print("Connection reset error, try to reconnect in 3 seconds")
                break

        command_code_int = int.from_bytes(command_code_bytes, byteorder="little", signed=False)
        if command_code_int == 200:
            new_time_delay_bytes = receive(16, master_socket)
            new_time_delay_bytes = decrypt(new_time_delay_bytes)
            new_time_delay_int = int.from_bytes(new_time_delay_bytes, byteorder="little", signed=False)
            print(f"set new esp8266 new time delay: {new_time_delay_int}")
            with share_lock:
                share.is_make_change = True
                share.delay = new_time_delay_int
        elif command_code_int == 0:
            with share_lock:
                share.is_make_change = True
                share.reset = True

        sleep(2)


def changes(new_delay):
    with share_lock:
        share.delay = new_delay
        share.is_make_change = True


def create_master_thread():
    return Thread(target=listen_master)
