# Notes for M103: Basic Cluster Administration

## MongoD

Launch mongod in our first shell:

    mongod

Connect to the Mongo shell in our second shell:

    mongo

Shutdown mongod from our second shell:

    mongo admin --eval 'db.shutdownServer()'

Find command line options for mongod:

    mongod --help

Create new data directory and try to launch mongod with a new port and dbpath, and also fork the process:

    mkdir first_mongod
    mongod --port 30000 --dbpath first_mongod --fork

The above command will fail without a logpath - so we add one and then successfully launch mongod:

    mongod --port 30000 --dbpath first_mongod --logpath first_mongod/mongod.log --fork

Try to connect back to Mongo shell, without specifying a port:

    mongo

We need to add a port, because our mongod is running on port 30000, not the default 27017:

    mongo --port 30000

Shutdown the new server:

    mongo admin --port 30000 --eval 'db.shutdownServer()'

Launch mongod using default configuration:

    mongod

Launch mongod with specified --dbpath and --logpath:

    mongod --dbpath /data/db --logpath /data/log/mongod.log

Launch mongod and fork the process:

    mongod --dbpath /data/db --logpath /data/log/mongod.log --fork

Launch mongod with many configuration options:

    mongod --dbpath /data/db --logpath /data/log/mongod.log --fork --replSet "M103" --keyFile /data/keyfile --bind_ip "127.0.0.1,192.168.0.100" --sslMode requireSSL --sslCAFile "/etc/ssl/SSLCA.pem" --sslPEMKeyFile "/etc/ssl/ssl.pem"

Example configuration file, with the same configuration options as above:

    storage:
      dbPath: "/data/db"
    systemLog:
      path: "/data/log.mongod.log"
      destination: "file"
    replication:
      replSetName: M103
    net:
      bindIp : "127.0.0.1,192.168.0.100"
    ssl:
      mode: "requireSSL"
      PEMKeyFile: "/etc/ssl/ssl.pem"
      CAFile: "/etc/ssl/SSLCA.pem"
    security:
      keyFile: "/data/keyfile"
    processManagement:
      fork : true

List --dbpath directory:

    ls -l /data/db

List diagnostics data directory:

    ls -l /data/db/diagnostic.data

List journal directory:

    ls -l /data/db/journal

List socket file:

    ls /tmp/mongodb-27017.sock

### Basic commands

User management commands:

    db.createUser()
    db.dropUser()

Collection management commands:

    db.<collection>.renameCollection()
    db.<collection>.createIndex()
    db.<collection>.drop()

Database management commands:

    db.dropDatabase()
    db.createCollection()

Database status command:

    db.serverStatus()

Creating index with Database Command:

    db.runCommand(
    { "createIndexes": <collection> },
    { "indexes": [
        {
        "key": { "product": 1 }
        },
        { "name": "name_index" }
        ]
    }
    )

Creating index with Shell Helper:

    db.<collection>.createIndex(
    { "product": 1 },
    { "name": "name_index" }
    )

Introspect a Shell Helper:

    db.<collection>.createIndex

### Logging basics

Get the logging components:

    mongo admin --host 192.168.103.100:27000 -u m103-admin -p m103-pass --eval '
    db.getLogComponents()
    '

Change the logging level:

    mongo admin --host 192.168.103.100:27000 -u m103-admin -p m103-pass --eval '
    db.setLogLevel(0, "index")
    '

Tail the log file:

    tail -f /data/db/mongod.log

Update a document:

    mongo admin --host 192.168.103.100:27000 -u m103-admin -p m103-pass --eval '
    db.products.update( { "sku" : 6902667 }, { $set : { "salePrice" : 39.99} } )
    '

Look for instructions in the log file with grep:

    grep -R 'update' /data/db/mongod.log

### Profiler

CRUD operations, Administrative commands, and Cluster configuration operations are all captured by the database profiler.

Get profiling level:

    mongo newDB --host 192.168.103.100:27000 -u m103-admin -p m103-pass --authenticationDatabase admin --eval '
    db.getProfilingLevel()
    '

