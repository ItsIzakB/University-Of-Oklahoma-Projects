import os

from cryptography.exceptions import InvalidKey
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC


def derive_key(password, salt):
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=8,
        salt=salt,
        iterations=10000,
    )

    key = kdf.derive(password)

    return key



def verify_pass(password, key, salt):
    try:
        kdf = PBKDF2HMAC(
            algorithm=hashes.SHA256(),
            length=8,
            salt=salt,
            iterations=10000,
        )

        kdf.verify(password, key)

        print("Password matches")

    except InvalidKey as e:
        print("Sorry, but your password is incorrect.")
        print("You will now be reported to the FBI.")
