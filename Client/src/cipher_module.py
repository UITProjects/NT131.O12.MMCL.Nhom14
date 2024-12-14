from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes
import base64
from Crypto.Util.Padding import pad
from Crypto.Util.Padding import unpad
import os
import secrets

import hashlib


def hash_password(plain_password_str: str):
    hash_interpreter = hashlib.sha256()
    password_bytes = plain_password_str.encode()
    hash_interpreter.update(password_bytes)
    hash_password = hash_interpreter.hexdigest()
    return hash_password


key_bytes = base64.b64decode(os.getenv("symmetric_key"))
cipher_ecbmode = AES.new(key_bytes, AES.MODE_ECB)


def generating_key():
    key_bytes = get_random_bytes(32)
    print(base64.b64encode(key_bytes).decode())


def encrypt(plaintext_bytes: bytes) -> bytes:
    plaintext_padding_bytes = pad(plaintext_bytes, 16)
    encrypted_plaintext_bytes = cipher_ecbmode.encrypt(plaintext_padding_bytes)
    return encrypted_plaintext_bytes


def decrypt(ciphertext_bytes: bytes) -> bytes:
    plaintext_bytes = cipher_ecbmode.decrypt(ciphertext_bytes)
    plaintext_unpadding_bytes = unpad(plaintext_bytes, 16)
    return plaintext_unpadding_bytes


def generate_random_token(length: int) -> str:
    return secrets.token_hex(length)


