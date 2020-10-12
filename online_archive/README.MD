# Prepare

M10+ cluster in any region of any cloud provider.

The standard data set is 

    1984-03-05T12:00:00Z
    1984-03-13T18:00:00Z

Need to make more recent.

Use https://www.timeanddate.com/date/durationresult.html?d1=05&m1=03&y1=1984&d2=13&m2=8&y2=2020

And update with number of days. Make sure it overlaps.

    db.data_before.updateMany(
    { ts : { $exists : true } },
    [{ $set: { ts: { $add: ["$ts", 13300*24*60*60000] } } }]
    )

`{ "acknowledged" : true, "matchedCount" : 10000, "modifiedCount" : 10000 }`

test it out

    db.data.find({}, { _id: 0, ts: 1  }).sort({ts:1}).limit(1)
    db.data.find({}, { _id: 0, ts: 1  }).sort({ts:-1}).limit(1)

range is now 3-08-2020 to 11-08-2020

# Configure

enable online archive

    sample_weatherdata.data
    ts

    we are 13-8 today so if we set it to 7
        => data before 6/8 will be archived
        => data after 6/8 will be in atlas

add dataSource as partition

execute the query

    db.data.find({ ts: { $lte: new Date(ISODate().getTime() - 1000 * 3600 * 24 * 7)}}).sort({ ts: 1 }).count()

it should NOT be 0 and NOT be 10.000

RUN IT

# Validate

The regular connection string: document count is halved.

Connect to cluster and online archive: all documents are returned.






