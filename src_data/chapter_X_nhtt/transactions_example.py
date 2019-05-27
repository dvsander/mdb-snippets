import pymongo
def do_transaction(client, db):
    s = client.start_session()
    s.start_transaction()
    try:
        ir = db.shipment.insert_one({"product": "abc123", "qty":  100}, session=s)
        ur = db.inventory.update_one({"_id": "abc123"}, \
          {"$inc": {"qty": -100}}, session=s)
        if ur.modified_count == 1:
            print("Transaction complete")
            s.commit_transaction()
        else:
            s.abort_transaction()
            print("Transaction Aborted, could not decrease qty in inventory")
    except Exception as e:
        s.abort_transaction()
        print("Transaction Aborted {}".format(e))
    s.end_session()

client = pymongo.MongoClient('mongodb://localhost:27017/test?replicaSet=replset')
db = client.products
db.shipment.drop()
db.create_collection("shipment")    # Collection can not be create in transaction
db.inventory.update_one({"_id": "abc123"}, {"$set": {"qty": 300}}, upsert=True)
do_transaction(client, db)
