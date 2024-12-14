import json

import mysql.connector
import os

USERNAME = os.getenv("username_mysql")
PASSWORD = os.getenv("password_mysql")
#HOST = "app.mariadb.uitprojects.com"
HOST = "maria_db"

PORT = 3306
DATABASE = "mobile_project"
SSL_CERT = os.getenv("ssl_client_cert")
SSL_key = os.getenv("ssl_client_key")
from threading import Lock

mysql_Lock = Lock()
mysql_connection = mysql.connector.connect(user=USERNAME, password=PASSWORD,
                                           host=HOST,
                                           database='mobile_project',
                                           ssl_cert=SSL_CERT,

                                           ssl_key=SSL_key, )


def access_database(statement: str, param_any=None):
    with mysql_Lock:
        while not mysql_connection.is_connected():
            print("Lost connection to mysql, reconnect in 3 seconds")
            mysql_connection.reconnect(delay=3)
        execute_command_interpreter = mysql_connection.cursor()
        execute_command_interpreter.execute(statement, param_any)
        response_tuple = execute_command_interpreter.fetchall()
        mysql_connection.commit()
    return response_tuple


general_statements: dict[str, str] = {
    'authentication_token': "select admin_account from account_setting join account on account_setting.username = "
                            "account.username join devices on account.username = devices.username where "
                            "refresh_token=%s",
    'authentication_credential': "select username from mobile_project.account "
                                 "where username=%s and password=%s",
    'forgot_password': "SELECT username,email FROM mobile_project.account "
                       "where username = %s",
    'change_new_password': "UPDATE `mobile_project`.`account` "
                           "SET `hashed_password` = '{new_hashed_password}' "
                           "WHERE (`username` = '%s' and `email` = '%s');",
    'upload_image_profile': "UPDATE `mobile_project`.`account`"
                            " SET `image_profile` = %s "
                            "WHERE (`username` = '{username}');",
    'load_profile_image': "select image_profile from account_setting join devices on devices.username = "
                          "account_setting.username where refresh_token = %s",
    'get_weather': "SELECT * FROM mobile_project.weather_api order by date_primarykey desc",
    'update_token': "INSERT INTO `mobile_project`.`devices`(`uuid`,`device_name`,`username`,`refresh_token`)"
                    "VALUES(%s,%s,%s,%s);",
    'update_temp': "insert into `mobile_project`.`raspberry`(`time_primary`,`temperature`,`humidity`)VALUES(%s,%s,%s)",
    'get_temp': "select temperature,humidity,time_primary from mobile_project.raspberry order by time_primary desc "
                "limit 1",
    "history": "select temperature,humidity,time_primary from mobile_project.raspberry where time_primary>%s and "
               "time_primary <%s "
               "order by time_primary {order} {limit} ",
    'create_account': "insert into mobile_project.account(`username`,`password`,`email`) values(%s,%s,"
                      "%s)",
    "update_otp": "UPDATE `mobile_project`.`account` SET `reset_password` = '1',otp_code=%s,expire=%s WHERE (`username` = %s);",
    "check_valid_otp": "select * from account where "
                       "reset_password = true and "
                       "otp_code =%s and "
                       "expire>%s and "
                       "username=%s",
    "update_otp_android_project": "insert into otp_email(`UUID`,`email`,`otp_code`,`expire`) values (%s,%s,%s,%s);",
    "check_otp_android_project": "select * from otp_email where email=%s and otp_code=%s and expire > %s",
    "change_password": "update account set "
                       "hashed_password=%s,"
                       "reset_password=false,"
                       "expire=null,otp_code=null "
                       "where username=%s",
    "update_setting_device": "update iot_setting set delay = %s,war_temp= %s, war_humidity =%s where device_name = %s",
    "get_device_info": "select device_name,delay,war_temp,war_humidity from iot_setting where device_name = %s",
    "check_limit": "select war_temp,war_humidity from iot_setting "
                   "where device_name ='esp8266' and (%s> war_temp)",
    "get_emails": "select email from account join account_setting on account.username = account_setting.username where notification = 1",
    "get_min_max": "select {type} from raspberry where time_primary > %s and time_primary < %s order by {type} desc",
    "user_notification": "select notification from account_setting "
                         "join devices "
                         "on account_setting.username = devices.username "
                         "where refresh_token = %s",
    'set_user_notification': 'update account_setting join devices on devices.username = account_setting.username set '
                             'notification=%s where refresh_token = %s'
}
