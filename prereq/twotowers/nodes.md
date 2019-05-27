# TwoTowers scenario

A scenario I used to better understand the failover mechanism, data consistency and datacenter outage scenario's.

## Step 1 : Set up the twotowers replicaset on the 2 machines dc1 and dc2 created with Vagrant

on dc1:

    mkdir -p ~/dc1/r{0,1,2}
    mongod --port 31000 --dbpath ~/dc1/r0 --logpath ~/dc1/r0/mongodb.log --replSet twotowers --fork --sslMode disabled --bind_ip dc1.mongodb.university
    mongod --port 31001 --dbpath ~/dc1/r1 --logpath ~/dc1/r1/mongodb.log --replSet twotowers --fork --sslMode disabled --bind_ip dc1.mongodb.university
    mongod --port 31002 --dbpath ~/dc1/r2 --logpath ~/dc1/r2/mongodb.log --replSet twotowers --fork --sslMode disabled --bind_ip dc1.mongodb.university

on dc2:

    mkdir -p ~/dc2/r{3,4}
    mongod --port 31003 --dbpath ~/dc2/r3 --logpath ~/dc2/r3/mongodb.log --replSet twotowers --fork --sslMode disabled --bind_ip dc2.mongodb.university
    mongod --port 31004 --dbpath ~/dc2/r4 --logpath ~/dc2/r4/mongodb.log --replSet twotowers --fork --sslMode disabled --bind_ip dc2.mongodb.university

on the terminal (any machine)

    mongo dc1.mongodb.university:31000
    rs.initiate({
        _id: "twotowers",
        version: 1,
        members: [
            { _id: 0, host : "dc1.mongodb.university:31000" },
            { _id: 1, host : "dc1.mongodb.university:31001" },
            { _id: 2, host : "dc1.mongodb.university:31002" },
            { _id: 3, host : "dc2.mongodb.university:31003" },
            { _id: 4, host : "dc2.mongodb.university:31004" }
        ]
    })

    use admin
    db.createUser({user : 'admin', pwd : 'secret', roles : ['root']})
    exit
    mongo --host "twotowers/dc1.mongodb.university:31000,dc1.mongodb.university:31001,dc1.mongodb.university:31002,dc2.mongodb.university:31003,dc2.mongodb.university:31004" -u admin -p secret --authenticationDatabase admin
    rs.isMaster()

## Step 2: Create some load against the new created

From host machine

    mongo --host "twotowers/127.0.0.1:31000,127.0.0.1:31001,127.0.0.1:31002,127.0.0.1:31003,127.0.0.1:31004" -u admin -p secret --authenticationDatabase admin
