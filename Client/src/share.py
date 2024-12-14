import socket
from threading import Lock

share_lock = Lock()
is_make_change = False
device_alive = False
delay = 1000
reset = False

def receive(length: int, current_socket: socket.socket) -> bytes:
    byte_read = 0
    actual_data = b""
    while byte_read < length:
        try:
            actual_data += current_socket.recv(length - byte_read)
        except ConnectionError:
            return b""
        if actual_data == b"":
            return b""
        byte_read = len(actual_data)
    return actual_data
