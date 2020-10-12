# M201 Performance

Notes from the M201 MongoDB training course.

## Hardware Considerations & Configurations

When talking about performance, we cannot forget about the actual hardware configuration that will support our database and application deployments.

A full disclosure of how to tune and size your hardware needs, for a given deployment, are out of scope for this course.

We have other specific courses on this subject.

That we recommend you guys to take.

But let's just do a quick pass on the hardware and configuration of our system, and why it becomes an essential consideration for the overall performance of your deployments.

MongoDB is a high performance database.

But to operate correctly, while supporting your applications, requires adequate hardware provisioning.

Let's review which hardware resources and configurations we have available.

And how MongoDB uses those computational resources.

A computer or server can be represented in its essence by the Von Neumann architecture.

Where we are going to have a CPU for processing and calculations.

Memory for execution.

Disk and IO-- although you may consider Disk as another type of IO.

For persistency and communications between servers, or even within our host processes.

These are the main resources that MongoDB relies on to operate adequately.

In MongoDB deployments, as in many other modern databases, memory is a quintessential resource.

Over the past few years, the availability of RAM and the fall of its production costs contributed for the development of databases' architectures.

That privileged the users of this fast and performant resource.

The fact that RAM or memory is 25 times faster than common SSDs also makes this transition of Disk oriented into RAM oriented a nice, strong appealing factor for databases to be designed around usage of memory.

As a result of this, MongoDB has storage engines that are either very dependent on RAM, or even completely in memory [?

execution ?] modes for its data management operations.

A significant number of operations rely heavily in RAM.

Like the aggregation pipeline operations, the index traversing.

Writes are first performed in RAM allocated pages.

The query engine requires RAM to retrieve the quarter results.

And finally, connections are handling memory.

Roughly, one megabyte per established connection.

And therefore they require memory space.

It is safe to say that the more RAM you have available, the more performance your department of MongoDB will tend to be.

CPU is used by all applications for computational processing.

Databases are just another category of applications.

MongoDB is no different.

But the utilization of this resource is generally more attached with two main factors.

Storage engines that we are using, and the concurrency level that your MongoDB instance will be subjected to.

By default, MongoDB will try to use all available CPU cores to respond to incoming requests.

Our non-locking concurrency control mechanism, using wired tag or storage engine, rely heavily in the CPU to process these requests.

This means that if we have non-blocking operations, like writing different documents concurrently or responding to an incoming query requests like Reads, MongoDB will perform better the more CPU resources we have available.

Also, there are certain operations, like page compression, data calculation operations, aggregation framework operations, and map reduce, amongst others that will require the availability of CPU cycles.

But do not forget that not all writes and read operations are non-locking operations.

Writing constantly to the same document, for example, and in similar place updates will require each write to block all other writes on that same document to comply.

In situations such as this, multiple CPU's do not help performance.

Because the threads can not do their work in parallel.

Since always the same document will be affected by the same write.

As you probably already noticed, MongoDB is a database.

And one of the things expected from databases is their ability to persist information written.

Genius we may say.

For persisting data, MongoDB will use disks.

The IOPS, which stands for input output operations per second, that your server DISK provides, the faster we can write and read data.

And the faster your persistency layer will respond to database and application requests.

The types of disks will greatly affect the overall performance of your MongoDB deployment.

If we compare the different types of disks.

HDDs, SSDs, EBS, volumes, in terms of random access latency and IOPS, we can immediately tell that there is going to be a big difference between what one can expect, in terms of performance, given the different types of disks that we might be using.

This can be used in different architectures.

More specifically, we can use RAID architectures in our servers for redundancy of read and write operations.

MongoDB will benefit from some but not all RAID architectures.

The recommended RAID architecture for MongoDB deployments is RAID 10, or RAID 1- 0.

This architecture is the one that offers more redundancy and safeguards guarantees with a good performance combination.

On the other hand, we highly discourage deployments that use RAID 5, or even RAID 6.

Since these do not typically provide sufficient performance to MongoDB deployments.

We also recommend avoiding RAID 0.

Because while providing good write performance, it provides very limited availability.

It can lead to reduced performance on read operations.

Britain is a disk architecture that provides both redundancy of segments across physical drives.

But also allows extended performance, since it last penalization of multiple writes, reads, and reads and writes.

In the same disk allocated segments.

Which is quite awesome for a database like MongoDB.

A particularly important aspect of MongoDB is the ability to use several different disks.

