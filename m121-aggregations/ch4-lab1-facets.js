var pipeline = [{
        $match: {
            "imdb.rating": {
                $gt: 0
            },
            "metacritic": {
                $gt: 0
            }
        }
    },
    {
        $facet: {
            "topTenImdb": [{
                    $sort: {
                        "imdb.rating": -1
                    }
                },
                {
                    $limit: 10
                }
            ],
            "topTenMetacritic": [{
                    $sort: {
                        "metacritic": -1
                    }
                },
                {
                    $limit: 10
                }
            ]
        }
    },
    {
        $project: {
            "commonTopFilms": {
                $size: {
                    $setIntersection: ["$topTenImdb", "$topTenMetacritic"]
                }
            }
        }
    }
];