# MongoDB Technical Training notes

These are notes taken during technical training and experimenting with MongoDB.

<!-- TOC -->

- [MongoDB Technical Training notes](#mongodb-technical-training-notes)
  - [Connection Details](#connection-details)
  - [Data types](#data-types)
  - [Use cases](#use-cases)
    - [Navigating via command line](#navigating-via-command-line)
    - [Finding the distinct values for a particular field in a collection](#finding-the-distinct-values-for-a-particular-field-in-a-collection)
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
    - [Collation](#collation)
  - [Security](#security)
    - [Introduction to security](#introduction-to-security)
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
    - [Write concern](#write-concern)
    - [Read concerns](#read-concerns)
    - [Read preference](#read-preference)
  - [Sharding](#sharding)
    - [When to shard](#when-to-shard)
    - [Sharding Architecture](#sharding-architecture)
    - [Setting up a sharded cluster](#setting-up-a-sharded-cluster)
      - [Configuring the CSRS (Config Server Replica Set)](#configuring-the-csrs-config-server-replica-set)
      - [Pointing mongos to the CSRS](#pointing-mongos-to-the-csrs)
      - [Enable replica set to be a shard](#enable-replica-set-to-be-a-shard)
      - [A rolling upgrade of the existing cluster](#a-rolling-upgrade-of-the-existing-cluster)
    - [Config database](#config-database)
    - [Shard keys](#shard-keys)
      - [How to shard](#how-to-shard)
      - [Picking a good shard key](#picking-a-good-shard-key)
      - [Hashed shard keys](#hashed-shard-keys)
    - [Chunks](#chunks)
    - [Balancing](#balancing)
    - [Queries in a sharded cluster](#queries-in-a-sharded-cluster)
    - [Targeted/Routed versus scatter-gather queries](#targetedrouted-versus-scatter-gather-queries)
    - [Detecting scatter-gather queries](#detecting-scatter-gather-queries)
  - [Indexes](#indexes)
    - [What are indexes](#what-are-indexes)
    - [Types of indexes](#types-of-indexes)
      - [Single field indexes](#single-field-indexes)
      - [Compound indexes](#compound-indexes)
      - [Multi-key indexes](#multi-key-indexes)
      - [Partial indexes](#partial-indexes)
      - [Sparse indexes](#sparse-indexes)
    - [Sorting with indexes](#sorting-with-indexes)
    - [Index operations](#index-operations)
    - [Query plans](#query-plans)
    - [Explain plans](#explain-plans)
    - [Resource allocation for indexes](#resource-allocation-for-indexes)
  - [MongoDB Performance](#mongodb-performance)
    - [How data is stored on disk](#how-data-is-stored-on-disk)
      - [Physical files](#physical-files)
      - [Journaling](#journaling)
    - [Basic benchmarking](#basic-benchmarking)
    - [Optimizing CRUD Operations](#optimizing-crud-operations)
    - [Covered Queries](#covered-queries)
    - [Insert performance](#insert-performance)
    - [Different data type implications](#different-data-type-implications)
    - [Performance considerations in distributed systems](#performance-considerations-in-distributed-systems)
  - [Extra: MongoDB World '17: Sizing MongoDB clusters](#extra-mongodb-world-17-sizing-mongodb-clusters)
    - [The sizing process](#the-sizing-process)
    - [Estimating IOPS](#estimating-iops)
    - [Estimating data size](#estimating-data-size)
    - [Estimating the working set](#estimating-the-working-set)
    - [Estimating the CPU](#estimating-the-cpu)
    - [Estimating the need for sharding](#estimating-the-need-for-sharding)

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
    db.getSiblingDB("m103") - alternative to use <database> in order to not set the global db variable in the shell
    show collections
    db.collection.find().pretty()
    db.stats()

### Finding the distinct values for a particular field in a collection

Returns an array, e.g. [ "Bundle", "Movie", "Music", "Software" ]

    db.products.distinct("type")

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

### Collation

Allow users to specify language specific rules for string comparison (letters, case, accent marks). The offer correctness on query and data level, have marginal performance impact and allow for case insensitive queries. A collation can be defined on the following levels:

- Collection level: all queries and indexes
- Index level
- Query level
  - Need to match the index by using the same collation!

Setting the collation on a collection:

    db.createCollection( "foreign_text", {collation: {locale: "pt"}})

## Security

### Introduction to security

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

### Write concern

Writeconcern is an acknowledgment mechanism that developers can add to write operations. A higher levels of acknowledgement produces a stronger durability guarantee. Durability means that the write has propagated to the number of replica set member nodes specified in the write concern. The more replica set members that acknowledge the success of a write, the more likely the write is durable in the event of a failure.

When a writeConcernError occurs, the document is still written to the healthy nodes. The WriteResult object simply tells us whether the writeConcern was successful or not - it will not undo successful writes from any of the nodes.

The unhealthy node will have the inserted document when it is brought back online. When the unhealthy node comes back online, it rejoins the replica set and its oplog is compared with the other nodes' oplog. Any missing operations will be replayed on the newly healthy node.

> The tradeoff is time. The client has to to wait for the acknowledgments.

| Level       | Description                                               |
| ----------- | --------------------------------------------------------- |
| 0           | No acknowledgement. Fire and forget.                      |
| 1 (default) | Acknowledgement from primary.                             |
| >= 2        | Acknowledgement from primary and one or more secondaries. |
| "majority"  | Acknowledgement from a majority, defined as (nodes/2)+1.  |

Options:

- wtimeout : < int > - the time to wait for the requested write concern before marking the operation as failed
  - If wtimeout is not specified, the write operation will be retried for an indefinite amount of time until the writeConcern is successful. If the writeConcern is impossible, like in this example, it may never return anything to the client.
- j : <true/false> - requires the node to commit the write operation to the journal before returning an acknowledgement
  - Implied with "majority" concern
  - Advantage to have an even higher guarantee changes are stored on disk. Disabling journaling or setting this to false acknowledges writes in memory

### Read concerns

Developers can direct their application with a readconcern to read documents with a certain durability guarantee. It serves as a companion to writeconcern.

In a rare circumstance where an application inserts data with a low level of writeconcern, data can be read from the primary without it being acknowledged by the secondaries. In case of a system failure, re-election and rollback of the (previous) primary, this can lead to stale data being read. This is why read concerns exist.

| Level                        | Description                                                                                    |
| ---------------------------- | ---------------------------------------------------------------------------------------------- |
| local (default)              | The most recent data in the cluster, read from primary. No guarantees.                         |
| available (sharded clusters) | Same as local for replica sets. Reads against secondary members.                               |
| majority                     | Acknowledgement as written to a majority, defined as (nodes/2)+1. Not freshest or latest data. |
| linearizable                 | Acknowledgement as written to a majority and read your own write.                              |

As general guideline the following is true:

| Fast | Latest | Safe | Strategy         | Consequence                                  |
| ---- | ------ | ---- | ---------------- | -------------------------------------------- |
| X    | X      |      | local, available | no durability guarantee                      |
| X    |        | X    | majority         | not latest written data                      |
|      | X      | X    | linearizable     | reads may be slower and single document only |

### Read preference

Allows applications to route read operations to specific members of a replica set. It's a driver side setting.

By default, applications read/write to the primary of a replica set. _Eventually_ the data in the secondaries catch up with the most recent data in the primary through the replica mechanism.

The following read preferences can be configured in your application (driver) settings:

| Level              | Description                               | Tradeoff                    |
| ------------------ | ----------------------------------------- | --------------------------- |
| primary (default)  | read from primary only                    | secondaries are for HA only |
| primaryPreferred   | if primary unavailable, read secondary    | possible to read stale data |
| secondary          | read from secondary only                  | possible to read stale data |
| secondaryPreferred | if secondary unavailable, read primary    | possible to read stale data |
| nearest            | least latency to the host (geo efficient) | possible to read stale data |

> The staleness entirely depends on how much delay there is between primary and secondary nodes.

Setting the read preference in mongo shell:

    db.getMongo().setReadPref('primaryPreferred')

## Sharding

Entire datasets can be stored on one server. Replication makes sure the data is highly available. When data grows you can grow _vertically_ by adding more resources. At MongoDB scaling is done _horizontally_ by adding more machines and distributing the data. We call this a _sharded replica set_ or _sharded cluster_.

In between the sharded cluster and the clients, the _mongos_ acts as a router process. It uses metadata about the data on each shard and stores it on a config server. In order to make the config server HA we use a config replica set.

- Shards: store distributed collections
- Config server: stores the metadata about each shard
- Mongos: routes the queries to the correct shards.

### When to shard

Is it economically viable and possible to vertically scale? Can we keep adding resources in order to scale up?

Rationale:

- Performance: jumping from 100$/h to 300$/h machines does not guarantee 3x the performance.
- Operations: increasing disk size (e.g. 1TB disks to 20TB disks at 75% usage) causes 15x more data to backup, 15x time required to restore and sync. Impact on operations, network.
- Workload: 15x data causes 15x larger indexes, needs more RAM to be efficient

With large amounts of data horizontal scaling by sharding can help by

- Performance: Single-thread operations such as aggregation commands, geographical distributed data use cases all benefit from being collocated and thus sharded.
- Operations: parallelization of backup, restore, sync processes
- Workload: Generally, when our deployment reaches 2-5TB per server, we should consider sharding
- Business: Sharding allows us to store different pieces of data in specific countries or regions

### Sharding Architecture

A virtually unlimited number of shards can be created and managed by a mongos. Clients communicate with the mongos, which communicates to the shards in a cluster - this includes the primary shard.

Each database in the sharded cluster set will be assigned a primary shard. The primary shard may have more data, because non-sharded collections will only exist on the primary shard. But this is not necessarily the case. We can manually change the primary shard of a database, if we need to.

Mongos responsibilities:

- Routing queries to one, many or all shards
- Gathering, organizing, possibly sorting results when documents are fetched from multiple shards in a shard_merge, transparently for the user
- Managing sharding configuration in the config server
- Balancing workload on each shard by moving around data when needed (e.g. "Smith" family name)
- Splitting into chunks (see later)

### Setting up a sharded cluster

#### Configuring the CSRS (Config Server Replica Set)

Create 3 node replica set. The **sharding.clusterRole = "configsvr"** marks all nodes as config servers. csrs_1.conf file contents:

    sharding:
        clusterRole: configsvr
    replication:
        replSetName: m103-csrs
    security:
        keyFile: /var/mongodb/pki/m103-keyfile
    net:
        bindIp: localhost,192.168.103.100
        port: 26001
    systemLog:
        destination: file
        path: /var/mongodb/db/csrs1.log
        logAppend: true
    processManagement:
        fork: true
    storage:
        dbPath: /var/mongodb/db/csrs1

Follow the standard procedure of creating a replica set, repeated below:

    mkdir /var/mongodb/db/{csrs1,csrs2,csrs3}
    mongod -f csrs_1.conf
    mongod -f csrs_2.conf
    mongod -f csrs_3.conf

    mongo --port 26001
    rs.initiate()

    use admin
    db.createUser({
        user: "m103-admin",
        pwd: "m103-pass",
        roles: [{role: "root", db: "admin"}]
    })

    db.auth("m103-admin", "m103-pass")
    rs.add("192.168.103.100:26002")
    rs.add("192.168.103.100:26003")

#### Pointing mongos to the CSRS

There is no _storage.dbPath_ value in the configuration file. Mongos has no database file itself and uses the CSRS to connect and store all information, including any users created on the config server.

mongos.conf:

    sharding:
        configDB: m103-csrs/192.168.103.100:26001,192.168.103.100:26002,192.168.103.100:26003
    security:
        keyFile: /var/mongodb/pki/m103-keyfile
    net:
        bindIp: localhost,192.168.103.100
        port: 26000
    systemLog:
        destination: file
        path: /var/mongodb/db/mongos.log
        logAppend: true
    processManagement:
        fork: true

Start the _mongos_ process. You can connect to the mongos process using the same mongo shell.

    mongos -f mongos.conf
    mongo --host "192.168.103.100:26000" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    sh.status()

    --- Sharding Status ---
    sharding version: {
        "_id" : 1,
        "minCompatibleVersion" : 5,
        "currentVersion" : 6,
        "clusterId" : ObjectId("5b7d6f34cefbf7876b7cf7b0")
    }
    shards:
    active mongoses:
            "3.6.6" : 1
    autosplit:
            Currently enabled: yes
    balancer:
            Currently enabled:  yes
            Currently running:  no
            Failed balancer rounds in last 5 attempts:  0
            Migration Results for the last 24 hours:
                    No recent migrations
    databases:
            {  "_id" : "config",  "primary" : "config",  "partitioned" : true }

The mongos is active, yet has no knowledge of any shards.

#### Enable replica set to be a shard

Reconfigure the nodes in your replica set with the following lines added to each of their config files:

    sharding:
    clusterRole: shardsvr
    storage:
    wiredTiger:
        engineConfig:
            cacheSizeGB: .1

The clusterRole: shardsvr section tells mongod that the node can be used in a sharded cluster.

The cacheSizeGB: .1 section restricts the memory usage of each running mongod. Note that this is not good practice. However, in order to run a sharded cluster inside a virtual machine with only 2GB of memory, certain adjustments must be made.

#### A rolling upgrade of the existing cluster

Restart both secondaries one at a time with the new configuration, watch how the cluster elects new primaries.

    mongo --port 27012 -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    use admin
    db.shutdownServer()
    mongod -f node2.conf

Connect to the primary and step it down

    mongo --port 27011 -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    rs.stepDown()
    use admin
    db.shutdownServer()
    mongod -f node1.conf

Sharding is now enabled on this replica set. Connect to mongos and add the replica set.

    mongo --host "192.168.103.100:26000" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    sh.addShard("m103-repl/192.168.103.100:27012")
    sh.status()

### Config database

> The config database is used and maintained internally by MongoDB, and you shouldn't write to it unless directed to by MongoDB Documentation or Support Engineers.

It has useful information in its collections to _read_.

Aggregate view on the config db is returned from the sh.status() command

    sh.status()

config.databases: return each database as one document, the primary shard and the partition key.

    db.databases.find().pretty()

config.collections: only collections which are sharded. Shows the key and whether that key is unique.

    db.collections.find().pretty()

config.shards: the shards in the cluster with the replica set names and hosts

    db.shards.find().pretty()

config.chunks: each chunk for each collection as one document, the range (inclMin and exclMax) on which data is chunked into the shards.

    db.chunks.find().pretty()

config.mongos: all mongos processes connected to this sharded replica set

    db.mongos.find().pretty()

### Shard keys

Shard keys are the indexed field(s) that MongoDB uses to partition data in a sharded collection and distribute it amongst the shards in your clusters. The logical groupings that are distributed are referred to as _chunks_.

Every time you write a new doc to a collection the mongos checks which shard contains the appropriate chunk for that particular key value and routes the document to that shard only. Depending on what shard is holding a given chunk , a new doc is routed to that shard and only that shard.

Shard keys also support distributed read operations. If the shard key is part of your query, mongos can direct your query to only the chunks, and therefore shards, that contain the related data. Ideally your shard keys should support the majority of queries you run on the collection. That way the majority of read operations can be targeted to a single shard. Without the shard key in the query, mongo will have to check each shard in the cluster to match the query. This is referred to as targeted vs broadcast read operations.

> The shard key must be present in every document in the collection and every document inserted.

Shard keys are

- **Indexed**: must be indexed first, before you can select the indexed fields for your shard key
- **Permanent** you cannot unshard a collection
- **Immutable**: shard keys are immutable. You cannot update the shard key of a collection nor update the values of the shard key for any document in the sharded collection

#### How to shard

Enable sharding for a particular database. Does not automatically shard but marks the collections inside as sharding eligible:

    sh.enableSharding("m103")

Decide on which key to use for sharding and create an index:

    db.products.createIndex( { "sku" : 1 } )

Enable sharding for the collection:

    sh.shardCollection("m103.products", {"sku" : 1 } )

#### Picking a good shard key

Collections can be sharded, not databases. The goal is to provide _good write distribution_ and _read isolation_:

- **High Cardinality**: High cardinality provides more shard key values, which determines the maximum number of chunks the balancer can create. e.g. gender: 3 possible shards (M/F/U), day of the week: 7 possible shards, state: 50 possible shards.
- **Low Frequency**: low repetition of a given unique shard value. High frequency means that most documents fall within a particular range. If the majority of documents contain only a subset of the possible shard key values, then the chunks storing those documents become a bottleneck within the cluster. e.g. states: 50 possibilities is a good choice in a nation-wide application with evenly distributed amount of users and interactions, not if used in 1 or a couple of states.
- **Non-monotonically change**: If the shard key value monotonically increased, all new inserts would be routed to the chunk with maxKey as the upper bound. e.g. timestamp or \_id.

> Sharding is final. Test your shard keys in staging environment first.

#### Hashed shard keys

A shard key where the underlying index is hashed, a special type of shard key.

> Hashed shard keys circumvent the _hotzoning_ problem when using monotonically changing shard keys as hashed values provide an even distribution of data.

Consequences:

- Queries on ranges of shard key values are more likely to be scatter-gather
- Cannot support geographically isolated read operations using zoned sharding
- Hashed Index must be on a single non-array-field
- Sorts are slower on a hashed index than a normal index

Usage:

    sh.enableSharding("<database>")
    db.collection.createIndex({"<field>" : "hashed"})
    sh.shardCollection({"<database>.<collection>", { <shard_key_field> : "hashed" }})

### Chunks

Config servers hold the cluster metadata: how many shards, what databases are sharded, configuration settings... and _the mapping of chunks to shards_.

When sharding is activated on a collection, one initial chunk is defined: minKeyIncl to maxKeyExcl. The different values will determine the key space of the sharding collection. As time progresses, the initial chunk is split in different chunks to be distributed evenly.

Chunks

- are logical groups of documents
- are defined by a minKey (inclusive) and maxKey (exclusive)
- can only live in one designated shard at a time
- are determined at runtime by mongos by
  - the chunk size: default 64MB, can be between 1MB-1024MB. Once a chunk reaches this size, it will be split.
  - the detection of jumbo chunks: a shift in key values frequency can cause relatively more data to enter with the same shard key. This generates an abnormal situation called jumbo chunks. Jumbo chunks
    - are larger than the maximum chunk size
    - cannot be migrated to another shard. Once marked as jumbo, the balancer skips these
    - will not be able to split in some cases when larger than max chunk size
    - can be avoided by raising the max chunk size

Change the chunk size:

    use config
    db.settings.save({_id: "chunksize", value: 2})

> Changing the chunkSize does not trigger mongos to start distributing the data. This will be activated once new data is inserted.

Showing details of all chunks:

    use config
    db.chunks.find().pretty()

The sharding status indicates the balancing of chunks.

    sh.status()

    --- Sharding Status ---
    ...
    databases:
            {  "_id" : "m103",  "primary" : "m103-repl",  "partitioned" : true }
                    m103.products
                            shard key: { "name" : 1 }
                            unique: false
                            balancing: true
                            chunks:
                                    m103-repl   2
                                    m103-repl-2 1
                            { "name" : { "$minKey" : 1 } } -->> { "name" : "In Meiner Mitte - CD" } on : m103-repl-2 Timestamp(2, 0)
                            { "name" : "In Meiner Mitte - CD" } -->> { "name" : "Tha Shiznit: Episode 1 - CD" } on : m103-repl Timestamp(2, 1)
                            { "name" : "Tha Shiznit: Episode 1 - CD" } -->> { "name" : { "$maxKey" : 1 } } on : m103-repl Timestamp(1, 2)

Finding the chunk document based on the shard key, in this case '21572585':

    db.getSiblingDB("config").chunks.find({
        "ns" : "m103.products",
        $expr: {
            $and : [
            {$gte : [ 21572585, "$min.sku"]},
            {$lt : [21572585, "$max.sku"]}
            ]
        }
    })

### Balancing

As data grows in a particular shard, so does the number of chunks. A well defined shard key should contribute to MongoDB evenly distributing data across the shards.

Showing detailed balancing information of a collection:

    db.people.getShardDistribution()

The balancer process:

- is responsible for evenly distributing chunks across the sharded cluster
- runs on the Primary member of the config server replica set
- is an automatic process and requires minimal user configuration
- migrates chunks in parallel where one shard can only participate in 1 migration at any time

Start the balancer - the interval delays the next balancing round

    sh.startBalancer(timeout, interval)

Stop the balancer - this will only stop after the current round ends

    sh.stopBalancer(timeout, interval)

Enable/disable the balancer:

    sh.setBalancerState(boolean)

### Queries in a sharded cluster

All queries in a sharded cluster need to be run on _mongos_. Mongos decides based on the shard mapping to which node(s) to direct the query. The mongos is responsible for merging the results of a standard find operation.

As a result, we can have very efficient queries on sharded clusters called _targeted queries_ when the query includes the shard key. _Scatter-Gather queries_ collect results from 2 or more shards when the query does not include the shard key or a range on the shard key, which could be less efficient.

_sort()_: mongos pushes the sort to each shard and merge-sort the results

_limit()_: mongos passes limit to each shard, then re-applies the limit to the merged set of results

_skip()_: mongos performs the skip against the merged set of results

### Targeted/Routed versus scatter-gather queries

- Targeted/routed queries are very efficient and require the shard key in the query.
  - Can skip post merge/sort phase of mongos
  - No network latency, waiting for multiple shards
- Ranged queries on the shard key however may still require targeting every shard in the cluster

> Pay special attention to _hashed keys_ that in range queries are extremely likely to result a scatter-gather query, and compound keys that require all fields in the compound key in order to be targeted

### Detecting scatter-gather queries

The global _winningPlan.stage = SINGLE_SHARD_ indicates the entire query was resolved by one shard, without needing post processing on mongos. The shard details show the SHARDING_FILTER was able to use a very efficient index IXSCAN to look up the results.

    MongoDB Enterprise mongos> db.products.find({"sku" : 1000000749 }).explain()
    {
        "queryPlanner" : {
            "mongosPlannerVersion" : 1,
            "winningPlan" : {
                "stage" : "SINGLE_SHARD",
                "shards" : [
                    {
                        "shardName" : "m103-repl",
                        ...
                        "winningPlan" : {
                            "stage" : "FETCH",
                            "inputStage" : {
                                "stage" : "SHARDING_FILTER",
                                "inputStage" : {
                                    "stage" : "IXSCAN",

The global _winningPlan.stage = SHARD_MERGE_ indicates the query had to be processed on multiple shards and merged by mongos. Additionally, on each shard that was targeted the SHARDING_FILTER had to perform a COLLSCAN, an entire lookup of the collection data without using an index.

    MongoDB Enterprise mongos> db.products.find( { "name" : "Gods And Heroes: Rome Rising - Windows [Digital Download]" } ).explain()
    {
        "queryPlanner" : {
            "mongosPlannerVersion" : 1,
            "winningPlan" : {
                "stage" : "SHARD_MERGE",
                "shards" : [
                    {
                        "shardName" : "m103-repl",
                        ...
                        "winningPlan" : {
                            "stage" : "SHARDING_FILTER",
                            "inputStage" : {
                                "stage" : "COLLSCAN",
                             ...
                    },
                    {
                        "shardName" : "m103-repl-2",
                        ...
                        "winningPlan" : {
                            "stage" : "SHARDING_FILTER",
                            "inputStage" : {
                                "stage" : "COLLSCAN",
                                ...

## Indexes

You can learn more about indexes by visiting the [Indexes Section of the MongoDB Manual](https://docs.mongodb.com/manual/indexes). Import the people.json dataset into the m201.people collection:

    mongoimport --username m103-admin --password m103-pass --authenticationDatabase admin --port 26000 -d m201 -c people /shared/people.json

### What are indexes

Indexes try to solve the problem of slow queries. Similar to the index section in many books. Indexes support the efficient execution of queries in MongoDB. Without indexes, MongoDB must perform a collection scan, i.e. scan every document in a collection, to select those documents that match the query statement. If an appropriate index exists for a query, MongoDB can use the index to limit the number of documents it must inspect.

The strategy becomes to use indexes to improve performance for common queries. Build indexes on fields that appear often in queries and for all operations that return sorted results. MongoDB automatically creates a unique index on the \_id field. Although indexes can improve query performances, indexes also present some operational considerations. As you create indexes, consider the following behaviors of indexes:

- Collections with high read-to-write ratio often benefit from additional indexes. Indexes do not affect un-indexed read operations.
- For collections with high write-to-read ratio, indexes are expensive since each insert/delete/update must also update any indexes.
- When active, each index consumes disk space and memory. This usage can be significant and should be tracked for capacity planning, especially for concerns over working set size. Each index requires at least 8 kB of data space.

### Types of indexes

#### Single field indexes

    db.people.createIndex( { <field> : <direction> } )

Key features

- Keys from only one field
- Can find a single value for the indexed field
  - in multi-field queries the indexed field is prioritized to filter first, then match other predicates
- Can find a range of values
  - $gte $gt $... operators
  - list of values
- Can use dot notation to index fields in subdocuments
- Can be used to find several distinct values in a single query

Showing the explain plan on a default, non-indexed query, leaving the full output in this document. You can clearly see a COLLSCAN has taken place, a one-by-one document evaluation which is of linear performance (more docs = more processing).

> The ratio of nReturned:totalDocsExamined, 1:50474, is a strong indicator of a possible slow/inefficient query

    db.people.find({ "ssn" : "720-38-5636" }).explain("executionStats")
    "executionStats" : {
            "nReturned" : 1,
            "executionTimeMillis" : 44,
            "totalKeysExamined" : 0,
            "totalDocsExamined" : 50474,
            "executionStages" : {
                "stage" : "SINGLE_SHARD",
                "nReturned" : 1,
                "executionTimeMillis" : 44,
                "totalKeysExamined" : 0,
                "totalDocsExamined" : 50474,
                "totalChildMillis" : NumberLong(38),
                "shards" : [
                    {
                        "shardName" : "m103-repl-2",
                        "executionSuccess" : true,
                        "executionStages" : {
                            "stage" : "COLLSCAN",
                            "filter" : {
                                "ssn" : {
                                    "$eq" : "720-38-5636"
                                }
                            },
                            "nReturned" : 1,
                            "executionTimeMillisEstimate" : 30,
                            "works" : 50476,
                            "advanced" : 1,
                            "needTime" : 50474,
                            "needYield" : 0,
                            "saveState" : 394,
                            "restoreState" : 394,
                            "isEOF" : 1,
                            "invalidates" : 0,
                            "direction" : "forward",
                            "docsExamined" : 50474
                        }
                    }
                ]
            }
        },

Creating an _ascending_ index

    db.people.createIndex( { ssn : 1 } )
    exp = db.people.explain("executionStats")

Executing the same query again shows a 1:1 ratio of documents returned and documents examines, perfect! You can also see detail on the IXSCAN, the usage of an index.

    exp.find( { "ssn" : "720-38-5636" } )
    "executionStages" : {
        "stage" : "FETCH",
        "nReturned" : 1,
        "docsExamined" : 1,
        "inputStage" : {
            "stage" : "IXSCAN",

Creating an index on a field in an embedded document

    db.examples.insertOne( { \_id : 0, subdoc : { indexedField: "value", otherField : "value" } } )
    db.examples.insertOne( { \_id : 1, subdoc : { indexedField : "wrongValue", otherField : "value" } } )

    db.examples.createIndex( { "subdoc.indexedField" : 1 } )
    db.examples.explain("executionStats").find( { "subdoc.indexedField" : "value" } )

#### Compound indexes

A compound index is an index on 2 or more fields. The order of fields listed in a compound index has significance. For instance, if a compound index consists of _{ userid: 1, score: -1 }_, the index sorts first by _userid_ and then, within each _userid_ value, sorts by _score_.

    db.collection.createIndex( { <field1>: <type>, <field2>: <type2>, ... } )

Index prefixes are the beginning subsets of indexed fields. For example, consider the following compound index:

    { "item": 1, "location": 1, "stock": 1 }

The index has the following index prefixes:

    { item: 1 }
    { item: 1, location: 1 }

For a compound index, MongoDB

- can use the index to support queries on the index prefixes
  - the item field,
  - the item field and the location field,
  - the item field and the location field and the stock field.
- can use the index to support a query on item and stock fields since item field corresponds to a prefix. However, the index would not be as efficient in supporting the query as would be an index on only item and stock.
- cannot use the index to support queries that include the following fields since without the item field, none of the listed fields correspond to a prefix index:
  - the location field,
  - the stock field, or
  - the location and stock fields.

If you have a collection that has both a compound index and an index on its prefix (e.g. { a: 1, b: 1 } and { a: 1 }), if neither index has a sparse or unique constraint, then you can remove the index on the prefix (e.g. { a: 1 }). MongoDB will use the compound index in all of the situations that it would have used the prefix index.

#### Multi-key indexes

A multi-key index is an index on an array field. For each entry in the array, a separate index key is created per entry. Watch out for the following:

- A compound index can only contain **1** array field
- Arrays with lots of elements cause the index to grow very big over time
- No support for covered queries

#### Partial indexes

Partial indexes only index a portion of a collection. This can be useful when the index has grown too large. Can be useful in combination with multi-key indexes.

When 90% of the time users query restaurants with ratings $gte 3.5, you could use the following spatial index:

    db.restaurants.createIndex(
     { "address.city": 1, cuisine: 1 },
     { partialFilterExpression: { 'stars': { $gte: 3.5 } } })

> In order to trigger the index, the filter predicate must match the expression

#### Sparse indexes

A special case of a partial index: only index documents where the index field is present. The following indexes have the same behavior:

    db.restaurants.createIndex(
        { stars: 1},
        { sparse: true }
    )

    db.restaurants.createIndex(
        { stars : 1},
        { partialFilterExpression : { 'stars' : { $exists : true } } }
    )

> Partial indexes are more verbose and expressive and are therefore recommended instead of parse indexes.

### Sorting with indexes

Documents are stored on disk in a random order. When asked MongoDB to return sorted documents, it can use

- In memory sorting: the default in memory sorting looks at all the documents, sorts them in memory.
- Index sorting: the documents will be fetched from the server using the index.

Sorting with compound keys

- Sort queries by using index prefixes in sort predicates
- Filter _and_ sort queries by splitting the index prefix between query and sort predicates
- Sort documents with an index if the sort predicate _inverts_ the index keys or their prefixes

The executionStats show the ratio nReturned:totalDocsExamined is at first glance not performing, yet the ratio totalKeysExamined indicates the cursor was used. It shows the index was not used for filtering but for sorting.

    var exp = db.people.explain('executionStats')
    exp.find({}, { _id : 0, last_name: 1, first_name: 1, ssn: 1 }).sort({ ssn: 1 })
    "executionStats" : {
        "nReturned" : 50474,
        "executionTimeMillis" : 202,
        "totalKeysExamined" : 50474,
        "totalDocsExamined" : 50474,

The index can also be used in backward mode

    exp.find({}, { _id : 0, last_name: 1, first_name: 1, ssn: 1 }).sort({ ssn: -1 })

And for filtering and sorting in the same query (both using the index, backward)

    exp.find( { ssn : /^555/ }, { _id : 0, last_name: 1, first_name: 1, ssn: 1 } ).sort( { ssn : -1 } )

With compound indexes this is very similar

    // create a new compound index
    db.coll.createIndex({ a: 1, b: -1, c: 1 })

    // walk the index forward
    db.coll.find().sort({ a: 1, b: -1, c: 1 })

    // walk the index backward, by inverting the sort predicate
    db.coll.find().sort({ a: -1, b: 1, c: -1 })

    // all of these queries use the index for sorting
    db.coll.find().sort({ a: 1 })
    db.coll.find().sort({ a: 1, b: -1 })
    db.coll.find().sort({ a: -1 })
    db.coll.find().sort({ a: -1, b: 1 })

### Index operations

MongoDB can create indexes in the _foreground_ or in the _background_.

- _Foreground_ index creation (default) is fast, but block operations to the database. It will block _all_ incoming operations to the database containing the database: the database is not available to application reads or writes.

- _Background_ index creation is slower, but don't block operations to the database. They impact the query performance of the MongoDB deployment while running.

> Foreground indexes should be used in maintenance windows or with applications with less read/write operations. In all other production environments and situations, using background index creation is recommended.

Creating a background index:

    db.restaurants.createIndex( {"cuisine": 1, "name": 1, "address.zipcode": 1}, {"background": true} )

Starting the command blocks the terminal until the command has executed successfully. Index creation itself is a background process so we can still log in to the database in another shell. To view the status of the index creation, execute db.currentOp() with a filter on index creation.

    db.currentOp({
        $or: [
        { op: "command", "query.createIndexes": { $exists: true } },
        { op: "insert", ns: /\.system\.indexes\b/ }
        ]
    })

    // you can kill the process with the killOp() command
    db.killOp(12345)

### Query plans

MongoDB has an empirical query optimizer where query plans are ran against each other during a trial period. The MongoDB query optimizer processes queries and chooses the most efficient query plan for a query given the available indexes. The query system then uses this query plan each time the query runs.

Query plans are cached so that plans do not need to be generated and compared against each other every time a query is executed.

Query plans are evicted when

- Restart of the server
- Workload of the first portion of the query is 10x faster than the winning plan
- Index is rebuilt
- Index is created/dropped

### Explain plans

The best way to analyse what's happening when a query gets executed:

- Is your query using the index you expect? The direction the index is used, the bounds of the values looked at and the number of keys examined?
- Is your query using an index to provide a sort? If a sort was performed by walking the index or done in memory?
- Is your query using an index to provide the projection?
- How selective is your index?
- Which part of your plan is the most expensive? All the different stages the query needs to go through with details about the time it takes, the number of documents processed and returned to the next stage in the pipeline?

**Theoretical** (default): without executing the query itself, simulate the queryPlan. This is a very powerful method of ?testing different queries. This is also the default.

    db.products.explain("queryPlanner").find({"sku": 23153496})

**Empirical**: execute the query and return statistics

    db.products.explain("executionStats").find({"sku": 23153496})
    db.products.explain("executionStats").find({"shippingWeight": 1.00})

**Verbose**: execute the query, return statistics and show alternative plans that were considered

    db.products.explain("allPlansExecution").find({"sku": 23153496})

Here are a few definitions regarding the output of explain():

- SHARDING_FILTER: The step performed by mongos used to make sure that documents fetched from a particular shard are supposed to be from that shard. To do this, mongos compares the shard key of the document with the metadata on the config servers.
- IXSCAN: An index scan, used to scan through index keys.
- FETCH: A document fetch, used to retrieve an entire document because one or more of the fields is necessary.

> The root node is the final stage from which MongoDB derives the result set.

You can find more information about explain() in the [official MongoDB documentation](https://docs.mongodb.com/manual/reference/explain-results/).

### Resource allocation for indexes

Getting physical info from disk:

    free -h

Getting information on index size, and a particular index

    db.stats()
    db.collection.stats()
    stats.indexDetails

Indexes use two resources: _disk_ and _memory_ When restrained on disk size, the index is not created at all. You will run out of data for collection data before running out of space for indexes. You can dedicate a disk for index data (see previous chapter). Deployments should also be sized in order to have all indexes on RAM. Indexes are not required to be entirely placed in RAM, however performance will be affected by constant disk access to retrieve index information.

Reporting could be an edge case as BI-queries typically are specialized queries. Indexing large queries can grow big for memory. They possibly don't need to reside on all nodes. Could be an index on one node, which could even be hidden and dedicated for BI.

## MongoDB Performance

Von Neumann Architecture considerations and advise for MongoDB

- Memory: 25x times faster than SSD's, has become commodity and cheaper than ever
  - Used for
    - Aggregation
    - Index Traversing
    - Write Operations
    - Query Engine
    - Connections
  - Considerations
    - MongoDB performs critical system operations in memory
    - In addition, a specialized all-in-memory configuration exists where all data is stored in memory
  - Advise: MongoDB greatly benefits from more memory
- CPU: MongoDB will by default try use all cores to respond to connections
  - Used for
    - Storage Engine
    - Concurrency Model
    - Page compression
    - Data calculation
    - Aggregation Framework Operations
    - Map Reduce
  - Considerations
    - MongoDB has a document based concurrency model.
    - Parallel actions on the same document can not benefit from multiple cores.
    - In a highly concurrent system, MongoDB (especially with WiredTiger) greatly benefits from more cores
  - Advise: MongoDB greatly benefits from more cores
- IO:
  - Used for
    - replica sets, sharding
    - client/mongo(s/d) communication
    - durability on disk
    - communication between CPU-Memory-Disk
  - Considerations
    - Disk
      - The amount of IOPS is a big performance indicator: the more IOPS the faster, and thus more, random reads and writes can be done to disk.
      - More disks benefit the architecture because the IO load of different databases, indexes, journaling, lock files, are distributed over different disks, thus increasing overall database performance.
      - When using multiple disks, RAID-setup is frequently configured. MongoDB compliance with RAID:
        - Recommended:
          - 10 great read performance, more redundancy and safety guarantees. Data is spread over RAID0 and RAID1 where inside the RAID the data is mirrored for extra read performance and durability.
        - Discouraged:
          - 5 and 6: do not provide sufficient performance.
          - 0: good write performance, yet limited availability and reduced read operations.
    - Network
      - The way different hosts that hold different nodes in the cluster, are connected, impact the overall performance of your deployment.
  - Advise:
    - Choose the size and volume of your bandwidth carefully according to your topology
    - Consider the type of connection, latency, firewalls, geography...
    - Consider the network in conjunction with the read/write preference

### How data is stored on disk

A storage engine is the part of a database that is responsible for managing how data is stored, both in memory and on disk. Many databases support multiple storage engines, where different engines perform better for specific workloads. For example, one storage engine might offer better performance for read-heavy workloads, and another might support a higher throughput for write operations.

#### Physical files

The MongoDB preferred storage engine supports fine-grained control on how data is persisted on disk. The options --directoryperdb and --wiredTigerDirectoryForIndexes are made available to give you this choice.

For each collection or index, the WiredTiger storage engine will write an individual file. The \_catalog.wt file contains a catalogue of all different collections and indexes this mongod contains.

| Option                                           | Result                                                               |
| ------------------------------------------------ | -------------------------------------------------------------------- |
| \<default>                                       | It's all stored plain and flat in one directory.                     |
| --directoryperdb                                 | Folders for each database.                                           |
| --directoryperdb --wiredTigerDirectoryForIndexes | Folders for each database and subfolders for collections and indexes |

> The fine grained option enables the possibility to parallelize IO by mapping these folders to separate disks.

Physical writes to disk occur when:

1. Writeconcern with high level
1. Periodically flushes from memory to disk as decided by the system

#### Journaling

The journal file acts as a safeguard against data corruption caused by incomplete writes due to e.g. unsuspected power outage. Data stored in the journal will be used to restore the system to a consistent state. The journal flushes atomically. In writeConcern you can force the writing to a journal file.

### Basic benchmarking

Different types of benchmarking can be performed. In order of relevance and differentiating, making a like for like comparison:

- Low-level benchmarking, tools such as sysbench, iibench:
  - File IO performance
  - Scheduler performance
  - Memory allocation and transfer speed
  - Thread performance
  - Database server performance
  - Transaction isolation
- Database server benchmarking, tools such as YCSB, TPC:
  - Data set load
  - Writes per second
  - Reads per second
  - Balanced workloads
  - Read / write ratio
- Distributed systems benchmarking, tools such as Hibench, JEPSEN:
  - Linearization
  - Serialization
  - Fault tolerance

Benchmarking conditions and anti-patterns:

| Anti-pattern                                  | Reason                                                                         |
| --------------------------------------------- | ------------------------------------------------------------------------------ |
| Database swap replace                         | Comparison relational<>document: copy/paste relational schema into collections |
| Using mongo shell for write and read requests | poc-code with loops et all are not representative of an enterprise application |
| Using mongoimport to test write response      | what are you actually proving here                                             |
| Local laptop to run tests                     | other variables into play (open applications, IO, network)                     |
| Using default mongodb parameters              | use production parameters and enterprise conditions: security, HA              |

### Optimizing CRUD Operations

    // run an explained query (COLLSCAN & in-memory sort)
    exp.find({ "address.zipcode": { $gt: '50000' }, cuisine: 'Sushi' }).sort({ stars: -1 })

    // create a naive index
    db.restaurants.createIndex({"address.zipcode": 1,"cuisine": 1,"stars": 1})

    // rerun the query (uses the index, but isn't very selective and still does an in-memory sort)
    exp.find({ "address.zipcode": { $gt: '50000' }, cuisine: 'Sushi' }).sort({ stars: -1 })

_Index Selectivity_: make sure the most selective fields are first

    // see how many documents match 50000 for zipcode (10)
    db.restaurants.find({ "address.zipcode": '50000' }).count()

    // see how many documents match our range (about half)
    db.restaurants.find({ "address.zipcode": { $gt: '50000' } }).count()

    // see how many documents match an equality condition on cuisine (~2%)
    db.restaurants.find({ cuisine: 'Sushi' }).count()

    // reorder the index key pattern to be more selective
    db.restaurants.createIndex({ "cuisine": 1, "address.zipcode": 1, "stars": 1 })

    // and rerun the query (faster, still doing an in-memory sort)
    exp.find({ "address.zipcode": { $gt: '50000' }, cuisine: 'Sushi' }).sort({ stars: -1 })

_Equality, sort, range_: When building an index, this is a great rule to select fields.

- **equality**: indexed fields on which our queries will do equality matching
- **sort**: indexed fields on which our queries will sort on
- **range**: indexed fields on which our queries will have a range condition

  // swap stars and zipcode to prevent an in-memory sort
  db.restaurants.createIndex({ "cuisine": 1, "stars": 1, "address.zipcode": 1 })
  |_ Equality |_ Sort |\_Range

  // awesome, no more in-memory sort! (uses the equality, sort, range rule)
  exp.find({ "address.zipcode": { $gt: '50000' }, cuisine: 'Sushi' }).sort({ stars: -1 })

_Performance tradeoffs_: Sometimes it makes sense to be a little bit less selective to prevent in memory sorts since the execution time will be the lowest.

More information on [Create indexes to support your queries](https://docs.mongodb.com/manual/tutorial/create-indexes-to-support-queries/?jmp=university), [Use indexes to sort query results](https://docs.mongodb.com/manual/tutorial/sort-results-with-indexes/?jmp=university), [Create queries that ensure selectivity](https://docs.mongodb.com/manual/tutorial/create-queries-that-ensure-selectivity/?jmp=university)

### Covered Queries

Covered queries have high performance when interacting with a database. They are entirely satisfied by index keys, 0 documents need to be examined.

> A index covers a query if all fields are part of the index and all fields returned in the result are in the index.

    // create a compound index on three fields
    db.restaurants.createIndex({name: 1, cuisine: 1, stars: 1})

    // checkout a projected query
    db.restaurants.find({name: { $gt: 'L' }, cuisine: 'Sushi', stars: { $gte: 4.0 } }, { _id: 0, name: 1, cuisine: 1, stars: 1 })

Not possible when

- Indexed fields are arrays
- Indexed fields are embedded documents
- Run against a mongos if index does not contain the shard key

More information on [Query Optimization](https://docs.mongodb.com/manual/core/query-optimization/?jmp=university)

### Insert performance

Two big performance contributors:

_Index overhead_: severely increases read performance, downside of keeping indexes up to date every update/delete. Every index added impacts write performance.

|                      |         |         |         |
| -------------------- | ------- | ------- | ------- |
| nr of indexes        | 0       | 1       | 5       |
| avg inserts/s        | ~16.000 | ~15.000 | ~10.500 |
| % loss from baseline | 0%      | ~6.3%   | ~34.4%  |

> Around 6% write performance overhead per index increases the read performance by more than 10x.

_Write concern_: fine-tuning write performance durability/performance

- w: how many members of replica set will we wait for the write to be propagated to
- j: boolean whether we wait for a physical write to journal file on disk
- wtimeout: the amount of ms we want to the command to timeout. The write may still occur!

|                      |         |         |                |               |
| -------------------- | ------- | ------- | -------------- | ------------- |
| write concern        | 1-false | 1-true  | majority-false | majority-true |
| avg inserts/s        | ~27.000 | ~19.000 | ~16.000        | ~14.000       |
| % loss from baseline | 0%      | ~29.6%  | ~40.7%         | ~48.1%        |

> Tests performed on local machine talking to MongoDB Atlas cluster. Not a best case scenario.

More information on [Write performance](https://docs.mongodb.com/manual/core/write-performance/?jmp=university)

### Different data type implications

_Querying_: you have to use the same data type in your filter than the field value in the document you are looking for.

_Sorting_: sorting can happen intelligently on numerical values (int, numberdecimal). Documents will be grouped based on different bson type if field values are mixed. MinKey, Null, Numbers (int, long, double, decimal), symbol/string, objects...

In order to get numeric sorting on non-numeric fields, a collation can be applied. The numeric and other fields will still be grouped separately and not merged!

    // sort some shapes
    db.shapes.find({}, {base:1, _id:0}).sort({base:1})

    // create an index with a numeric ordering collation
    db.shapes.createIndex({base: 1}, {collation: {locale: 'en', **numericOrdering**: true}})

    // now the sort will be in numeric order grouped for each data type
    db.shapes.find({}, {base:1, _id:0}).sort({base:1})

_Application Implications_: it's important to maintain the same data type for fields across different documents to avoid application data consistency issues, reduce complexity for build and test codebase. You can ensure correctness by using document validation on the fields that matter.

### Performance considerations in distributed systems

Implications include

- Consider latency
- Data is spread across different nodes
- Read implications
- Write implications

Before sharding

- sharding is horizontal scaling
- have we reached limits of vertical scaling
- understand how data grows and data is accessed
- define a good shard key

In a sharded environment

- latency in application is caused
  - collocating mongos in same network zone as replica set
  - replica set nodes low latency
  - zone based sharding has a conceptual latency cost
- attention to: scatter-gather queries, routed queries
- attention to: sorting, limit & skip

## Extra: MongoDB World '17: Sizing MongoDB clusters

Typical questions to solution architects throughout the project life cycle.

- Do I need to shard?
- What size of servers should I use?
- What will my monthly Atlas/AWS/Azure/Google costs be?
- When will I need to add a new shard or upgrade my servers?
- How much data can my servers support?
- Will we able to meet our query latency requirements?

> The only **accurate** way to size a cluster is to **build a prototype** and run performance tests using **actual data and queries** on hardware with **specs similar to production servers**. Anything else is a guess, an estimate, providing ballpark figures needed for decision taking, order processing and schema design.

The output of an estimation will consist of:

- \# of shards
- Specifications of each server
  - CPU
  - Storage: size and performance (IOPS)
  - Memory
  - Network

A working set is defined as the set of indexes + frequently processed documents. Ideally all indexes for frequently used queries exist in memory and all frequently access documents are kept in memory.

### The sizing process

A common methodology looks like

- _Assumption list_: to be maintained at all times, preferably in a sheet with configurable parameters
- _Collection size_: how many docs, average size, how much data, how much compression
- _Working set_: estimate size of indexes and frequently accessed documents
- _Queries_: map frequent queries to IOPS
- _Adjustment_: based upon working set, checkpoints
- _Candidate server specs_: calculate # of shards, validate # of IOPS, RAM
- _Review, iterate, repeat_

### Estimating IOPS

Assume working set < RAM < Data size and memory contains indexes only (retrieving documents go to disk)

| Action               | IOPS                                         |
| -------------------- | -------------------------------------------- |
| Retrieve a document  | 1 (retrieve)                                 |
| Inserting a document | 1 (insert) + # of indexes                    |
| Deleting a document  | 1 (delete) + # of indexes                    |
| Update a document    | 2 (delete+insert new version) + # of indexes |

In case an index does not exist and a COLLSCAN is needed, _all_ documents need to be read from disk resulting in a very large amount of IOPS needed.

Once the basic numbers are in using this simplified model against all identified queries, we need to include unknowns and revise them:

- Working set: hopefully _some_ documents will be covered in the working set and cover the entire workload of the application in memory.
- Checkpoints: wiredtiger engine writes to RAM, then journal and flushes IO to disk in checkpoints. Writing to same documents frequently can reduce the # of IOPS due to optimization of the engine writing merged changes only once to disk
- Document size relative to block size: very large documents (5MB) on a 4KB block size, need far more blocks read from disk to read an entire document.
- Indexed arrays: every indexed element of the array needs to be updated when inserting/deleting/updating documents so factor these in.

The IOPS calculation becomes

| Line item                                            |
| ---------------------------------------------------- |
| + # of documents returned per second                 |
| + # of documents updated per second                  |
| + # of indexes impacted by each update               |
| + # of inserts updated per second                    |
| + # of indexes impacted by each insert               |
| + # of deletes per second \* 2 (1 delete + 1 insert) |
| + # of indexes impacted by each delete               |
| - Multiple updates occurring within checkpoint       |
| - % of find query results in cache                   |
| **Total IOPS**                                       |

### Estimating data size

Very hard when application doesn't exist yet. Advise is to design one document and programmatically generate a large data set (>1M), add guessed indexes and measure the collection size, index size, compression. Extrapolate to production size.

- \# of documents
  - Input from use case, client
- Data size
  - Data size = # of documents \* average document size
  - Average document size is available in db.stats(), compass, ops manager, cloud manager, atlas...
- Index size
- WiredTiger compression

### Estimating the working set

A working set is defined as the set of indexes + frequently processed documents. We know the index size from the estimation of data size.

Estimating the working set given the queries is _an art_ since '_what are the frequently accessed docs_ is a rhetorical question.

e.g. query analysis show

- dashboards look at last minute of data
- customer support tools inspect last hour worth of data
- reports run once a day inspect last year worth of data

The active documents would become 1 hour worth of data: 5000 x 3600 x 1KB = 18M KB

### Estimating the CPU

In most cases, RAM requirements -> large servers -> many cores so no specific requirements about CPU.

Exception to this rule of thumb is aggregations: aggregations are split up in threads which can benefit from multi-core set-up.

### Estimating the need for sharding

Calculation based on input gathered in previous steps on disk space and RAM.

- Disk:
  - Data size: 9TB
  - WT compression ratio: .33
  - Storage size: 3TB
  - Server disk capacity: 2TB
  - => 2 Shards required
- RAM:
  - Working set: 428GB
  - Server RAM: 128GB
  - 428/128 = 3.34
  - => 4 shards required
- IOPS:
  - 50K OPS
  - 20K IOPS AWS
  - 50K/20K = 2.5
  - => 3 shards required
