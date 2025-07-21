from fastapi import FastAPI
from pydantic import BaseModel
from api_call import get_chat_response
from firebase import initialize_db
from test import work_please

app = FastAPI()
initialize_db()


class ChatRequest(BaseModel):
    message: str
    path: str

@app.post("/chat")
async def chat(request: ChatRequest):           
    reply = get_chat_response(request.message)
    print(reply)
    print(type(reply))

    path = "questions/" + request.path
    work_please(reply,path)


    return {"reply": "Sent to db"}
