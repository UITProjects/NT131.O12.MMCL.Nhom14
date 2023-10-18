import base64
import mysql.connector.errors
import pandas

import cipher_module
import server_core
import smtp
from database_statements_module import general_statements
import database_module
from cipher_module import hash_password
from sqlalchemy import create_engine


def authentication(argument: dict) -> dict[str, str]:
    refresh_token_str = argument["refresh_token"]
    if refresh_token_str == "null":
        full_statement_str = general_statements["authentication_credential"].format(
            username_primary=argument["username_primary"],
            hashed_password=hash_password(
                argument["password"]))
    else:
        full_statement_str = general_statements["authentication_token"].format(refresh_token=argument["refresh_token"])

    response_from_mysql = database_module.access_database(full_statement_str)
    print(response_from_mysql)
    if response_from_mysql:
        if refresh_token_str == "null":
            refresh_token_str = cipher_module.generate_random_token(32)
            uuid_str = cipher_module.generate_random_token(10)
            update_token_fullstatement_str = general_statements["update_token"].format(
                username_foreignkey=argument["username_primary"], device_name=argument["device_name"],
                refresh_token=refresh_token_str, uuid=uuid_str)
            database_module.access_database(update_token_fullstatement_str)
            return {"type": "login", "status": "success", "refresh_token": refresh_token_str}
        else:
            return {"type": "login", "status": "success", "refresh_token": refresh_token_str}
    else:
        return {"type": "login", "status": "failed"}


def create_account(argument: dict) -> dict[str, str]:
    full_statement = general_statements["create_account"].format(username_primary=argument["username_primary"],
                                                                 hashed_password=hash_password(argument["password"]),
                                                                 email=argument["email"]
                                                                 )
    try:
        database_module.access_database(full_statement)
    except mysql.connector.errors.IntegrityError:
        return {"create_account": "failed_because_username_exist"}
    return {"create_account": "successful"}


def forgot_password(argument: dict = None):
    if argument["otp_valid"] == "none":
        full_statement = general_statements["forgot_password"].format(email=argument["email"],
                                                                      username_primary=argument["username_primary"])
        response_from_mysql = database_module.access_database(full_statement)
        if response_from_mysql is not None:
            smtp.send_email_otp(argument["random_otp"], argument["email"])
            return {"type": "forgot_password", "status": "correct_username_email"}

        else:
            return {"type": "forgot_password", "status": "incorrect_username_email"}
    elif argument["otp_valid"] == "valid":
        full_statement = general_statements["change_new_password"].format(username_primary=argument["username_primary"],
                                                                          email=argument["email"],
                                                                          new_hashed_password=hash_password(
                                                                              argument["new_password"]))
        try:
            database_module.access_database(full_statement)
        except mysql.connector.errors.IntegrityError:
            pass
        return {"type": "forgot_password", "status": "change_password_success"}


def upload_image_profile(argument: dict):
    image_encoded_base64_string: str = argument["image_encoded_base64_string"]
    image_bytes = base64.b64decode(image_encoded_base64_string)
    fullstatement = general_statements["upload_image_profile"].format(username_primary=argument["username_primary"])
    database_module.access_database(fullstatement, (image_bytes,))
    return {"type": "upload_image_profile", "status": "success"}


def load_profile_image(argument: dict):
    fullstatement: str = general_statements["load_profile_image"].format(refresh_token=argument["refresh_token"])

    response_from_mysql_list = database_module.access_database(fullstatement)
    load_image_bytes: bytes = response_from_mysql_list[0][0]
    server_core.Handle_android_app_socket.large_data = load_image_bytes

    return {"type": "load_profile_image", "status": "pending_download", "large_file_size": str(len(load_image_bytes)),
            "large_data": "true"}


import pandas as pd


def get_weather_data(argument: dict):
    fullstatement: str = general_statements["get_weather"]
    engine = create_engine('mysql+mysqlconnector://', creator=lambda: database_module.mysql_connection)
    response_from_mysql_dataframe = pd.read_sql(fullstatement, engine)
    weather_data_firstrow_dataframe: pandas.DataFrame = response_from_mysql_dataframe.iloc[0]
    return {"type": "get_weather", "status": "success", "weather_data": weather_data_firstrow_dataframe.to_json()}


type_client_message = {
    "authentication": authentication,
    "create_account": create_account,
    "forgot_password": forgot_password,
    "upload_image_profile": upload_image_profile,
    "load_profile_image": load_profile_image,
    "get_weather_data":get_weather_data
}

def process(client_message: dict):
    return type_client_message[client_message["type"]](client_message)