Set profiling level:

    mongo newDB --host 192.168.103.100:27000 -u m103-admin -p m103-pass --authenticationDatabase admin --eval '
    db.setProfilingLevel(1)
    '

Show collections:

    mongo newDB --host 192.168.103.100:27000 -u m103-admin -p m103-pass --authenticationDatabase admin --eval '
    db.getCollectionNames()
    '

Note: show collections only works from within the shell

Set slowms to 0:

    mongo newDB --host 192.168.103.100:27000 -u m103-admin -p m103-pass --authenticationDatabase admin --eval '
    db.setProfilingLevel( 1, { slowms: 0 } )
    '

Insert one document into a new collection:

    mongo newDB --host 192.168.103.100:27000 -u m103-admin -p m103-pass --authenticationDatabase admin --eval '
    db.new_collection.insert( { "a": 1 } )
    '

Get profiling data from system.profile:

    mongo newDB --host 192.168.103.100:27000 -u m103-admin -p m103-pass --authenticationDatabase admin --eval '
    db.system.profile.find().pretty()
    '

The command show collections no longer lists the system.\* collections. It changed after version 4.0.

To list all of the collection names you can run this command:

    db.runCommand({listCollections: 1})

### Security

Print configuration file:

    cat /etc/mongod.conf

Launch standalone mongod:

    mongod -f /etc/mongod.conf

Connect to mongod:

    mongo --host 127.0.0.1:27017

Create new user with the root role (also, named root):

    use admin
    db.createUser({
    user: "root",
    pwd: "root123",
    roles : [ "root" ]
    })

Connect to mongod and authenticate as root:

    mongo --username root --password root123 --authenticationDatabase admin

Run DB stats:

    db.stats()

Shutdown the server:

    use admin
    db.shutdownServer()

### Built-in roles

Authenticate as root user:

    mongo admin -u root -p root123

Create security officer:

    db.createUser(
    { user: "security_officer",
        pwd: "h3ll0th3r3",
        roles: [ { db: "admin", role: "userAdmin" } ]
    }
    )

Create database administrator:

    db.createUser(
    { user: "dba",
        pwd: "c1lynd3rs",
        roles: [ { db: "admin", role: "dbAdmin" } ]
    }
    )

Grant role to user:

    db.grantRolesToUser( "dba",  [ { db: "playground", role: "dbOwner"  } ] )

Show role privileges:

    db.runCommand( { rolesInfo: { role: "dbOwner", db: "playground" }, showPrivileges: true} )

### Server Tools

List mongodb binaries:

    find /usr/bin/ -name "mongo\*"

Create new dbpath and launch mongod:

    mkdir -p ~/first_mongod
    mongod --port 30000 --dbpath ~/first_mongod --logpath ~/first_mongod/mongodb.log --fork

Use mongostat to get stats on a running mongod process:

    mongostat --help
    mongostat --port 30000

Use mongodump to get a BSON dump of a MongoDB collection:

    mongodump --help
    mongodump --port 30000 --db applicationData --collection products
    ls dump/applicationData/
    cat dump/applicationData/products.metadata.json

Use mongorestore to restore a MongoDB collection from a BSON dump:

    mongorestore --drop --port 30000 dump/

Use mongoexport to export a MongoDB collection to JSON or CSV (or stdout!):

    mongoexport --help
    mongoexport --port 30000 --db applicationData --collection products
    mongoexport --port 30000 --db applicationData --collection products -o products.json

Tail the exported JSON file:

    tail products.json

Use mongoimport to create a MongoDB collection from a JSON or CSV file:

    mongoimport --port 30000 products.json

## Replication

The configuration file for the first node (node1.conf):

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

Creating the keyfile and setting permissions on it:

    sudo mkdir -p /var/mongodb/pki/
    sudo chown vagrant:vagrant /var/mongodb/pki/
    openssl rand -base64 741 > /var/mongodb/pki/m103-keyfile
    chmod 400 /var/mongodb/pki/m103-keyfile

Creating the dbpath for node1:

    mkdir -p /var/mongodb/db/node1

Starting a mongod with node1.conf:

    mongod -f node1.conf

Copying node1.conf to node2.conf and node3.conf:

    cp node1.conf node2.conf
    cp node2.conf node3.conf

