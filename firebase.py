import firebase_admin
from firebase_admin import db
import json

def initialize_db():
    cred_obj = firebase_admin.credentials.Certificate('C:\\Users\\dylan\\backend_FastAPI\\mobileapp-7f8b1-firebase-adminsdk-mtyrk-f2ff7014b1.json')
    default_app = firebase_admin.initialize_app(cred_obj, {
    'databaseURL':'https://mobileapp-7f8b1-default-rtdb.europe-west1.firebasedatabase.app/'
    })


def write_to_db(response, path: str):
    ref = db.reference(path)
    ref.set(response)