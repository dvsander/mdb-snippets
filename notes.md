# MongoDB Technical Training notes

These are notes taken during technical training and experimenting with MongoDB.

## Connection Details

    #Class cluster: READ ONLY BY DESIGN
    mongo "mongodb://cluster0-shard-00-00-jxeqq.mongodb.net:27017,cluster0-shard-00-01-jxeqq.mongodb.net:27017,cluster0-shard-00-02-jxeqq.mongodb.net:27017/test?replicaSet=Cluster0-shard-0" --authenticationDatabase admin --ssl --username m001-student

    #Self cluster: 2FA PROTECTED + TRAINING ENVIRONMENT (NO SUBSCRIPTION)
    mongo "mongodb+srv://cluster-nhtt-oip7z.gcp.mongodb.net/test" --ssl --username m001-student

## Data types

Read more: [MongoDB BSON Data Types](https://docs.mongodb.com/manual/reference/bson-types/)

MongoDB was built on top of the JSON spec. Additional data types were added.

- JSON Types
  - object
  - array
  - string
  - number
  - bool ("true"/"false")
  - null ("null")
- BSON Types
  - int
  - long
  - decimal (since 3.4)
  - double
  - date
  - ObjectId
- Advanced
  - bindata
  - regex
  - javascript
  - javascriptWithScope
  - minKey
  - maxKey

## Use cases

### Navigating via command line

    show databases
    use database
    show collections
    db.collection.find().pretty()

### Executing javascript on the host machine

Navigate to the directory on the host where the .js file is located. Start up the mongo connection. Use the load() command to run the script inside the shell.

    load("loadMovieDetailsDataset.js")

### Inserting one single document

    db.moviesScratch.insertOne({title:"Star Trek II: The Wrath of Khan", year: 1982, imdb: "tt0084726"})

### Instering multiple documents

#### Ordered (standard): stops on error, e.g. duplicate key

    insertMany([array of docs to insert])

#### Unordered: continue on error

    insertMany([array of docs to insert], { "ordered" : false })

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

    find({filter},{projections})
    find({runtime: {$gte: 90, $lte: 120}}, {_id: 0, title: 1, runtime: 1})

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

$elemMatch:
