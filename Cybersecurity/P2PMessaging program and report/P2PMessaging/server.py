import socket
import threading

def handle_clients(client1, client2):
    while True:
        msg = client1.recv(1024)

        try:
            if msg == 'exit':
                print("Client 1 Disconnected")
                break
            else:
                client2.send(msg)
        except Exception as e:
            print(f"Error dealing with Client 1: {e}")
            break

        msg = client2.recv(1024)

        try:
            if msg == 'exit':
                print('Client 2 Disconnected')
                break
            else:
                client1.send(msg)
        except Exception as e:
            print(f'Error dealing with Client 2: {e}')
            break

    client1.close()
    client2.close()


try:
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)  # for port reuse
    server_socket.bind(('127.0.0.1', 12345))

    server_socket.listen(2)

    print("Server: Waiting for 2 people to join")

    clients = []

    while len(clients) < 2:
        conn, addr = server_socket.accept()
        print("Chatter joined")
        clients.append(conn)

    print('Successful connection')
    threading.Thread(target=handle_clients, args=(clients[0], clients[1])).start()

except OSError as e:
    print(f"OS error: {e}")