Editing node2.conf using vi:

    vi node2.conf

Saving the file and exiting vi:

    :wq

node2.conf, after changing the dbpath, port, and logpath:

    storage:
      dbPath: /var/mongodb/db/node2
    net:
      bindIp: 192.168.103.100,localhost
      port: 27012
    security:
      keyFile: /var/mongodb/pki/m103-keyfile
    systemLog:
      destination: file
      path: /var/mongodb/db/node2/mongod.log
      logAppend: true
    processManagement:
      fork: true
    replication:
      replSetName: m103-example

node3.conf, after changing the dbpath, port, and logpath:

    storage:
      dbPath: /var/mongodb/db/node3
    net:
      bindIp: 192.168.103.100,localhost
      port: 27013
    security:
      keyFile: /var/mongodb/pki/m103-keyfile
    systemLog:
      destination: file
      path: /var/mongodb/db/node3/mongod.log
      logAppend: true
    processManagement:
      fork: true
    replication:
      replSetName: m103-example

Creating the data directories for node2 and node3:

    mkdir /var/mongodb/db/{node2,node3}

Starting mongod processes with node2.conf and node3.conf:

    mongod -f node2.conf
    mongod -f node3.conf

Connecting to node1:

    mongo --port 27011

Initiating the replica set:

    rs.initiate()

Creating a user:

    use admin
    db.createUser({
    user: "m103-admin",
    pwd: "m103-pass",
    roles: [
        {role: "root", db: "admin"}
    ]
    })

Exiting out of the Mongo shell and connecting to the entire replica set:

    exit
    mongo --host "m103-example/192.168.103.100:27011" -u "m103-admin"
    -p "m103-pass" --authenticationDatabase "admin"

Getting replica set status:

    rs.status()

Adding other members to replica set:

    rs.add("m103.mongodb.university:27012")
    rs.add("m103.mongodb.university:27013")

Getting an overview of the replica set topology:

    rs.isMaster()

Stepping down the current primary:

    rs.stepDown()

Checking replica set overview after election:

    rs.isMaster()

Commands covered in this lesson:

    rs.status()
    rs.isMaster()
    db.serverStatus()['repl']
    rs.printReplicationInfo()

### local database

Making a data directory and launching a mongod process for a standalone node:

    mkdir allbymyselfdb
    mongod --dbpath allbymyselfdb

All MongoDB instances start with two default databases, admin and local:

    mongo
    show dbs

Display collections from the local database (this displays more collections from a replica set than from a standalone node):

    use local
    show collections

Querying the oplog after connected to a replica set:

    use local
    db.oplog.rs.find()

Getting information about the oplog. Remember the oplog is a capped collection, meaning it can grow to a pre-configured size before it starts to overwrite the oldest entries with newer ones. The below will determine whether a collection is capped, what the size is, and what the max size is.

Storing oplog stats as a variable called stats:

    var stats = db.oplog.rs.stats()

Verifying that this collection is capped (it will grow to a pre-configured size before it starts to overwrite the oldest entries with newer ones):

    stats.capped

Getting current size of the oplog:

    stats.size

Getting size limit of the oplog:

    stats.maxSize

Getting current oplog data (including first and last event times, and configured oplog size):

    rs.printReplicationInfo()

Create new namespace m103.messages:

    use m103
    db.createCollection('messages')

Query the oplog, filtering out the heartbeats ("periodic noop") and only returning the latest entry:

    use local
    db.oplog.rs.find( { "o.msg": { $ne: "periodic noop" } } ).sort( { $natural: -1 } ).limit(1).pretty()

Inserting 100 different documents:

    use m103
    for ( i=0; i< 100; i++) { db.messages.insert( { 'msg': 'not yet', _id: i } ) }
    db.messages.count()

Querying the oplog to find all operations related to m103.messages:

    use local
    db.oplog.rs.find({"ns": "m103.messages"}).sort({$natural: -1})

Illustrating that one update statement may generate many entries in the oplog:

    use m103
    db.messages.updateMany( {}, { $set: { author: 'norberto' } } )
    use local
    db.oplog.rs.find( { "ns": "m103.messages" } ).sort( { $natural: -1 } )

