# M121: The MongoDB Aggregation Framework

mongo "mongodb://cluster0-shard-00-00-jxeqq.mongodb.net:27017,cluster0-shard-00-01-jxeqq.mongodb.net:27017,cluster0-shard-00-02-jxeqq.mongodb.net:27017/aggregations?replicaSet=Cluster0-shard-0" --authenticationDatabase admin --ssl -u m121 -p aggregations --norc

[Aggregation Pipeline Quick Reference](https://docs.mongodb.com/manual/meta/aggregation-quick-reference/)

## Example Aggregation Pipeline

- Pipelines are always an array of one or more stages
- Stages are composed of one or more aggregation operators or expressions
- Expressions may take a single argument or an array of arguments. This is expression dependant.
- Some expressions can only be used in certain stages. For example, accumulator expressions can only be used within the $group stage, with select accumulator expressions available in the $project stage.

Example document:

    // simple first example
    db.solarSystem.aggregate([{
        "$match": {
            "atmosphericComposition": { "$in": [/O2/] },
            "meanTemperature": { $gte: -40, "$lte": 40 }
        }
        }, {
        "$project": {
            "_id": 0,
            "name": 1,
            "hasMoons": { "$gt": ["$numberOfMoons", 0] }
        }
        }], { "allowDiskUse": true});

## Match stages

- Must be used as a filter for documents through the pipeline.
- Uses the MongoDB query language query operators to express queries.
- Should come early (first) in pipeline: The earlier in the pipeline, the more efficient our pipelines will become. Not only because we will expression filters that reduce the number of documents to process, but also the fact that we might be using indexes withing the pipeline execution.
- May contain a \$text query operator but must be first stage in the pipeline.
- Cannot use with \$where or with projection.

Examples:

    // $match all celestial bodies, not equal to Star
    db.solarSystem.aggregate([{
        "$match": { "type": { "$ne": "Star" } }
    }]).pretty()

    // same query using find command
    db.solarSystem.find({
        "type": { "$ne": "Star" }
    }).pretty();

    // count the number of matching documents
    db.solarSystem.count();

    // using $count
    db.solarSystem.aggregate([{
        "$match": { "type": { "$ne": "Star"} }
    }, {
        "$count": "planets"
    }]);

    // matching on value, and removing ``_id`` from projected document
    db.solarSystem.find({"name": "Earth"}, {"_id": 0});

## Project stages

- Once we specify one field to retain, we must specify all fields we want to retain. The \_id fields is the only exception to this.
- Beyond simply removing and retaining fields, \$project lets us add new fields
- \$project can be used as many times as required within an Aggregation pipeline
- \$project can be used to reassign values to existing field names and to derive entirely new fields

Examples

    // project ``name`` and remove ``_id``
    db.solarSystem.aggregate([{ "$project": { "_id": 0, "name": 1 } }]);

    // project ``name`` and ``gravity`` fields, including default ``_id``
    db.solarSystem.aggregate([{ "$project": { "name": 1, "gravity": 1 } }]);

    // using dot-notation to express the projection fields
    db.solarSystem.aggregate([{ "$project": { "_id": 0, "name": 1, "gravity.value": 1 } }]);

    // reassing ``gravity`` field with value from ``gravity.value`` embeded field
    db.solarSystem.aggregate([{"$project": { "_id": 0, "name": 1, "gravity": "$gravity.value" }}]);

    // creating a document new field ``surfaceGravity``
    db.solarSystem.aggregate([{"$project": { "_id": 0, "name": 1, "surfaceGravity": "$gravity.value" }}]);

    // creating a new field ``myWeight`` using expressions
    db.solarSystem.aggregate([{"$project": { "_id": 0, "name": 1, "myWeight": { "$multiply": [ { "$divide": [ "$gravity.value", 9.8 ] }, 86 ] } }}]);

## Utilities stages

### AddFields

    // reassign ``gravity`` field value
    db.solarSystem.aggregate([{"$project": { "gravity": "$gravity.value" } }]);

    // adding ``name`` and removing ``_id`` from projection
    db.solarSystem.aggregate([{"$project": { "_id": 0, "name": 1, "gravity": "$gravity.value" } }])''

    // adding more fields to the projected document
    db.solarSystem.aggregate([
    {"$project":{
        "_id": 0,
        "name": 1,
        "gravity": "$gravity.value",
        "meanTemperature": 1,
        "density": 1,
        "mass": "$mass.value",
        "radius": "$radius.value",
        "sma": "$sma.value" }
    }]);

    // using ``$addFields`` to generate the new computed field values
    db.solarSystem.aggregate([
    {"$addFields":{
        "gravity": "$gravity.value",
        "mass": "$mass.value",
        "radius": "$radius.value",
        "sma": "$sma.value"}
    }]);

    // combining ``$project`` with ``$addFields``
    db.solarSystem.aggregate([
    {"$project": {
        "_id": 0,
        "name": 1,
        "gravity": 1,
        "mass": 1,
        "radius": 1,
        "sma": 1}
    },
    {"$addFields": {
        "gravity": "$gravity.value",
        "mass": "$mass.value",
        "radius": "$radius.value",
        "sma": "$sma.value"
    }}]);

### geoNear

- geoNear must be the first stage within a pipeline
- Collection can have only one 2dsphere index
- 2dsphere returns distance in meters. Legacy coordinates returns in radians.
- Why Not near?
  - geoNear can be used on sharded cluster
  - geoNear can use other indexes

Example

    // using ``$geoNear`` stage
    db.nycFacilities.aggregate([
    {
        "$geoNear": {
        "near": {
            "type": "Point",
            "coordinates": [-73.98769766092299, 40.757345233626594]
        },
        "distanceField": "distanceFromMongoDB",
        "spherical": true
        }
    }
    ]).pretty();

    // include ``limit`` to results
    db.nycFacilities.aggregate([
    {
        $geoNear: {
        near: {
            type: "Point",
            coordinates: [-73.98769766092299, 40.757345233626594]
        },
        distanceField: "distanceFromMongoDB",
        spherical: true,
        query: { type: "Hospital" },
        limit: 5
        }
    }
    ]).pretty()

### Cursor-like stages

- $sort, $skip, $limit, and $count are functionally equivalent to the similarly named cursor methpds
- \$sort can take advantage of inexes if used early within a pipeline
- By default \$sort uses up to 100MB of RAM. Setting allowDiskUse: true will allow larger sorts

Examples

    // project fields ``numberOfMoons`` and ``name``
    db.solarSystem.find({}, {"_id": 0, "name": 1, "numberOfMoons": 1}).pretty();

    // count the number of documents
    db.solarSystem.find({}, {"_id": 0, "name": 1, "numberOfMoons": 1}).count();

    // skip documents
    db.solarSystem.find({}, {"_id": 0, "name": 1, "numberOfMoons": 1}).skip(5).pretty();

    // limit documents
    db.solarSystem.find({}, {"_id": 0, "name": 1, "numberOfMoons": 1}).limit(5).pretty();

    // sort documents
    db.solarSystem.find({}, { "_id": 0, "name": 1, "numberOfMoons": 1 }).sort( {"numberOfMoons": -1 } ).pretty();

    // ``$limit`` stage
    db.solarSystem.aggregate([{
    "$project": {
        "_id": 0,
        "name": 1,
        "numberOfMoons": 1
    }
    },
    { "$limit": 5  }]).pretty();

    // ``skip`` stage
    db.solarSystem.aggregate([{
    "$project": {
        "_id": 0,
        "name": 1,
        "numberOfMoons": 1
    }
    }, {
    "$skip": 1
    }]).pretty()

    // ``$count`` stage
    db.solarSystem.aggregate([{
    "$match": {
        "type": "Terrestrial planet"
    }
    }, {
    "$project": {
        "_id": 0,
        "name": 1,
        "numberOfMoons": 1
    }
    }, {
    "$count": "terrestrial planets"
    }]).pretty();

    // removing ``$project`` stage since it does not interfere with our count
    db.solarSystem.aggregate([{
    "$match": {
        "type": "Terrestrial planet"
    }
    }, {
    "$count": "terrestrial planets"
    }]).pretty();


    // ``$sort`` stage
    db.solarSystem.aggregate([{
    "$project": {
        "_id": 0,
        "name": 1,
        "numberOfMoons": 1
    }
    }, {
    "$sort": { "numberOfMoons": -1 }
    }]).pretty();

    // sorting on more than one field
    db.solarSystem.aggregate([{
    "$project": {
        "_id": 0,
        "name": 1,
        "hasMagneticField": 1,
        "numberOfMoons": 1
    }
    }, {
    "$sort": { "hasMagneticField": -1, "numberOfMoons": -1 }
    }]).pretty();

    // setting ``allowDiskUse`` option
    db.solarSystem.aggregate([{
    "$project": {
        "_id": 0,
        "name": 1,
        "hasMagneticField": 1,
        "numberOfMoons": 1
    }
    }, {
    "$sort": { "hasMagneticField": -1, "numberOfMoons": -1 }
    }], { "allowDiskUse": true }).pretty();

### Sample stage

    // sampling 200 documents of collection ``nycFacilities``
    db.nycFacilities.aggregate([{"$sample": { "size": 200 }}]).pretty();

## Group stage

- \_id is where to specifiy what incoming documents should be grouped on
- can use all accumulator expressions within \$group
- \$group can be used multiple times within a pipeline
- may be necessary to sanitize incoming data

Example

    // grouping by year and getting a count per year using the { $sum: 1 } pattern
    db.movies.aggregate([
    {
        "$group": {
        "_id": "$year",
        "numFilmsThisYear": { "$sum": 1 }
        }
    }
    ])

    // grouping as before, then sorting in descending order based on the count
    db.movies.aggregate([
    {
        "$group": {
        "_id": "$year",
        "count": { "$sum": 1 }
        }
    },
    {
        "$sort": { "count": -1 }
    }
    ])

    // grouping on the number of directors a film has, demonstrating that we have to
    // validate types to protect some expressions
    db.movies.aggregate([
    {
        "$group": {
        "_id": {
            "numDirectors": {
            "$cond": [{ "$isArray": "$directors" }, { "$size": "$directors" }, 0]
            }
        },
        "numFilms": { "$sum": 1 },
        "averageMetacritic": { "$avg": "$metacritic" }
        }
    },
    {
        "$sort": { "_id.numDirectors": -1 }
    }
    ])

    // showing how to group all documents together. By convention, we use null or an
    // empty string, ""
    db.movies.aggregate([
    {
        "$group": {
        "_id": null,
        "count": { "$sum": 1 }
        }
    }
    ])

    // filtering results to only get documents with a numeric metacritic value
    db.movies.aggregate([
    {
        "$match": { "metacritic": { "$gte": 0 } }
    },
    {
        "$group": {
        "_id": null,
        "averageMetacritic": { "$avg": "$metacritic" }
        }
    }
    ])

### Accumulators

Example

    // run to get a view of the document schema
    db.icecream_data.findOne()

    // using $reduce to get the highest temperature
    db.icecream_data.aggregate([
    {
        "$project": {
        "_id": 0,
        "max_high": {
            "$reduce": {
            "input": "$trends",
            "initialValue": -Infinity,
            "in": {
                "$cond": [
                { "$gt": ["$$this.avg_high_tmp", "$$value"] },
                "$$this.avg_high_tmp",
                "$$value"
                ]
            }
            }
        }
        }
    }
    ])

    // performing the inverse, grabbing the lowest temperature
    db.icecream_data.aggregate([
    {
        "$project": {
        "_id": 0,
        "min_low": {
            "$reduce": {
            "input": "$trends",
            "initialValue": Infinity,
            "in": {
                "$cond": [
                { "$lt": ["$$this.avg_low_tmp", "$$value"] },
                "$$this.avg_low_tmp",
                "$$value"
                ]
            }
            }
        }
        }
    }
    ])

    // note that these two operations can be done with the following operations can
    // be done more simply. The following two expressions are functionally identical

    db.icecream_data.aggregate([
    { "$project": { "_id": 0, "max_high": { "$max": "$trends.avg_high_tmp" } } }
    ])

    db.icecream_data.aggregate([
    { "$project": { "_id": 0, "min_low": { "$min": "$trends.avg_low_tmp" } } }
    ])

    // getting the average and standard deviations of the consumer price index
    db.icecream_data.aggregate([
    {
        "$project": {
        "_id": 0,
        "average_cpi": { "$avg": "$trends.icecream_cpi" },
        "cpi_deviation": { "$stdDevPop": "$trends.icecream_cpi" }
        }
    }
    ])

    // using the $sum expression to get total yearly sales
    db.icecream_data.aggregate([
    {
        "$project": {
        "_id": 0,
        "yearly_sales (millions)": { "$sum": "$trends.icecream_sales_in_millions" }
        }
    }
    ])

### Unwind stage

Example

    // finding the top rated genres per year from 2010 to 2015...
    db.movies.aggregate([
    {
        "$match": {
        "imdb.rating": { "$gt": 0 },
        "year": { "$gte": 2010, "$lte": 2015 },
        "runtime": { "$gte": 90 }
        }
    },
    {
        "$unwind": "$genres"
    },
    {
        "$group": {
        "_id": {
            "year": "$year",
            "genre": "$genres"
        },
        "average_rating": { "$avg": "$imdb.rating" }
        }
    },
    {
        "$sort": { "_id.year": -1, "average_rating": -1 }
    }
    ])

    // unfortunately we got too many results per year back. Rather than peform some
    // other complex grouping and matching, we just append a simple group and sort
    // stage, taking advantage of the fact the documents are in the order we want
    db.movies.aggregate([
    {
        "$match": {
        "imdb.rating": { "$gt": 0 },
        "year": { "$gte": 2010, "$lte": 2015 },
        "runtime": { "$gte": 90 }
        }
    },
    {
        "$unwind": "$genres"
    },
    {
        "$group": {
        "_id": {
            "year": "$year",
            "genre": "$genres"
        },
        "average_rating": { "$avg": "$imdb.rating" }
        }
    },
    {
        "$sort": { "_id.year": -1, "average_rating": -1 }
    },
    {
        "$group": {
        "_id": "$_id.year",
        "genre": { "$first": "$_id.genre" },
        "average_rating": { "$first": "$average_rating" }
        }
    },
    {
        "$sort": { "_id": -1 }
    }
    ])

## Lookup stage

- the from collection cannot be sharded
- the from collection must be in the same database
- the values in localField and foreignField are matched on equality
- as can be any name, if field already exists it will be overwritten

Example

    // familiarizing with the air_alliances schema
    db.air_alliances.findOne()

    // familiarizing with the air_airlines schema
    db.air_airlines.findOne()

    // performing a lookup, joining air_alliances with air_airlines and replacing
    // the current airlines information with the new values
    db.air_alliances
    .aggregate([
        {
        "$lookup": {
            "from": "air_airlines",
            "localField": "airlines",
            "foreignField": "name",
            "as": "airlines"
        }
        }
    ])
    .pretty()

## graphLookup

- \$graphLookup provides MongoDB with graph or graph-like capabilities
- \$graphLookup provides MongoDB a transitive closure implementation
- connectFromField value will be used to match connectToField in a recursive match
- connectToField will be used on recursive find operations
- maxDepth allows to specify the number of recursive lookups
- depthField determines a field, in the result document, which specifies the number of recursive lookups needed to reach that document
- memory allocation may require \$allowDiskUse
- indexes might accelerate queries, having connectToField index is a mist
- from collection cannot be sharded
- graphLookups can be jused in any position of the pipeline and acts in the same wqay as a regular lookup

## Facets

### Single facets

Single query facets are supported by the new aggregation pipeline stage $sortByCount. As like any other aggregation pipelines, except for $out, we can use the output of this stage, as input for downstream stages and operators, manipulating the dataset accordingly.

find companies matching term `networking` using text search

    db.companies.aggregate([
        {"$match": { "$text": {"$search": "network"}  }  }] )

\$sortByCount single query facet for the previous search

    db.companies.aggregate([
    {"$match": { "$text": {"$search": "network"}  }  },
    {"$sortByCount": "$category_code"}] )

extend the pipeline for a more elaborate facet

    db.companies.aggregate([
    {"$match": { "$text": {"$search": "network"}  }  } ,
    {"$unwind": "$offices"},
    {"$match": { "offices.city": {"$ne": ""}  }}   ,
    {"$sortByCount": "$offices.city"}] )

### Bucket stage

Only works on single value.
Use default is recommended, fails when 1 doc doesn't have a value that can be grouped on.

    db.movies.aggregate([
    {
        "$bucket": {
        "groupBy": "$imdb.rating",
        "boundaries:" [0, 5, 8, Infinity],
        "default": "not rated"
        }
    }
    ])

to get the count per bucket after we have specified an output, we have to explicitly calculate it

    db.movies.aggregate([
    {
        "$bucket": {
        "groupBy": "$imdb.rating",
        "boundaries": [0, 5, 8, Infinity],
        "default": "not rated",
        "output": {
            "average_per_bucket": { "$avg": "$imdb.rating" },
            "count": { "$sum": 1 }
        }
        }
    }
    ])

### Manual buckets

Use self-defined ranges and group documents into buckets.

    db.companies.aggregate([{
            "$match": {
                "founded_year": {
                    "$gt": 1980
                }
            }
        },
        {
            "$bucket": {
                "groupBy": "$number_of_employees",
                "boundaries": [0, 20, 50, 100, 500, 1000, Infinity],
                "default": "Other"
            }
        }
    ])

    db.companies.aggregate([{
            "$match": {
                "founded_year": {
                    "$gt": 1980
                }
            }
        },
        {
            "$bucket": {
                "groupBy": "$number_of_employees",
                "boundaries": [0, 20, 50, 100, 500, 1000, Infinity],
                "default": "Other",
                "output": {
                    "total": {
                        "$sum": 1
                    },
                    "average": {
                        "$avg": "$number_of_employees"
                    },
                    "categories": {
                        "$addToSet": "\$category_code"
                    }
                }
            }
        }
    ])

### Bucket auto stage

- Auto Bucketing will, given a number of buckets, try to distribute documents evenly across buckets.
- Auto Bucketing will adhere bucket boundaries to a numerical series set by the granularity option
- It skips record with missing/incompatible fields

Example

    db.movies.aggregate([{
            "$match": {
                "imdb.rating": {
                    "$gte": 0
                }
            }
        },
        {
            "$bucketAuto": {
                "groupBy": "$imdb.rating",
                "buckets": 4,
                "output": {
                    "average_per_bucket": {
                        "$avg": "$imdb.rating"
                    },
                    "count": {
                        "$sum": 1
                    }
                }
            }
        }
    ])

    db.companies.aggregate([{
            "$match": {
                "offices.city": "New York"
            }
        },
        {
            "$bucketAuto": {
                "groupBy": "$founded_year",
                "buckets": 5
            }
        }
    ])

You can also use predefined cardinality used in industries.

    db.granularity_test.aggregate([{
        "$bucketAuto": {
            "groupBy": "$powers_of_2",
            "buckets": 10,
            "granularity": "POWERSOF2"
        }
    }])

    db.series.aggregate({
        $bucketAuto: {
            groupBy: "$\_id",
            buckets: 5,
            granularity: "R20"
        }
    })

    db.companies.aggregate([{
            "$match": {
                "offices.city": "New York"
            }
        },
        {
            "$bucketAuto": {
                "groupBy": "$founded_year",
                "buckets": 5,
                "output": {
                    "total": {
                        "$sum": 1
                    },
                    "average": {
                        "$avg": "$number_of_employees"
                    }
                }
            }
        }
    ])

### \$facet - Multiple facets

- allows several sub-pipelines to be executed to produce multiple facets.
- allows the applications to generate several different facets with one single database request.
- allows other stages to be included on the sub-pipelines, except for: $facet, $out, $geoNear, $indexStats, \$collStats
- the sub-pipelines, defined for each individual facet, cannot share their output accross other parallel facets. Each sub-pipeline will receive the same input data set but does not share the result dataset with parallel facets.

Example

    db.companies.aggregate([{
            "$match": {
                "$text": {
                    "$search": "Databases"
                }
            }
        },
        {
            "$facet": {
                "Categories": [{
                    "$sortByCount": "$category_code"
                }],
                "Employees": [{
                        "$match": {
                            "founded_year": {
                                "$gt": 1980
                            }
                        }
                    },
                    {
                        "$bucket": {
                            "groupBy": "$number_of_employees",
                            "boundaries": [0, 20, 50, 100, 500, 1000, Infinity],
                            "default": "Other"
                        }
                    }
                ],
                "Founded": [{
                        "$match": {
                            "offices.city": "New York"
                        }
                    },
                    {
                        "$bucketAuto": {
                            "groupBy": "$founded_year",
                            "buckets": 5
                        }
                    }
                ]
            }
        }
    ]).pretty()

### \$sortByCount

Performing a group followed by a sort to rank occurence

    db.movies.aggregate([
    {
        "$group": {
        "_id": "$imdb.rating",
        "count": { "$sum": 1 }
        }
    },
    {
        "$sort": { "count": -1 }
    }
    ])

\$sortByCount is equivalent to the above. In fact, if you execute this pipeline with { explain: true } you will see that it is transformed to the above!

    db.movies.aggregate([
    {
        "$sortByCount": "$imdb.rating"
    }
    ])

### summary

\$bucket

- Must always specify at least 2 values to boundaries
- boundaries must all be of the same general type (Numeric, String)
- count is inserted by default with no output, but removed when output is specified

\$bucketAuto

- Cardinality of groupBy expression may impact even distribution and nr of buckets
- Specifying a granularity requires the expression to groupBy ro resolve to a numeric value

\$sortByCount

- is equivalent to a group stage to count occurrence, and then sorting in descending order

## \$redact

Used for field level redaction.

- '$$KEEP' and '$$PRUNE' automatically apply to all levels below the evaluated level
- '\$\$DESCEND' retains the current level and evaluates the next level down.
- \$redact is not for restricting access to a collection

Example

    var userAccess = "Management"
    db.employees.aggregate([{
        "$redact": {
            "$cond": [{ "$in": [userAccess, "$acl"] }, "$$DESCEND", "$$PRUNE"]
            }
        }]).pretty()

## \$out

- $out must be the last stage in a pipeline, and is not allowed within a $facet stage.
- All indexes on an existing collection are rebuilt when \$out overwrites the collection, so must be honored.
- \$out will overwrite an existing collection if specified.
- \$out will not create a new collection or overwrite an existing collection if the pipeline errors.

## views

Views are read-only and contain no information themselves. The documents "in" a view are simply the result of the defining pipeline being executed.

- views contain no data themselves, are created on demand and reflect data in the source collection
- horizontal slicing: \$match
- vertical slicing: \$project or shaping stage
- read only: no write operations, no index operations (create, update), no renaming
- view definitions are public
- collation restrictions
- restrictions: must abide by the rules of the Aggregation Framework; cannot use find operations:
  - find() operations with projection operators are not permitted ($, $elemMatch, $slice, $meta)
  - no mapReduce
  - no \$text
  - no \$geoNear (must be first stage)

Example

    db.createView("bronze_banking", "customers", [
    {
        "$match": { "accountType": "bronze" }
    },
    {
        "$project": {
        "_id": 0,
        "name": {
            "$concat": [
            { "$cond": [{ "$eq": ["$gender", "female"] }, "Miss", "Mr."] },
            " ",
            "$name.first",
            " ",
            "$name.last"
            ]
        },
        "phone": 1,
        "email": 1,
        "address": 1,
        "account_ending": { "$substr": ["$accountNumber", 7, -1] }
        }
    }
    ])

    // getting all collections in a database and seeing their information
    db.getCollectionInfos()

    // getting information on views only
    db.system.views.find()

## Performance

- The Aggregation Framework can automatically project fields if the shape of the final document is only dependent upon those fields in the input document.
- The query in a \$match stage can be entirely covered by an index
- Causing a merge in a sharded deployment will cause all subsequent pipeline stages to be performed in the same location as the merge
- The Aggregation Framework will automatically reorder stages in certain conditions
- Indexes used as of stage 1. Whenever indexes can no longer be used, they can never again be used in the pipeline.
- When $limit and $sort are close together a very performant top-k sort can be performed
- Transforming data in a pipeline stage prevents us from using indexes in the stages that follow
- Results returned limited to 16MB document. In the pipeline this can be anything. use $limit and $project.
- 100MB RAM per stage. Use indexes and allowDiskUse:true

Example

    // an initial aggregation finding all movies where the title begins
    // with a vowel. Notice the $project stage that will prevent a covered
    // query!
    db.movies.aggregate([
    {
        $match: {
        title: /^[aeiou]/i
        }
    },
    {
        $project: {
        title_size: { $size: { $split: ["$title", " "] } }
        }
    },
    {
        $group: {
        _id: "$title_size",
        count: { $sum: 1 }
        }
    },
    {
        $sort: { count: -1 }
    }
    ])

    // showing the query isn't covered
    db.movies.aggregate(
    [
        {
        $match: {
            title: /^[aeiou]/i
        }
        },
        {
        $project: {
            title_size: { $size: { $split: ["$title", " "] } }
        }
        },
        {
        $group: {
            _id: "$title_size",
            count: { $sum: 1 }
        }
        },
        {
        $sort: { count: -1 }
        }
    ],
    { explain: true }
    )

    // this is better, we are projecting away the _id field. But this seems like
    // a lot of manual work...
    db.movies.aggregate([
    {
        $match: {
        title: /^[aeiou]/i
        }
    },
    {
        $project: {
        _id: 0,
        title_size: { $size: { $split: ["$title", " "] } }
        }
    },
    {
        $group: {
        _id: "$title_size",
        count: { $sum: 1 }
        }
    },
    {
        $sort: { count: -1 }
    }
    ])

    // verifying that it is a covered query
    db.movies.aggregate(
    [
        {
        $match: {
            title: /^[aeiou]/i
        }
        },
        {
        $project: {
            _id: 0,
            title_size: { $size: { $split: ["$title", " "] } }
        }
        },
        {
        $group: {
            _id: "$title_size",
            count: { $sum: 1 }
        }
        },
        {
        $sort: { count: -1 }
        }
    ],
    { explain: true }
    )

    // can we... do this? Yes, yes we can.
    db.movies.aggregate([
    {
        $match: {
        title: /^[aeiou]/i
        }
    },
    {
        $group: {
        _id: {
            $size: { $split: ["$title", " "] }
        },
        count: { $sum: 1 }
        }
    },
    {
        $sort: { count: -1 }
    }
    ])

    // proof
    db.movies.aggregate(
    [
        {
        $match: {
            title: /^[aeiou]/i
        }
        },
        {
        $group: {
            _id: {
            $size: { $split: ["$title", " "] }
            },
            count: { $sum: 1 }
        }
        },
        {
        $sort: { count: -1 }
        }
    ],
    { explain: true }
    )

    // and a very succinct way of expressing what we wanted all along
    db.movies.aggregate([
    {
        $match: {
        title: /^[aeiou]/i
        }
    },
    {
        $sortByCount: {
        $size: { $split: ["$title", " "] }
        }
    }
    ])

    // a naive way to get teh number of trades by action. We unwind the trades
    // array first thing. We get the results we want, but maybe there is a better
    // way
    db.stocks.aggregate([
    {
        $unwind: "$trades"
    },
    {
        $group: {
        _id: {
            time: "$id",
            action: "$trades.action"
        },
        trades: { $sum: 1 }
        }
    },
    {
        $group: {
        _id: "$_id.time",
        actions: {
            $push: {
            type: "$_id.action",
            count: "$trades"
            }
        },
        total_trades: { $sum: "$trades" }
        }
    },
    {
        $sort: { total_trades: -1 }
    }
    ])

    // working within the arrays is always better if we want to do analysis within
    // a document. We get the same results in a slighlty easier to work with format
    // and didn't incur the cost of a $group stage
    db.stocks.aggregate([
    {
        $project: {
        buy_actions: {
            $size: {
            $filter: {
                input: "$trades",
                cond: { $eq: ["$$this.action", "buy"] }
            }
            }
        },
        sell_actions: {
            $size: {
            $filter: {
                input: "$trades",
                cond: { $eq: ["$$this.action", "sell"] }
            }
            }
        },
        total_trades: { $size: "$trades" }
        }
    },
    {
        $sort: { total_trades: -1 }
    }
    ])

    // remember, expression composition is powerful. Be creative, and things
    // that can be done inline. Notice that there is no intermediary stage to
    // filter the trades array first, it's just done as part of the argument to
    // the reduce expression.

    db.stocks.aggregate([
    {
        $project: {
        _id: 0,
        mdb_only: {
            $reduce: {
            input: {
                $filter: {
                input: "$trades",
                cond: { $eq: ["$$this.ticker", "MDB"] }
                }
            },
            initialValue: {
                buy: { total_count: 0, total_value: 0 },
                sell: { total_count: 0, total_value: 0 }
            },
            in: {
                $cond: [
                { $eq: ["$$this.action", "buy"] },
                {
                    buy: {
                    total_count: { $add: ["$$value.buy.total_count", 1] },
                    total_value: {
                        $add: ["$$value.buy.total_value", "$$this.price"]
                    }
                    },
                    sell: "$$value.sell"
                },
                {
                    sell: {
                    total_count: { $add: ["$$value.sell.total_count", 1] },
                    total_value: {
                        $add: ["$$value.sell.total_value", "$$this.price"]
                    }
                    },
                    buy: "$$value.buy"
                }
                ]
            }
            }
        }
        }
    }
    ])
