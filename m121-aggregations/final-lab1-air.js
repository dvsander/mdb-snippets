var pipeline = [{
        $match: {
            $or: [{
                    $and: [{
                        "src_airport": "JFK"
                    }, {
                        "dst_airport": "LHR"
                    }]
                },
                {
                    $and: [{
                        "src_airport": "LHR"
                    }, {
                        "dst_airport": "JFK"
                    }]
                }
            ]
        }
    },
    {
        $lookup: {
            from: "air_alliances",
            localField: "airline.name",
            foreignField: "airlines",
            as: "alliance"
        }
    },
    {
        $group: {
            "_id": '$airline.id',
            "alliance": {
                $first: "$alliance"
            }
        }
    },
    {
        $match: {
            "alliance": {
                $not: {
                    $size: 0
                }
            }
        }
    },
    {
        $sortByCount: "$alliance"
    },
    {
        $limit: 1
    }
];

db.air_routes.aggregate(pipeline)