That might be available in your servers.

This will allow to distributing IO load of different databases, indexes, journaling, and other files like lock files.

Which allow you to optimize your MongoDB overall performance.

MongoDB deployments also rely on network hardware.

Applications will reach the database by establishing connections to the hosts, where MongoDB instance is running.

The faster and the larger the bandwidth is for your network, the better performance you will experience.

But this is not the end of the story regarding network utilization with MongoDB.

MongoDB is a distributed database for high availability.

Rapid [INAUDIBLE] clusters do the high availability part.

But also for horizontal scaling, where the shard and cluster in all its different components, allows you to get a horizontal distribution of your data.

The way that your different hosts that hold the different nodes of your cluster are connected, can affect the overall performance of your system.

Also, the types of network switches, load balancers, firewalls, and how far apart the cluster nodes are-- either by being distributed across different data centers or regions.

The type of connections between data centers, especially latency-- we haven't cracked going faster than the speed of light yet-- will play a great deal in the performance experienced by your application.

This aligned with the write concern, read concern, and read preference that your application can set while emitting commands or doing requests to the server, needs to be taken into consideration when analyzing the performance of your application.

## Indexes

Welcome to the chapter on indexes.

In this chapter, we're going to cover some of the different indexes supported by MongoDB and we'll also discuss the different properties that those indexes have.

In this first video, we're going to examine what indexes are, and how they work.

And to answer this first question, I think it's helpful to ask the question.

What problem do indexes try to solve?

And that is, slow queries.

Let's use a quick analogy to illustrate what I'm talking about.

Let's say we're looking for some content on bedspreads in a book about interior design.

One way that I can find this information is if I went through the book page by page looking for information on bedspreads.

But this would not be very fast.

I could find this information much more quickly if I went to the back of the book to the index, where I can quickly search for the word bedspread because the index is ordered alphabetically.

I'll then find what page talks about bedspreads, and I can go directly to that page.

And this is essentially how database indexes work.

Working off the book analogy, a book, in the context MongoDB, would be a collection.

If you don't use an index when we query our collection, then the database will have to look at every single document.

Creatively we call this a collection scan, which means that as our collection grows in size, we will have to search through more and more documents to satisfy our query.

In computer science we call this an order of N operation, commonly referred to as big O of N, or having a linear running time.

And that's because the running time of our query is linearly proportional to the number of documents-- N-- that we have in our collection.

But we can do much better if we have an index.

Like in the book analogy, we can use an index to limit our search space.

Rather than searching through every document, we can search through the ordered index first.

The MongoDB index keeps a reference to every document in our collection.

Think of this index as a list of key value pairs, where the key is the value of the field that we've indexed on, and the value of the key is the actual document itself.

I like to point out that this means an index is associated with one or more fields.

This means when we create our index, we have to specify which fields on the documents in our collection we want to index on.

For example, \_id field is automatically indexed for us.

But if I were to write a query that didn't use the \_id field, I wouldn't be able search the \_id index and find the reference to my documents.

And it is possible to have many indexes on the same collection.

I might create multiple indexes on different fields if I find that I have different queries for different fields.

Like the index at the back of a book, the index keys are stored in an order.

Because of this, we don't have to look at every single index entry in or to find it.

MongoDB uses a data structure called a b-tree to store its indexes.

B-trees can be used to find target values with very few comparisons.

With a collection scan, each new insertion creates an extra comparison.

But with a b-tree, each new insertion doesn't necessarily mean an extra comparison.

For example, if I was searching for the value of 15, my search wouldn't change if I inserted the value of 5.

We can plot a chart to get an idea of how many fewer documents we need to examine when using an index.

You can see that when we don't have an index, we have a very linear function of number of comparisons.

But when we do have an index, we need to examine far fewer documents.

Before I finish this video, I would to discuss one last topic, which is index overhead.

The awesome query performance gain that we get with indexes doesn't come for free.

With each additional index, we decrease our write speed for a collection.

Every time there's a new document inserted into collection, a collections indexes might need to be updated.

Similarly, if a document were to change, or if it was completely removed, one or more of our b-trees might need to be balanced.

This means that we need to be careful when creating indexes.

We don't want to have too many unnecessary indexes in a collection because there would then be an unnecessary loss in insert, update, and delete performance.

You should have a good idea of what indexes are, their pros and cons, and how they work.

## How data is stored on disk

Databases tend to have this nice feature that allows data to be persisted using the server's file system.

