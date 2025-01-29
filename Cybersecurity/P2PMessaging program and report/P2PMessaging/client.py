import socket
import message_encrypt_and_decrypt as med
import key_encrypt as kdf
import threading


class EncryptMessage:
    def __init__(self, socket, key):
        self.socket = socket
        self.key = key

    def run(self):
        while True:
            message = input()
            print()
            if message.lower == b"exit":
                print("Exiting...")
                break
            encrypted_message = med.message_encrypt(self.key, message.encode())
            self.socket.send(encrypted_message)


class DecryptMessage:
    def __init__(self, socket, key):
        self.socket = socket
        self.key = key

    def run(self):
        while True:
            response = self.socket.recv(1024)
            try:
                decrypted_message = med.message_decrypt(self.key, response)
                print(f"Plaintext from Client: {decrypted_message}")
                print("")
            except ValueError as e:
                print(f"\ndecryption error:  {e}")
                print(f"\nWill disconnect user")
                break


class Client:

    def __init__(self, name, password, salt):
        self.salt = salt
        self.name = name
        self.password = password

        self.c_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.c_socket.connect(('127.0.0.1', 12345))

        print(f"{self.name} connected")
        self.key = kdf.derive_key(password, self.salt)

    def start(self):
        print("Start Chatting!")
        decrypt_thread = threading.Thread(target=DecryptMessage(self.c_socket, self.key).run)
        encrypt_thread = threading.Thread(target=EncryptMessage(self.c_socket, self.key).run)

        encrypt_thread.start()
        decrypt_thread.start()

        encrypt_thread.join()
        decrypt_thread.join()
        self.c_socket.close()
