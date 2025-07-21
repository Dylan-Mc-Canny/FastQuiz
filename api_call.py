import os
from dotenv import load_dotenv
from openai import OpenAI

load_dotenv()


api_key = os.getenv("OPENAI_API_KEY")
if not api_key:
    raise EnvironmentError("OPENAI_API_KEY not found in environment variables.")

client = OpenAI(api_key=api_key)

def get_chat_response(prompt: str) -> str:
    response = client.chat.completions.create(
        model="gpt-4.1-nano",
        messages=[{"role": "user", "content": prompt}],
        response_format={ "type": "json_object" } 
    )
    return response.choices[0].message.content