But how do they do it?

Which files do they write information to?

How these files are organized?

How do databases collections, indexes, get to get organized within the data structures and storage engines that support the persistency layer of your database?

These are the topics we're going to discuss in this lesson.

The way that MongoDB stores data will differ between the different storage engines that MongoDB supports.

The particular details of how each storage engine organize data in detail are out of scope, but let's review at a high level how the different storage engines organize data.

Now, MongoDB allows us to create a few data management objects.

We have databases, which are logical groups of collections, collections, which are operational units that group documents together.

We're going to have indexes on collections over [INAUDIBLE] presence on documents.

And obviously, we're going to have documents, atomic units of information used by applications.

But before we jump into the overview of the data structures, let's have a peek into the dbpath content of our MongoDB directory.

Here, I'm going to start my mongod, pointing my dbpath to data/db.

Now, this is the default MongoDB data path.

But I just want to reinforce the fact that you can change it to another alternative dbpath if you wish to do so.

All right.

I started the process.

And this is going to be a very short lived instruction, because I'm going to shut down the server again.

If we ls that data/db folder, we can see that immediately MongoDB stores and starts writing a bunch of different documents in some directories.

For each collection, or index, the WiredTiger storage engine will write an individual file.

We start with a MDB catalog file that contains the catalog of all different collections and indexes that this particular mongod contains.

But we can have a little bit more elaborate file system in data structures than this plain flat organization.

Let's go ahead and remove that data/db folder, remove all its content.

Let's recreate the folder again.

And now let's launch the mongod with a little with more flavor.

I'm going to use the same dbpath, the same logpath as well, the same file for logging.

I'm also going to fork the process.

And I'm going to add this directoryperdb instruction.

Once I do that, I'm also going to write a single document on a particular new collection that doesn't exist at the moment, hello, and inserting it on collection a.

And then, I'm just going to go ahead and shut down the server.

If I look into the folder, my dbpath again, I can see that now I have a slightly different organization of data.

I'm going to have these three new folders, admin, local, and hello.

Admin and local are default databases that MongoDB creates.

Hello is the newly created database that we've created on the previous instruction.

By specifying dash dash directoryperdb, we'll get slightly different organization in the way that we are going to have a folder for each single database that this mongod raised.

If we look into the subfolder inside of our dbpath for our newly created database, we will see that we are going to have one collection and one index file.

Collection for our a database collection, and obviously you will always have an underscore there's ID index.

So this is the file that we have created.

But we can go a little bit step forward in terms of organization of our data, especially on WiredTiger.

I repeated exactly the same process.

I created, again, our hello database.

And here, I've shut down our mongod again.

If we look into our database folder, our dbpath, we can see that we're still creating one single folder for each database that our mongod holds.

But if we're looking at the hello database now, we're going to have a different organization.

We're going to have a single directory for collections and one for all index files.

So that sounds cool and all, but what does this have to do with performance anyway?

Well, if you have several disks in your server, this will enable a great deal of I/O paralyzation.

To do this, we create symbolic links to mount points on different physical drives.

Every time you write or read from our database, we will most likely be using the two data structures, the collections and the indexes that support our queries, or when we need to be updated, we will write the data to the collection and to the indexes too.

Paralyzation of I/O can improve the overall throughput of our persistency layer, and therefore, positively impacting the performance of operations.

Mongod also offers compression while storing information on disc.

We instruct our storage engine to store data on disk using a compression algorithm.

This has a direct impact on performance by performing smaller I/O operations, which means that smaller data is faster I/O, at the cost of CPU cycles.

Before writing data to disk, data will be allocated in memory.

Without getting ourselves too overwhelmed in details of memory allocation, all data is eventually written to this, for persistency reasons.

This process will be triggered by two main ways, user side, by specifying a particular writeConcorn, or forcing an [INAUDIBLE] sync operation within administration command, or by the periodical internal process that regulates how data needs to be flushed and synced into the data file.

This is defined by sync periods.

Journaling is as well an essential component of our persistency mechanism.

The journal file acts as a safeguard against data corruption caused by incomplete data file writes.

The system suffers a shut down and expected data stored in the journal will be used to recover into a consistent correct state.

Now, journal has its own file structure that include individual write operations.

To minimize the performance impact of journal, the journal flushes our performed using group commits in a compressed format.

All writes to the journal file are atomic, ensuring consistency of on this journal files.

From the application perspective, and taking performance of operations in mind, we can also force data to be synced to journal before acknowledging a write.

