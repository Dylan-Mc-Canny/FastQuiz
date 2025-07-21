import firebase_admin
from firebase_admin import db
import json

def initialize_db():
    cred_obj = firebase_admin.credentials.Certificate()
    default_app = firebase_admin.initialize_app(cred_obj, {
    'databaseURL':#url here removed
    })


def write_to_db(response, path: str):
    ref = db.reference(path)
    ref.set(response)