Remember, even though you can write data to the local db, you should not.

## Sharding

Configuration file for config servers:

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

Starting the three config servers:

    mongod -f csrs_1.conf
    mongod -f csrs_2.conf
    mongod -f csrs_3.conf

Connect to one of the config servers:

    mongo --port 26001

Initiating the CSRS:

    rs.initiate()

Creating super user on CSRS:

    use admin
    db.createUser({
    user: "m103-admin",
    pwd: "m103-pass",
    roles: [
        {role: "root", db: "admin"}
    ]
    })

Authenticating as the super user:

    db.auth("m103-admin", "m103-pass")

Add the second and third node to the CSRS:

    rs.add("192.168.103.100:26002")
    rs.add("192.168.103.100:26003")

Mongos config (mongos.conf):

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

Connect to mongos:

    vagrant@m103:~$ mongo --port 26000 --username m103-admin --password m103-pass --authenticationDatabase admin

Check sharding status:

    MongoDB Enterprise mongos> sh.status()

Updated configuration for node conf:

    sharding:
      clusterRole: shardsvr
    storage:
      dbPath: /var/mongodb/db/node1
    wiredTiger:
        engineConfig:
        cacheSizeGB: .1
    net:
      bindIp: 192.168.103.100,localhost
      port: 27011
    security:
      keyFile: /var/mongodb/pki/m103-keyfile
    systemLog:
      destination: file
      path: /var/mongodb/db/node1/mongod.log
      logAppend: true
    processManagement:
      fork: true
    replication:
      replSetName: m103-repl

Connecting directly to secondary node (note that if an election has taken place in your replica set, the specified node may have become primary):

    mongo --port 27012 -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"

Shutting down node:

    use admin
    db.shutdownServer()

Restarting node with new configuration:

    mongod -f node2.conf

Stepping down current primary:

    rs.stepDown()

Adding new shard to cluster from mongos:

    sh.addShard("m103-repl/192.168.103.100:27012")

### Config DB

Switch to config DB:

    use config

Query config.databases:

    db.databases.find().pretty()

Query config.collections:

    db.collections.find().pretty()

Query config.shards:

    db.shards.find().pretty()

Query config.chunks:

    db.chunks.find().pretty()

Query config.mongos:

    db.mongos.find().pretty()

### Shard key

- Shard keys must be indexed: must exist before you can select the indexed fields for your shard key
- Shard keys are immutable: cannot change shard key fields post-sharding, you cannot change the values of the shard key fields post-sharding
- Shard keys are permanent: cannot unshard a sharded collection

Show collections in m103 database:

    use m103
    show collections

Enable sharding on the m103 database:

    sh.enableSharding("m103")

Find one document from the products collection, to help us choose a shard key:

    db.products.findOne()

Create an index on sku:

    db.products.createIndex( { "sku" : 1 } )

Shard the products collection on sku:

    sh.shardCollection("m103.products", {"sku" : 1 } )

Checking the status of the sharded cluster:

    sh.status()

### Chunks

Show collections in config database:

    use config
    show collections

Find one document from the chunks collection:

    db.chunks.findOne()

Change the chunk size:

    use config
    db.settings.save({_id: "chunksize", value: 2})

Check the status of the sharded cluster:

    sh.status()

Find all chunks for a given collection

    db.getSiblingDB("config").chunks.find(
    {
        "ns" : "m103.products"
    }
    )

    db.getSiblingDB("config").chunks.find(
    {
        "ns" : "m103.products",
        $expr: {
            $and : [
            {$gte : [ 21572585, "$min.sku"]},
            {$lt : [21572585, "$max.sku"]}
            ]
        }
    }
    )

### Balancer

The Primary of the CSRS is responsible for running the balancer process.

Start the balancer:

    sh.startBalancer(timeout, interval)

Stop the balancer:

    sh.stopBalancer(timeout, interval)

Enable/disable the balancer:

    sh.setBalancerState(boolean)

### Targeted vs Scatter-gather queries

The mongos is responsible for merging the results of a standard find operation.