This is using j equals true in our writeConcern.

That said, keep in mind that these will have some impact in the performance of your application.

We will wait till the sync is done to disk, and then confirm back that the write has been acknowledged.

And this is how data is stored on disk.

### Code

    # start a mongod
    mongod --dbpath /data/db --fork --logpath /data/db/mongodb.log

    # immediately shut down the server
    mongo admin --eval 'db.shutdownServer()'

    # checkout the server's data files
    ls /data/db


    # remove the folder and recreate it
    rm -rf /data/db
    mkdir -p /data/db

    # this time, start the server with the --directoryperdb option
    mongod --dbpath /data/db --fork --logpath /data/db/mongodb.log --directoryperdb

    # write a single document into the 'hello' database
    mongo hello --eval 'db.a.insert({a:1}, {writeConcern: {w:1, j:true}})'

    # then, shutdown the server
    mongo admin --eval 'db.shutdownServer()'

    # checkout the server's data files
    ls /data/db

    # checkout the hello collection's data/index file(s)
    ls /data/db/hello


    # this time, start the server with the --directoryperdb and
    # --wiredTigerDirectoryForIndexes options
    mongod --dbpath /data/db --fork --logpath /data/db/mongodb.log \
        --directoryperdb --wiredTigerDirectoryForIndexes

    # write a single document into the 'hello' database
    mongo hello --eval 'db.a.insert({a:1}, {writeConcern: {w:1, j:true}})'

    # then, shutdown the server
    mongo admin --eval 'db.shutdownServer()'

    # checkout the server's data files
    ls /data/db

    # checkout the hello collection's data/index folder(s)
    ls /data/db/hello


    # checkout the journal directory
    ls /data/db/journal

## Single field indexes

In this video, I want to talk about single field indexes.

The single field index is the simplest index that MongoDB has to offer.

It will form the foundation for all later indexes that we'll discuss.

And we'll be describing all of those in contrast with the based on behaviors that we'll learn about here.

A single field index is exactly what it sounds like.

It's an index that captures just the keys on a single field, and it has the following key features.

The keys are from only one field.

We can find a single value for that indexed field.

We can find a range of values.

We can use dot notation to index the fields in our subdocuments.

And we can use it to find several distinct values with a single query.

By the end of this section you should be able to create single field indexes, and use them in each of the cases listed here.

Let's go ahead and see what this looks like in the shell.

First, let's go ahead and import some data by using the Mongo import command and, we're going to import the people.json on file.

Let's go ahead and use the Mongo import command.

Import the people.json file.

Now, let's fire up the shell, and run a find query on our new people collection.

So here's the query we're going to run.

We're going to try to find all the documents that have a SSN, which is a social security number, of this value.

And you'll notice I've appended this "explain" function of pass and this execution stats parameter.

And this is so that we can get some extra information about our query.

Now, there's a lot of output here.

In other lessons, we'll dig into this output much more deeply, but for now there's just a few fields we care about.

If I scroll up here, you can see that the query planner says that we're doing a collection scan, which means that we're looking at every document in the database.

And if we scroll down here, we can look at execution stats, and see that we had to examine 50,000 documents.

Which makes sense because there's 50,000 documents in this collection, and since we had a collection scan we had to look at all 50,000 documents.

We had to look at 50,000 documents, even though we only returned one document.

So this is a very bad ratio.

Even though we only had to return one document, we had look at 50,000.

It's not very efficient.

You'll also notice that we looked at zero index keys, and that's because we did a collection scan.

We didn't use an index because we haven't created any indexes yet.

Let's go ahead and do that now.

Here I'm creating an index on the SSN field, and I'm saying one to specify that it's going to be an ascending index.

Now, that I've ran this command, MongoDB built the index.

And by doing that, MongoDB had a look at every single document in the collection.

And then it pulls out the SSN field.

If the SSN field is not present on a document, then that key entry is going to have a null value.

Let's go ahead and run our query again.

I want to show you a cool trick.

Here I'm going to create an explainable object, where instead of putting explain after the find, I'm going to put it directly on a collection.

Now, I can just run "find" on my explainable object.

So I'm running the same query as I did before.

Now, this time when we look at the explain output, we'll notice that the winning plan is now an index scan.

So we're not doing a collection scan anymore.

And if we scroll down to execution stats, we'll see that we returned the same document we did before.

We return one document.

