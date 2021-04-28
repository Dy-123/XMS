import numpy as np
import firebase_admin
from firebase_admin import auth
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate("x-ms-708bc-firebase-adminsdk-fqav2-5a957a9a04.json")
firebase_admin.initialize_app(cred)

db = firestore.client()

z = input("Enter \n 1 to view all MAC Address \n 2 to add a new MAC Address \n 3 to delete a MAC Address \n\n")
z = int(z)

if z == 1:
    docs = db.collection(u'MAC-Addresses').stream()

    for x in docs:
        print(x.to_dict())

if z == 2:
    x = input("Enter MAC Address of WiFi Network\n")
    y = input("Enter Wifi Name of Location\n")
    x=" "+x
    y=" "+y

    docRef = db.collection(u'MAC-Addresses').document(u'MAC')
    docRef.update({ f'{x}' : f'{y}' })

if z == 3:
    docRef = db.collection(u'MAC-Addresses').document(u'MAC')
    x = input("Enter MAC Address of WiFi Network")
    x=" "+x

    docRef.update({ x : firestore.DELETE_FIELD })

