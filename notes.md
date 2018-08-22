# MongoDB Technical Training notes

These are notes taken during technical training and experimenting with MongoDB.

<!-- TOC -->

- [MongoDB Technical Training notes](#mongodb-technical-training-notes)
  - [Connection Details](#connection-details)
  - [Data types](#data-types)
  - [Use cases](#use-cases)
    - [Navigating via command line](#navigating-via-command-line)
    - [Executing javascript on the host machine](#executing-javascript-on-the-host-machine)
    - [Inserting one single document](#inserting-one-single-document)
    - [Inserting multiple documents](#inserting-multiple-documents)
    - [Reading documents](#reading-documents)
    - [Projection on documents](#projection-on-documents)
    - [Updating documents](#updating-documents)
    - [Update many documents](#update-many-documents)
    - [Upserts](#upserts)
    - [Replace one](#replace-one)
    - [Delete](#delete)
    - [Advanced query operators](#advanced-query-operators)
      - [Comparison operators](#comparison-operators)
      - [Element operators](#element-operators)
      - [Logical operators](#logical-operators)
      - [Array operators](#array-operators)
      - [Regex](#regex)
  - [The MongoD](#the-mongod)
    - [Starting the server](#starting-the-server)
    - [Shutting down the server](#shutting-down-the-server)
    - [Architecture](#architecture)
    - [Data structures](#data-structures)
    - [Configuration file](#configuration-file)
    - [Creating users via command line](#creating-users-via-command-line)
    - [Folder hierarchy](#folder-hierarchy)
    - [Basic linux](#basic-linux)
    - [Basic mongo shell commands](#basic-mongo-shell-commands)
      - [Logging basics](#logging-basics)
      - [Profiling the database](#profiling-the-database)
  - [Security](#security)
    - [Introduction](#introduction)
    - [Adding security to a new mongod](#adding-security-to-a-new-mongod)
    - [Terminology](#terminology)
      - [Resources](#resources)
      - [Privileges](#privileges)
      - [Role inheritance](#role-inheritance)
      - [Network Authentication Restrictions](#network-authentication-restrictions)
    - [Built in roles](#built-in-roles)
  - [Server Tools Overview](#server-tools-overview)
    - [mongostat](#mongostat)
    - [mongodump and mongorestore](#mongodump-and-mongorestore)
    - [mongoexport and mongorestore](#mongoexport-and-mongorestore)
  - [Replication](#replication)
    - [Setting up a MongoDB replica set](#setting-up-a-mongodb-replica-set)
      - [How to initiate a replica set](#how-to-initiate-a-replica-set)
      - [How to add nodes to a replica set](#how-to-add-nodes-to-a-replica-set)
      - [How to check the status of a replica set](#how-to-check-the-status-of-a-replica-set)
    - [The Replication Configuration Document](#the-replication-configuration-document)
    - [Replication commands](#replication-commands)
    - [The oplog](#the-oplog)
      - [Configuring the oplog](#configuring-the-oplog)
      - [Querying the oplog](#querying-the-oplog)
      - [One final thing about oplog and the local database](#one-final-thing-about-oplog-and-the-local-database)
    - [Reconfiguring a running replica set](#reconfiguring-a-running-replica-set)
    - [Reading and writing to a replica set](#reading-and-writing-to-a-replica-set)
    - [Failover and elections](#failover-and-elections)
  - [Write concerns, Read concerns and Read preferences](#write-concerns-read-concerns-and-read-preferences)

<!-- /TOC -->

## Connection Details

    #Class cluster: READ ONLY BY DESIGN
    mongo "mongodb://cluster0-shard-00-00-jxeqq.mongodb.net:27017,cluster0-shard-00-01-jxeqq.mongodb.net:27017,cluster0-shard-00-02-jxeqq.mongodb.net:27017/test?replicaSet=Cluster0-shard-0" --authenticationDatabase admin --ssl --username m001-student

    #Self cluster: 2FA PROTECTED + TRAINING ENVIRONMENT (NO SUBSCRIPTION)
    mongo "mongodb+srv://cluster-nhtt-oip7z.gcp.mongodb.net/test" --ssl --username m001-student

## Data types

Read more: [MongoDB BSON Data Types](https://docs.mongodb.com/manual/reference/bson-types/)

MongoDB was built on top of the JSON spec. Additional data types were added.

| Data type             | Origin     |
| --------------------- | ---------- |
| object                | JSON       |
| array                 | JSON       |
| string                | JSON       |
| number                | JSON       |
| bool ("true"/"false") | JSON       |
| null ("null")         | JSON       |
|                       |            |
| int                   | BSON       |
| long                  | BSON       |
| decimal (since 3.4)   | BSON       |
| double                | BSON       |
| date                  | BSON       |
| ObjectId              | BSON       |
|                       |            |
| bindata               | BSON (adv) |
| regex                 | BSON (adv) |
| javascript            | BSON (adv) |
| javascriptWithScope   | BSON (adv) |
| minKey                | BSON (adv) |
| maxKey                | BSON (adv) |

## Use cases

### Navigating via command line

    show databases
    use database
    show collections
    db.collection.find().pretty()
    db.stats()

### Executing javascript on the host machine

Navigate to the directory on the host where the .js file is located. Start up the mongo connection. Use the load() command to run the script inside the shell.

    load("loadMovieDetailsDataset.js")

### Inserting one single document

    db.moviesScratch.insertOne({title:"Star Trek II: The Wrath of Khan", year: 1982, imdb: "tt0084726"})

### Inserting multiple documents

Ordered (standard): stops on error, e.g. duplicate key.  
Unordered: continue on error.

    [array of docs to insert], { "ordered" : false }
    db.moviesScratch.insertMany([array of docs to insert])

The mongo command is a full-fledged javascript interpreter, so this also works:

    for ( i=0; i< 100; i++) { db.messages.insert( { 'msg': 'not yet', _id: i } ) }

### Reading documents

find inside embedded documents

    {"awards.wins" : 2, "awards.nominations" : 2})
    {"rated" : "PG", "awards.nominations" : 10})

find inside arrays: any array value _exact match_

    {"actors": "Jeff Bridges"})
    {"actors": ["Timothy Bottoms", "Jeff Bridges", "Cybill Shepherd", "Ben Johnson"]})
    {"writers" : ["Ethan Coen", "Joel Coen"]}
    {"genres" : "Family"}

find inside arrays: any array value on specified position

    {"actors.0" : "Jeff Bridges"}
    {"genres.1" : "Western"}

### Projection on documents

0 to exclude fields.  
1 to include fields (and exclude all others).  
\_id is always returned in projections, hard exclusion (0) is necessary in that case.

    {filter},{projections}

### Updating documents

Read more: [Update Operator Manual](https://docs.mongodb.com/manual/reference/operator/update/)

Update _first_ document retrieved from the result set

    updateOne({ "title" : "The Martian" } , { "$set" : { "poster" : "somePosterUrl"}})
    { "acknowledged" : true, "matchedCount" : 1, "modifiedCount" : 1 }

    updateOne({ "title" : "The Martian" } , { "$inc" : { "tomato.reviews" : 3, "tomato.userReviews" : 25}})
    { "acknowledged" : true, "matchedCount" : 1, "modifiedCount" : 1 }

    # $each makes sure each element gets added individually as elements to the array. Without $each this would become a single element array containing... one array.
    updateOne({ "title" : "The Martian" } ,
        { "$push" : {
                reviews : {
                    $each : [
                        {
                            "rating" : 0.5,
                            "reviewer" : "John",
                            "text" : "Bloody awful."
                        }, {
                            "rating" : 5,
                            "reviewer" : "Kristina",
                            "text" : "Excellent!"
                        }
                    ]
                }
            }
        }
    )

### Update many documents

Update _all_ documents retrieved by the result set

    # value for rated "" can be anything
    updateMany({rated:null},{$unset : { rated : "" }})
    { "acknowledged" : true, "matchedCount" : 1599, "modifiedCount" : 1599 }

### Upserts

If the filter does not match any documents in the collection, the document will be inserted in the collection.  
If the filter matches a document, the document will be updated.

    updateOne(
        {"imdb.id" : detail.imdb.id},
        { "$set" : detail },
        { "upsert" : true }
    )

### Replace one

Useful vs updateOne when it's simpler to replace an entire document instead op update operators.

    detailDoc = findOne({"imdb.id" : "tt4368814"});
    //update detailDoc
    replaceOne(
        {"imdb.id" : detaildoc.imdb.id},
        detailDoc
    );

### Delete

Delete one or more documents

    deleteOne({"_id" : ObjectId("5b73dbca36a123c73792652a")}
    deleteMany({"reviewer_id" : 759723314} )

### Advanced query operators

Read more: [Query and Projection Operators Manual](https://docs.mongodb.com/manual/reference/operator/query/)

#### Comparison operators

Remark: $ne will also return documents where field is _not set_

    find({runtime: {$gt: 90}})
    find({runtime: {$gt: 90}}, {_id: 0, title: 1, runtime: 1})
    find({runtime: {$gt: 90, $lt: 120}}, {_id: 0, title: 1, runtime: 1})
    find({runtime: {$gte: 90, $lte: 120}}, {_id: 0, title: 1, runtime: 1})
    find({runtime: {$gte: 180}, "tomato.meter": 100}, {_id: 0, title: 1, runtime: 1})
    find({rated: {$ne: "UNRATED"}}, {_id: 0, title: 1, rated: 1})
    find({rated: {$in: ["G", "PG"]}}, {_id: 0, title: 1, rated: 1})
    find({rated: {$in: ["G", "PG", "PG-13"]}}, {_id: 0, title: 1, rated: 1})
    find({rated: {$in: ["R", "PG-13"]}}, {_id: 0, title: 1, rated: 1})
    find({"writers" : { "$in" : ["Ethan Coen", "Joel Coen"] } }, {"_id" : 0, "title" : 1, "writers" : 1})

#### Element operators

$ exists true: matches documents containing the key.  
$ exists false: matches documents not containing the key.

    find({mpaaRating: {$exists: true}})
    find({mpaaRating: {$exists: false}})

Default behavior of querying _null_ is to exclude documents with key not set.

    find({mpaaRating: null})
    find({$and: [{"metacritic": {$ne: null}}, {"metacritic": {$exists: true}}]})
    find({$and: [{"metacritic": null}, {"metacritic": {$exists: true}}]})

Only yields documents where key is set _and_ having a certain type.

    find({viewerRating: {$type: "int"}}).pretty()
    find({viewerRating: {$type: "double"}}).pretty()

#### Logical operators

    find({"$or" : [ {"watlev" : "always dry"},{ "depth" : 0} ] })

    find({$or: [{"tomato.meter": {$gt: 95}}, {"metacritic": {$gt: 88}}]},
    {_id: 0, title: 1, "tomato.meter": 1, "metacritic": 1})

    find({$and: [{"tomato.meter": {$gt: 95}}, {"metacritic": {$gt: 88}}]},
    {_id: 0, title: 1, "tomato.meter": 1, "metacritic": 1})

    find({"tomato.meter": {$gt: 95}, "metacritic": {$gt: 88}},
    {_id: 0, title: 1, "tomato.meter": 1, "metacritic": 1})

    find({$and: [{"metacritic": {$ne: null}}, {"metacritic": {$exists: true}}]},
    {_id: 0, title: 1, "metacritic": 1})

    find({$and: [{"metacritic": null}, {"metacritic": {$exists: true}}]},
    {_id: 0, title: 1, "metacritic": 1})

#### Array operators

$all: All values need to be in the array (in any order). The document value itself can contain any number of array elements. "All of this and others".

    find({genres: {$all: ["Comedy", "Crime"]}}, {_id: 0, title: 1, genres: 1})
    find({genres: {$all: ["Comedy", "Crime", "Drama"]}}, {_id: 0, title: 1, genres: 1})
    find({"sections" : { "$all" : ["AG1", "MD1", "OA1"]}})

$size: The number of elements (size) of the array

    find({countries: {$size: 1}})
    find({"sections" : { "$size" : 2}})

$elemMatch: Find inside array _document_ values that match.

Suppose the following

    boxOffice : [
    { "country": "USA", "revenue": 228.4 },
    { "country": "Australia", "revenue": 19.6 },
    { "country": "UK", "revenue": 33.9 },
    { "country": "Germany", "revenue": 16.2 },
    { "country": "France", "revenue": 19.8 } ]

    find({"boxOffice.country": "Germany", "boxOffice.revenue": {$gt: 17}})

The above does _not_ do the trick. It matches _any_ document in the array matching any of the criteria.

    find({boxOffice: {$elemMatch: {"country": "Germany", "revenue": {$gt: 17}}}})
    find({boxOffice: {$elemMatch: {"country": "Germany", "revenue": {$gt: 16}}}})
    find({ "results" : {"$elemMatch" : {"product" : "abc", "score" : 7}}})

#### Regex

$regex used for wildcard searches

    find({}, {_id: 0, "title": 1, "awards.text": 1}).pretty()
    find({"awards.text": {$regex: /^Won.* /}}, {_id: 0, title: 1, "awards.text": 1}).pretty()

Read more: $text and full text indexes out of scope

    find({"awards.text": {$regex: /^Won.* /}}, {_id: 0, title: 1, "awards.text": 1}).pretty()

## The MongoD

### Starting the server

    mongod --port 30000 --dbpath first_mongod --logpath first_mongod/mongod.log --fork

- port the port to listen to
- dbpath the location of data files
- logpath the location of log files
- fork a flag to run as a background process

### Shutting down the server

locally

    use admin
    db.shutdownServer()

remote

    mongo admin --eval 'db.shutdownServer()'

### Architecture

- Query Language: interaction with the database and applications through client side libraries MongoDB Drivers
- Document Data Model: management of namespaces, indexes, data structures, replication mechanism (writeconcern, readconcern)
- Storage layer: persistency later calls, system calls, disk flush, file structures, encryption/compression
- Security: user management, network, authorization
- Admin: creating databases, logging infrastructure, management

Multiple MongoD can form a replica set. 1 node managing read/write (primary) and several others storing copies of the data. A failover protocol ensures in case of failover the system can elect a new master without downtime or loss of data. Replica sets can be deployed anywhere on different infrastructure.

MongoDB is a scalable database. Different mongod processes can scale horizontally through sharding. A mongoS is a shard routing component handling all operations to the shard cluster transparently.

Sharded clusters are composed of a mongos, replica sets and a special type of replica set, the config servers which manage all the metadata, how many mongos, how many replica sets etc.

### Data structures

- Database: top-level hierarchical structure
  - createCollection, createUser, dropUser, runCommand (change behavior of database)
- Collection: similar to a folder in the database hierarchy, storing one or more documents
- Index: a special type of collection holding a subset of the data in an easy traversable format
- Documents: a single chunk of data belonging together
  - all data belonging to the documents are stored together on disk and memory
  - stored in BSON, represented as JSON

### Configuration file

YAML file containing the configuration parameters for mongod and mongos.

- Provide same functionality as command line options
- Improve readability
- Use documentation to facilitate mapping

Usage:

    mongod --config "/etc/mongod.conf"
    mongod -f "/etc/mongod.conf"

Example:

    storage:
        dbPath: "/data/db"
    systemLog:
        path: "/data/log.mongod.log"
        destination: "file"
    replication:
        replSetName: M103
    net:
        bindIp : "127.0.0.1, 192.168.0.10"
    ssl:
        mode: "requireSSL"
        PEMKeyFile: "/etc/ssl/ssl.pem"
        CAFile: "/etc/ssl/SSLCA.pem"
    security:
        keyFile: "/data/keyfile"
    processManagement:
        fork : true

### Creating users via command line

    mongo admin --host localhost:27000 --eval '
        db.createUser({
            user: "m103-admin",
            pwd: "m103-pass",
            roles: [
            {role: "root", db: "admin"}
            ]
        })
    '

### Folder hierarchy

- /data/db : _never_ edit these.
  - collection.\*.wt contains collection data (binary)
  - index.\*.wt contains index data (binary)
  - .\*.lock blocks processes for accessing same files
  - (dir) diagnostic.data only for diagnostic purposes used by MDB support engineers
  - (dir) journal: writes are buffered in mem, flushed 60s, writes ahead buffer entries every 50ms

### Basic linux

    sudo mkdir -p /var/mongodb/db
    sudo chown vagrant:vagrant /var/mongodb/db

### Basic mongo shell commands

Basic helper groups

- db.method: interact with the database
  - db.collection.method: interact with a specific collection
- rs.method: control replica set
- sh.method: control shared cluster deployment and management

User management commands:

    db.createUser()
    db.dropUser()

Collection management commands:

    db.collection.renameCollection()
    db.collection.createIndex()
    db.collection.drop()

Database management commands:

    db.dropDatabase()
    db.createCollection()

Database status command:

    db.serverStatus()

Creating index with Database Command versus Creating index with Shell Helper::

    db.runCommand(
        { "createIndexes": collection },
        { "indexes": [
            { "key": { "product": 1 }},
            { "name": "name_index" }]
        }
    )
    db.collection.createIndex(
        { "product": 1 },
        { "name": "name_index" }
    )

Introspect a Shell Helper:

    db.collection.createIndex

#### Logging basics

Process log entails the entire mongod instance. It supports multiple components for controlling granularity of the events captured.

Get the logging components and change the logging level:

    db.getLogComponents()
    db.setLogLevel(0, "index")

Use tail or other tools to retrieve the log.

#### Profiling the database

Profiler captures

- CRUD operations
- Administrative operations
- Configuration operations (cluster)

And has the following levels

- 0 (default), does not collect any data
- 1 collects data for operations > slowms
- 2 collects data for all operations

Usage

    db.getProfilingLevel()
    db.setProfilingLevel(1)
    db.setProfilingLevel( 1, { slowms: 0 } )
    db.system.profile.find().pretty()

In the configuration file

    systemLog:
        path: "/var/mongodb/db/mongod.log"
        destination: "file"
        logAppend: true
    operationProfiling:
        mode: slowOp
        slowOpThresholdMs : 50

## Security

### Introduction

authentication - who are you

- SCRAM - password security
- X.509 - certificate based
- (Enterprise) LDAP
- (Enterprise) Kerberos
- (Cluster) Cluster Authentication

authorization - what can you do

- Role Based Access Control
  - Each user has one or more Roles
  - Each Role has one or more Privileges
  - Privilege is a group of Actions and the Resources they apply to

### Adding security to a new mongod

Enable RBAC on the cluster and implicitly enables authentication as well. Your instance is protected yet doesn't contain any users. The **localhost exception** allows you to access a server enforcing authentication without a configured user to authenticate with.

- Must run mongo shell from the same host running the mongod
- Localhost exception closes after first user is created
- Always create a user with admin privileges first

MongoD conf:

    security:
      authorization: enabled

Creating the first admin user:

    use admin
    db.createUser({
    user: "root",
    pwd: "root123",
    roles : [ "root" ]
    })

Connecting as the newly created user:

    mongo --username root --password root123 --authenticationDatabase admin

### Terminology

A _Role_ is composed of a set of _Privileges_ which are _Actions_ that can be performed over a _Resource_

#### Resources

    // specific database and collection
    { db : "products", collection: "inventory"}

    // all databases and all collections
    { db : "", collection: ""}

    // any database and specific collection
    { db : "", collection: "accounts"}

    // specific database and any collections
    { db : "products", collection: ""}

    // or cluster resource
    { cluster : true}

#### Privileges

A privilege is an action that can be performed over a resource.

    // allow to shutdown over the cluster
    { resource : { cluster : true }, actions : [ "shutdown" ] }

#### Role inheritance

A role can inherit from one or more other roles.

#### Network Authentication Restrictions

A role can be configured to allow only access from one or more particular client sources (_clientSource_) or to one or more articular server addresses (_serverAddress_).

### Built in roles

All roles defined here are per database level for each user. Different roles can be applied to different users on different databases. Exception to this rule is that Database user, database administration, super user are _all database roles_.

| Role                    | Desc                                                                            |
| ----------------------- | ------------------------------------------------------------------------------- |
| Database user           | read, readWrite                                                                 |
| Database administration | dbAdmin, userAdmin, dbOwner                                                     |
| All-Database roles      | readAnyDatabase, readWriteAnyDatabase, dbAdminAnyDatabase, userAdminAnyDatabase |
| Cluster administration  | clusterAdmin, clusterManager, clusterMonitor, hostManager                       |
| Backup/Restore          | backup, restore                                                                 |
| Super user              | root                                                                            |

| Role      | Operations detail                                                                                                                                        |
| --------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| userAdmin | changeCustomData, changePassword, createRole, createUser, dropRole, dropUser, grantRole, revokeRole, setAuthenticationRestriction, viewRole, viewUser... |
| dbAdmin   | collStats, dbHash, dbStats, killCursors, listIndexes, listCollections, bypassDocumentValidation, collMod, collStats, compact, convertToCapped...         |
| dbOwner   | _Combines_ **readWrite**, **dbAdmin**, **userAdmin** roles to allow the user to perform _any_ administrative action on the database.                     |

Create a security officer, only allowed to manage users, not data:

    db.createUser({
        user: "security_officer",
        pwd: "h3ll0th3r3",
        roles: [ { db: "admin", role: "userAdmin" } ]
    })

Create a database admin, only allowed to manage databases, not data:

    db.createUser({
        user: "dba",
        pwd: "secret",
        roles: [ { db: "admin", role: "dbAdmin" } ]
    })

Grant a role to a user:

    db.grantRolesToUser( "dba",  [ { db: "playground", role: "dbOwner"  } ] )

Show details on the roles and privileges:

    db.runCommand( { rolesInfo: { role: "dbOwner", db: "playground" }, showPrivileges: true} )

## Server Tools Overview

### mongostat

get quick stats on a running mongod process

    mongostat --port 30000

### mongodump and mongorestore

file import and export of a mongod collection **to/from BSON** along with its metadata

    mongodump --port 30000 --db applicationData --collection products
    mongorestore --drop --port 30000 dump/

### mongoexport and mongorestore

import and export to JSON or CSV of mongodb collections

    mongoexport --port 30000 --db applicationData --collection products -o products.json
    mongoimport --port 30000 products.json

    mongoimport --username m103-application-user --password m103-application-pass --authenticationDatabase admin --port 27000 -d applicationData -c products /dataset/products.json

## Replication

The concept of maintaining multiple copies of your data.

MongoDB uses the statement based replication mechanism.

- binary replication: specific byte changes on disk (and their location) are recorded and shared amongst the members
  - less data, faster
  - OS is consistent amongst all members
- statement based: all statements recorded in oplog, synced and replayed across members
  - not bound by OS, machine level, work on all architectures
  - transforms statements to idempotent ones e.g. $inc statements transformed to value statements

Replica set can contain up to 50 members.
Only 7 members can be voting.
The number of nodes need to be an odd number to allow the voting mechanism to work.

- **Non-voting** a node containing data yet without voting privileges
- **Arbiter** nodes can be added yet are discouraged, no data
- **Hidden** nodes can provide specific read-only workloads, copies of data hidden from your application. Must have priority set to 0
- **Delayed** nodes: specific hidden nodes which enable hot backups thus adding resilience without having to rely on cold backup nodes

[Read more on the RAFT protocol](http://thesecretlivesofdata.com/raft/)

### Setting up a MongoDB replica set

Configuration node1.conf:

    storage:
        dbPath: /var/mongodb/db/node1
    net:
        bindIp: 192.168.103.100,localhost
        port: 27011
    security:
        authorization: enabled
        keyFile: /var/mongodb/pki/m103-keyfile
    systemLog:
        destination: file
        path: /var/mongodb/db/node1/mongod.log
        logAppend: true
    processManagement:
        fork: true
    replication:
        replSetName: m103-example

Creating the keyfile, directories and other setup code

    sudo mkdir -p /var/mongodb/pki/
    sudo chown vagrant:vagrant /var/mongodb/pki/
    openssl rand -base64 741 > /var/mongodb/pki/m103-keyfile
    chmod 400 /var/mongodb/pki/m103-keyfile

    mkdir -p /var/mongodb/db/{node1, node2,node3}
    cp node1.conf node2.conf
    cp node1.conf node3.conf

    #adapt dbPath, port and systemlog parameters to unique value (node nr).

Run the 3 standalone mongod processes

    mongod -f node1.conf
    mongod -f node2.conf
    mongod -f node3.conf

#### How to initiate a replica set

    mongo --port 27011
    rs.initiate()

    use admin
    db.createUser({
        user: "m103-admin",
        pwd: "m103-pass",
        roles: [{role: "root", db: "admin"}]
    })

    exit
    mongo --host "m103-example/192.168.103.100:27011" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"

#### How to add nodes to a replica set

    rs.add("192.168.103.100:27012")
    rs.add("192.168.103.100:27013")

#### How to check the status of a replica set

Getting replica set status:

    rs.status()

Getting an overview of the replica set topology:

    rs.isMaster()

Stepping down the current primary, forcing an election:

    rs.stepDown()

Checking replica set overview after election:

    rs.isMaster()

### The Replication Configuration Document

A JSON object that defines the configuration options of our replica set. Can be configured manually from the shell. Helper methods exist. Document is shared across the set.

| Field       | Description                                                                                 |
| ----------- | ------------------------------------------------------------------------------------------- |
| \_id        | the replica set name, see --replSetName in mongo.conf                                       |
| version     | gets incremented each time the replica set changes (e.g. adding a member)                   |
| settings    | advanced configuration settings for the replica set                                         |
| members     | array containing the node members of the replica set                                        |
| \_id        | unique identifier for a node member within the set                                          |
| host        | hostname and port                                                                           |
| arbiterOnly | role of the node: no data, only arbiter                                                     |
| hidden      | role of the node: not visible to the application, used for non-operational (BI)             |
| priority    | number hierarchy 0-1-1000, higher priority, more chance to become primary. 0 never primary. |
| slaveDelay  | number in seconds, default 0, used for hot backups                                          |

### Replication commands

rs.status(): used to report on general health on each nodes. Data is get from heartbeats that get sent across.

rs.isMaster(): describes the role of the node on which this command is run

db.serverStatus()['repl']: similar to rs.isMaster yet adds the _rbid_ field. You can use this filed to evaluate the amount of rollbacks it lags or is before another node.

rs.printReplicationInfo(): returns oplog data relative to current node in _timings_. For actual oplog data, consult the oplog file itself.

rs.add: adds a new node to the replica set

rs.initiate: initiates the replica set mechanism before everything else

rs.remove: removes a node from the replica set

rs.config: returns a document containing the replica set configuration

### The oplog

In a standalone node, two default databases are created: _admin_ and _local_. The _admin_ database hosts (and runs) the administrative functions. The _local_ database stores the startup logs.

In a replica set, additional collections in the _local_ database are created.

    me
    oplog.rs <!--
    replset.election
    replset.minvalid
    replset.oplogTruncateAfterPoint
    startup_log
    system.replset
    system.rollback.id

oplog \.rs is the central point of replication mechanism. It keeps track of all statements being replicated in the replica set. All piece of information needs to be replicated are stored inside.

As operations are performed, the collection accumulates the statements. Once max is reached, the earliest is overwritten. The time between overwriting is the replication window. Important to monitor: impacts the time a node can be down without human intervention is needed. The secondary nodes apply the master oplog in their own oplog. Once a node gets down (network, system), the secondary keeps accumulating the writes.

In order to catch up, the server will need to decide a common point in the past by evaluating each others oplog. If it's not able to find one (already rotated), automatic recovery is not possible and the node will go into "maintenance" mode. The larger the oplog, the higher the replication window and better the chances of automated recovery.

One update statement may generate many entries in the log. In order to make the oplog immutable, update commands can be transformed to $set statements. As such:

    use m103
    db.messages.updateMany( {}, { $set: { author: 'norberto' } } )
    use local
    db.oplog.rs.find( { "ns": "m103.messages" } ).sort( { $natural: -1 } )
    {   "ts" : Timestamp(1534851637, 100),
        "t" : NumberLong(3),
        "h" : NumberLong("-6002197775968380802"),
        "v" : 2,
        "op" : "u",
        "ns" : "m103.messages",
        "ui" : UUID("46e5bb3b-b711-4371-ba6b-561f0ba94852"),
        "o2" : { "_id" : 99 },
        "wall" : ISODate("2018-08-21T11:40:37.789Z"),
        "o" : { "$v" : 1, "$set" : { "author" : "norberto" } }
    }

#### Configuring the oplog

The oplog is a capped collection, meaning it can grow to a pre-configured size before it starts to overwrite the oldest entries with newer ones. 5% of free disk is reserved for oplog max size.

    var stats = db.oplog.rs.stats() # or db.oplog.rs.stats(1024*1024) (in megabytes)
    stats.capped # Verifying that this collection is capped
    stats.size # Getting current size of the oplog (in bytes)
    stats.maxSize # Getting size limit of the oplog (in bytes)

It can be configured in the mongod configuration. To change the oplog size of a running replica set member, use the _replSetResizeOplog_ administrative command. _replSetResizeOplog_ enables you to resize the oplog dynamically without restarting the mongod process.

     replication.oplogSizeMB¶

#### Querying the oplog

Querying the oplog after connected to a replica set:

    use local
    db.oplog.rs.find()
    db.oplog.rs.find( { "o.msg": { $ne: "periodic noop" } } ).sort( { $natural: -1 } ).limit(1).pretty()
    db.oplog.rs.find({"ns": "m103.messages"}).sort({$natural: -1})

Getting the current status of the replication

    rs.printReplicationInfo()
        configured oplog size:   1677.328125MB
        log length start to end: 77888secs (21.64hrs)
        oplog first event time:  Mon Aug 20 2018 13:08:34 GMT+0000 (UTC)
        oplog last event time:   Tue Aug 21 2018 10:46:42 GMT+0000 (UTC)
        now:                     Tue Aug 21 2018 10:46:51 GMT+0000 (UTC)

Log length expressed as time: given the current workload, in what time will we begin overwriting the oplog.

#### One final thing about oplog and the local database

> Never write to the local database.

### Reconfiguring a running replica set

From the Mongo shell of the replica set, adding the new secondary and the new arbiter, removing the arbiter. These operations trigger changes to the replica set automatically.

    rs.add("m103.mongodb.university:27014")
    rs.addArb("m103.mongodb.university:28000")
    rs.remove("m103.mongodb.university:28000")

In order to modify the advanced configuration aspects (voting power, hidden) you must use the following logic and trigger a manual _reconfig_.

    cfg = rs.conf()
    cfg.members[3].votes = 0
    cfg.members[3].hidden = true
    cfg.members[3].priority = 0
    rs.reconfig(cfg)

### Reading and writing to a replica set

By default, you can only read and write from the master of a replica set. This is enforced in order to guarantee always seeing the most recent and consistent information.

Trying to read from a secondary results in

    "errmsg" : "not master and slaveOk=false",

To force reads from a secondary, apply this command on the secondary.

> Attention: this enables stale reads and non-consistent data

    rs.slaveOk()

Writing to a secondary _always_ results in an error message.

    "errmsg" : "not master",

When there is no longer a majority (e.g. only 1 node available), this node automatically steps down as a secondary. This is a fail safe mechanism, writes are no longer possible. The other nodes in the replica set need to be revived asap to enable strong consistency.

### Failover and elections

Primary node is the first point of contact for communicating with the database. In case of maintenance or outage, a new primary needs to be selected. This is done via the _election process_.

> Priority and recency of a node's oplog dictates which nodes are more likely to become primary.

_Elections_ take place when the topology changes. A node with the most recent copy of the information votes for itself. Other nodes support that vote and that node becomes the new primary.

> This is the main reason for having an odd number of nodes in a replica set: in case of an even number, the voting can result in a _tie_. That's not the end of the world, a new voting takes place, but can cause x amount of waiting time for the clients. If a majority of nodes are unavailable, elections cannot take place.

Forcing an election in this replica set, safely:

    rs.stepDown()

_Priority_ can be manually set to each node to influence the likelihood of the elections and favour nodes in becoming primary. You can also set the priority to 0 to disable the node from ever becoming a primary. The node still has voting power. Nodes with priority 0 are marked _passives_ in MongoDB.

Setting the priority of a node to 0, so it cannot become primary (making the node "passive"):

    cfg = rs.conf()
    cfg.members[2].priority = 0
    rs.reconfig(cfg)

## Write concerns, Read concerns and Read preferences
