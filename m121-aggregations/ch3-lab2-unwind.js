var pipeline = [{
        $unwind: "$cast"
    },
    {
        $group: {
            "_id": "$cast",
            "numFilms": {
                $sum: 1
            },
            "average": {
                $avg: "$imdb.rating"
            }
        }
    },
    {
        $sort: {
            "numFilms": -1
        }
    },
    {
        $limit: 1
    }
];