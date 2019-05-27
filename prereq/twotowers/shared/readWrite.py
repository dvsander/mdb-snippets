#!/usr/bin/env python

from bson import objectid
from pymongo import MongoClient

def main():
    # Assign variables to the options & set type appropriately.
    if _id is None:
        _id = objectid.ObjectId()
    
    dbname = "middle-earth"
    collname = "battles"
    
    # Initialize the collection
    
    client = MongoClient('mongodb://dc1.mongodb.university:31000,dc1.mongodb.university:31001,dc1.mongodb.university:31002,dc2.mongodb.university:31003,dc2.mongodb.university:31004/?replicaSet=twotowers')
    db = client[dbname]
    collection = db[collname]
    
    collection.findOne()


if __name__ == '__main__':
    main()