But rather than looking at 50,000 documents, we only had to look at one document.

We only have to look at the one document that we returned, which is awesome.

And that's because we're able to use index keys to find our document.

In this case we had to use one index key.

We immediately found the index key that had the value of our query, and that index key pointed directly to the document that we wanted to return.

This is much more efficient than scanning the entire collection, which is what we do if we don't have an index to use on our query.

Now, if the query predicate of my query doesn't use a field that is indexed on, you'll see from the explain output that we weren't able to use that index.

We had do a collection scan.

So that means that because we didn't use the SSN field, the field that we indexed on, we still had to look at 50,000 documents to return the 10 documents that we wanted.

Now, we know that MongoDB allows us to use dot notation to query inside of a subdocument.

But you can also use dot notation when specifying indexes.

Let's use a simple example to illustrate this.

Here, I'm going to insert a document into the examples collection.

This document will have a field that has a subdocument as its value.

And the subdocument has two keys, One called index field and one called other field.

And I'll go ahead and also insert another document that has similar fields, but with different values.

And like I was saying, we can specify an index that uses dot notation to index on the fields of our embedded document.

And this way, when we use dot notation in our queries, we'll still be able to use an index, as illustrated by our explain output, right here.

By the way, I want to point out that we should never index on the field that points to a subdocument, because doing so we'd have to query on the entire subdocument.

It's much better to use dot notation when querying because we can just query on the fields that we care about in our subdocuments.

If you do need to index on more than one field, you can use a compound index, which we'll learn about in another lesson.

In this video, let's continue talking about single field indexes.

In the last video, we saw how we can create an index on a single field to query on a single value or to use dot notation to query on a single value inside of a subdocument.

In this video, let's talk about using single field indexes to query on ranges of values and querying on several distinct values.

Earlier, we saw how to use an index to get just one value for a social security number.

Here, we're going to try to match for a range of social security numbers.

And if we look at our explain output, as we would expect, we're able to do an index scan.

And by doing this index scan, when we look at execution stats, we can see that we only had to examine 49 documents to return 49 documents.

So we didn't have to look at any superfluous documents, which is really, really awesome, because we were able to use our 49 index keys to directly get those 49 documents.

Rather than using a range, what if I wanted to query on a set of social security numbers?

Well, as you'll see, we're still able to do an index scan as our winning plan.

And you'll see, if we look under executionStats, we only had to examine three documents to find the three documents that matched our set.

We did have to look at six index keys, which is more than we would probably expect, which is a little less efficient than it could be.

But it's still much better than doing a collections scan.

These extra index keys are due to the algorithm the system used, which can involve overshooting the values we're looking for.

I also want to note that I can specify multiple fields in my query.

We can still use an index, even if we're only touching one of the multiple fields.

So when we look our explain output, we can see what's going on here.

And we can see here, by looking at our winningPlan, that what we're doing is, as we're doing an index scan on the social security number, to filter down to the three documents that match our query.

And then from those three documents, we're then filtering on which of those three match our last name predicate.

So if a query is matching documents on two or more fields, but the database only has a single field index for one of them, what will happen is the database will filter using the index and then it will then look at those documents and fetch only the ones that match the other predicates.

Later, you'll see how we can use compound indexes to get even more efficiency in our queries.

But this is the behavior you should expect from single field indexes.

So let's recap what we've learned.

In these last two videos, we've learned that we can use a single field index to query on a single value or to query on a range of values.

We can also use it for dot notation when using subdocuments.

And finally, we've seen that we can use it for several distinct values, as I have a single query.

And that's what you need to know about single field indexes.

### Code

    // execute the following query and collect execution statistics
    db.people.find({ "ssn" : "720-38-5636" }).explain("executionStats")

    // create an ascending index on ssn
    db.people.createIndex( { ssn : 1 } )

    // create an explainable object for the people collection
    exp = db.people.explain("executionStats")

    // execute the same query again (should use an index)
    exp.find( { "ssn" : "720-38-5636" } )

    // execute a new query on the explainable object (can't use the index)
    exp.find( { last_name : "Acevedo" } )


    // insert a documents with an embedded document
    db.examples.insertOne( { _id : 0, subdoc : { indexedField: "value", otherField : "value" } } )
    db.examples.insertOne( { _id : 1, subdoc : { indexedField : "wrongValue", otherField : "value" } } )

    // create an index using dot-notation
    db.examples.createIndex( { "subdoc.indexedField" : 1 } )

    // explain a query using dot-notation
    db.examples.explain("executionStats").find( { "subdoc.indexedField" : "value" } )

    // explain a range query (using an index)
    exp.find( { ssn : { $gte : "555-00-0000", $lt : "556-00-0000" } } )

    // explain a query on a set of values
    exp.find( { "ssn" : { $in : [ "001-29-9184", "177-45-0950", "265-67-9973" ] } } )

    // explain a query where only part of the predicates use a index
    exp.find( { "ssn" : { $in : [ "001-29-9184", "177-45-0950", "265-67-9973" ] }, last_name : { $gte : "H" } } )

