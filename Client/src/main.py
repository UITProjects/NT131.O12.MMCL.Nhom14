from slave import listen_from_slave_Thread
from time import sleep
from master import create_master_thread
listen_from_slave_Thread.start()
sleep(5)
while True:
    master_thread = create_master_thread()
    master_thread.start()
    print("thread create")
    master_thread.join()
    sleep(5)