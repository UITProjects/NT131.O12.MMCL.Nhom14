import asyncio
import json
from typing import Union, Annotated

import numpy
from fastapi import FastAPI, Form, Request, HTTPException
from starlette.responses import StreamingResponse
from handle_types_message_client_module import *

MESSAGE_STREAM_DELAY = 1  # second
MESSAGE_STREAM_RETRY_TIMEOUT = 15000

app = FastAPI()


@app.get('/live-data')
async def message_stream(request: Request):
    async def generator():
        latest: (float, float) = None
        while True:
            response = database_module.access_database(general_statements["get_temp"])
            response = response[0]
            if response != latest:
                temp_float32 = numpy.float32(response[0])
                humidity_float32 = numpy.float32(response[1])
                time_int = response[2]
                data_bytes = b""
                data_bytes += temp_float32.tobytes()
                data_bytes += humidity_float32.tobytes()
                data_bytes += time_int.to_bytes(byteorder="little", signed=True, length=4)
                latest = response
                yield data_bytes
            await asyncio.sleep(1)

    return StreamingResponse(generator())


@app.post("/authentication")
async def login(username_primary: Annotated[str, Form()],
                password: Annotated[str, Form()],
                device_name: Annotated[str, Form()],
                refresh_token: Annotated[str, Form()]
                ):
    return authentication_credential(username_primary, password, device_name, refresh_token)


@app.post("/registration")
async def registration(
        username_primary: Annotated[str, Form()],
        password: Annotated[str, Form()],
        email: Annotated[str, Form()]
):
    return create_account(username_primary, password, email)


@app.get("/current_temp")
async def get_current_temp():
    return current()


@app.get("/history")
async def get_history(left: int = 0, right: int = 2147483647, order: str = "desc", limit: int = 1000):
    if order == "desc" or order == "asc":
        return history(left, right, order, limit)
    else:
        raise HTTPException(status_code=400, detail="order is invalid")


@app.post("/update_temp")
async def temp_update(time_primary: Annotated[int, Form()],
                      temperature: Annotated[float, Form()],
                      humidity: Annotated[float, Form()]
                      ):
    return update_temp(time_primary, temperature, humidity)
