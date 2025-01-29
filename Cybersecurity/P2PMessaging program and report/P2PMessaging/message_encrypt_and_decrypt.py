import key_encrypt as kdf
import secrets
from Crypto.Cipher import DES
from Crypto.Util.Padding import pad, unpad
import base64


def message_encrypt(key, plaintext):
    # print("Sent Message:")
    block_size = 8  # 56-bits for DES

    iv = secrets.token_bytes(block_size)

    # print(f"Generated IV : {iv}")

    cipher = DES.new(key, DES.MODE_CBC, iv=iv)

    # using a pkcs7 padding style
    padded_plaintext = pad(plaintext, DES.block_size)

    ciphertext = cipher.encrypt(padded_plaintext)
    print(f"Sent IV + CipherText:  {iv + ciphertext}")
    return iv + ciphertext


def message_decrypt(key, encrypted_message):
    iv_and_ciphertext = encrypted_message
    iv = iv_and_ciphertext[:8]
    ciphertext = iv_and_ciphertext[8:]

    if not ciphertext:
        raise ValueError("Ciphertext is missing")
    print(f"\nReceived IV + Ciphertext: {iv_and_ciphertext}")
    # print(f"Length of Encrypted Message: {len(iv_and_ciphertext)}")

    #
    # print(f"IV: {iv}")
    # print(f"Ciphertext: {ciphertext}")
    # print(f"Ciphertext Length: {len(ciphertext)}")

    cipher = DES.new(key, DES.MODE_CBC, iv)

    try:
        padded_plaintext = cipher.decrypt(ciphertext)
        # print(f"Padded Plaintext: {padded_plaintext}")
        plaintext = unpad(padded_plaintext, DES.block_size)
        # print(f"Plaintext: {plaintext.decode()}")
        return plaintext.decode()
    except ValueError as e:
        print(f"Decryption error: {e}")
        raise
