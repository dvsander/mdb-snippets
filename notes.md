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
    - [Instering multiple documents](#instering-multiple-documents)
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
| int                   | BSON       |
| long                  | BSON       |
| decimal (since 3.4)   | BSON       |
| double                | BSON       |
| date                  | BSON       |
| ObjectId              | BSON       |
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

### Instering multiple documents

Ordered (standard): stops on error, e.g. duplicate key.  
Unordered: continue on error.

    [array of docs to insert], { "ordered" : false }
    db.moviesScratch.insertMany([array of docs to insert])

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

Default behaviour of querying _null_ is to exclude documents with key not set.

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

- Query Language: interaction with the database and applicationss through client side libraries MongoDB Drivers
- Document Data Model: management of namespecases, indexes, data structures, replication mechanism (writeconcern, readconcern)
- Storage layer: persistency later calls, system calls, disk flush, file structures, encryption/compression
- Security: user management, network, authorisation
- Admin: creating databases, logging infrastructure, management

Multiple MongoD can form a replica set. 1 node managing read/write (primary) and several others storing copies of the data. A failover protocol ensures in case of failover the system can elect a new master without downtime or loss of data. Replica sets can be deployed anywhere on different infrastructure.

MongoDB is a scalable database. Different mongods can scale horizontally through sharding. A mongoS is a shard routing component handling all operations to the shard cluster transparantly.

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
  - (dir) journal: writes are bufered in mem, flushed 60s, writeahead buffer entries every 50ms

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

authorisation - what can you do

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

All roles defined here are per database level for each user. Different roles can be applied to diferent users on different databases. Exception to this rule is that Database user, database administration, super user are _all database roles_.

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
| dbAdmin   | collStats, dbHash, dbStats, killCursors, listIndexes, listCollections, bypassDocumentValidation, collMod, collStats, compact, converToCapped...          |
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
        pwd: "c1lynd3rs",
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
