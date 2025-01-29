from client import Client

try:
    bob = Client('Bob', b'pass', b'\x12\x34\x56\x78\x9a\xbc\xde\xf0\x11\x22\x33\x44\x55\x66\x77\x88')
    bob.start()
except ConnectionRefusedError as e:
    print("Failed to connect to server")