## Sorting with indexes

In this video, I'd like to discuss how we can utilize indexes to sort the documents in our queries.

We already know that any query can also be sorted.

Now there are two ways that these documents can be sorted.

They can be sorted in memory or by using an index.

Let's talk about the first case, when sorting happens in memory.

The documents in our collections are stored on disk in an unknown order, therefore, when we query the server our documents are going to be returned in the same order that the server finds them.

If we happen to want them in the same order that the server finds them, great, but that's not very likely to happen.

This means that when we add a sort, the server is going have to read the documents from disk into RAM.

And then in RAM, it will perform some kind of sorting algorithm on them.

Depending on how many documents you have, this might take a long time.

Moreover, because sorting a large amount of documents in memory might be an expensive operation, the server is going to abort sorting in memory when 32 megabytes of memory is being used.

This leads nicely in how we can sort with indexes.

In an index, the keys are ordered according to the field specified during index creation.

The server can take advantage of this via sort.

If a query is using an index scan, the order of the documents returned is guaranteed to be sorted by the index keys.

This means that there is no need to reform an explicit sort, as the documents will be fetched from the server in the sorted order.

Now it's important to point out that these documents are only going to be ordered according to the fields that make up the index.

If we have an ascending index on last name, then the documents will be ordered according to last name.

If there's an index on first name, then the documents will be ordered according to first name.

It's important to note that the query planner considers indexes that can be helpful to either the query predicate or to the requested sort.

Let's dig a little bit deeper into sorting functionality with single field indexes by looking at the shell.

For this lesson, we're going to continue using the people.json data set that we have been using in the previous lessons.

If you haven't got it, you can go ahead and import it with Mongo import.

If you can, go ahead and skip this step.

Let's go ahead and find all the documents in our collection sorted by social security number.

Here are the first 20 documents.

You can see that we're finding everyone, and we're sorting by the social security number.

Let's go ahead and create our explainable object and use it to explain our query.

If we go to executionStats, you can see that we had to look at 50,000 documents to return our 50,000 documents, which makes sense.

But you also notice that we used 50,000 index keys and that's because we still did an index scan.

Well, why did we do an index scan if we needed to return all the documents?

Well that's because the index wasn't used for filtering documents, but was rather used for sorting.

If I instead sort on first name, which we don't have an index for.

If we scroll up to executionStats, you'll see that we still examined 50,000 documents because we returned 50,000 documents.

But this time we looked at no index keys, and that's because we did in memory SORT.

So we effectively did a collection scan, read all the documents into memory, and then once they are in memory, we were able to do an in memory SORT on the unsorted documents for the first name field.

In our previous index sorted example, we sorted by security number ascending.

This time let's sort by it descending.

And then we look at our output, you'll see that we're still doing an index scan.

This is because this time we're able to walk our index backwards instead of walking it forwards.

When we're sorting with a single field index, we can always do that.

I can sort my documents either ascending or descending, regardless of the physical ordering of the index keys.

I can both filter and sort in the same query.

Here I'm finding everyone whose social security number starts with 555.

And then we look at the explain output, you can see that we did an index scan.

And this index scan was used both for filtering and sorting the documents because we can see that we only had to look at 49 documents, which were the same 49 that we were returned.

Just to verify things, let's see what would happen if we built descending index keys.

We'll first drop our old indexes.

And now we'll create another index on social security number, but this time descending instead of ascending.

And so now, when I search for social security numbers beginning with 555, and I sort them the descending, we're now walking the index forward because it is a descending index.

Whereas before, we were walking it backwards because it was an ascending index.

This may seem unimportant, but it will become more important later when we discuss compound indexes.

Let's recap what we've learned.

In this lesson you learned how to use indexes to sort.

And how to create indexes that both match and sort on a single query.

And you also learned how to create indexes that can be used for ascending and descending sort orders.
