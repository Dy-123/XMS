import numpy as np
import firebase_admin
from firebase_admin import auth
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate("x-ms-708bc-firebase-adminsdk-fqav2-5a957a9a04.json")
firebase_admin.initialize_app(cred)



csvData = open("data.csv","r")

for line in csvData:
    field = np.array(line.split(","))
    userEmail = field[0]

    try:
        user = auth.get_user_by_email(userEmail.strip())
        userId = user.uid
        db = firestore.client()
        doc_ref = db.collection(u'UserLogDetail').document(userId)
        doc2_ref = db.collection(u'personPresent').document(userId)
        doc = doc_ref.get()
        doc2=doc2_ref.get()
        
        data = {
            field[1].strip() : field[2].strip()
        }


        if doc.exists:
            doc_ref.update(data)
        else:
            doc_ref.set(data)

        if doc2.exists:
            doc2_ref.update(data)
        else:
            doc2_ref.set(data)

        if field[2].strip() == "Exit" :
            doc2_ref.delete()    

        print("Data updated successfully for " + userEmail.strip())

    except Exception as E:
        print(E)

csvData.close()