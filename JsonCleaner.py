import json
from firebase import write_to_db
from firebase import initialize_db

def json_clean(cleaned_reply,path: str):
    try:
        parsed = json.loads(cleaned_reply)  
    except json.JSONDecodeError as e:
        print("Error decoding JSON:", e)
        print(cleaned_reply)
        raise e

    write_to_db(parsed,path) 
