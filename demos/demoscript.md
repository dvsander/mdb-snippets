# Useful demo commands

mongoimport --host cluster-dev-shard-0/cluster-dev-shard-00-00-4fyyo.mongodb.net:27017,cluster-dev-shard-00-01-4fyyo.mongodb.net:27017,cluster-dev-shard-00-02-4fyyo.mongodb.net:27017 --ssl --username admin --password nimda --authenticationDatabase admin --db test --collection m500 --file m500file.json

mongoimport --host  --ssl --username admin --password <PASSWORD> --authenticationDatabase admin --db <DATABASE> --collection <COLLECTION> --type <FILETYPE> --file <FILENAME>

    date; mgeneratejs mgenerate500.json -n 10000 | mongoimport --host cluster-dev-shard-0/cluster-dev-shard-00-00-4fyyo.mongodb.net:27017,cluster-dev-shard-00-01-4fyyo.mongodb.net:27017,cluster-dev-shard-00-02-4fyyo.mongodb.net:27017 --ssl --username admin --password nimda --authenticationDatabase admin --db test --collection m500; date

date; mgeneratejs mgenerate500.json -n 1 | mongoimport --host atlas-rr7e4q-shard-0/boldotcom-sizing-shard-00-00.4fyyo.gcp.mongodb.net:27017,boldotcom-sizing-shard-00-01.4fyyo.gcp.mongodb.net:27017,boldotcom-sizing-shard-00-02.4fyyo.gcp.mongodb.net:27017 --ssl --username admin --password nimda --authenticationDatabase admin --db boldotcom --collection promises; date


## Preparation of environment

mongo “mongodb+srv://admin@cluster-dev-4fyyo.mongodb.net/test?retryWrites=true"

Generate load to cluster-dev

    java -jar ~/Local/IdeaProjects/poc-driver/POCDriver/bin/POCDriver.jar -k 20 -i 10 -u 10 -b 20 -n 'datasets.pocdriver' -c "mongodb+srv://admin:nimda@cluster-dev-4fyyo.mongodb.net/test?retryWrites=true"

Generate slow query

    {'job' : {'$eq' : 'Architectural technologist'}, 'email' : {'$regex' : '.*@taylor.biz'}}

## Zero RPO

Create cluster with continuous backups enabled.

Import the default dataset to populate it.

Load the good documents

    date; mgeneratejs mgenerateBefore.json -n 1000 | mongoimport --host uniserv-rpo-demo-shard-0/uniserv-rpo-demo-shard-00-00-4fyyo.gcp.mongodb.net:27017,uniserv-rpo-demo-shard-00-01-4fyyo.gcp.mongodb.net:27017,uniserv-rpo-demo-shard-00-02-4fyyo.gcp.mongodb.net:27017 --ssl --username admin --password nimda --authenticationDatabase admin --db test --collection RPO; date

Load the bad documents

    date; mgeneratejs mgenerateAfter.json -n 100 | mongoimport --host uniserv-rpo-demo-shard-0/uniserv-rpo-demo-shard-00-00-4fyyo.gcp.mongodb.net:27017,uniserv-rpo-demo-shard-00-01-4fyyo.gcp.mongodb.net:27017,uniserv-rpo-demo-shard-00-02-4fyyo.gcp.mongodb.net:27017 --ssl --username admin --password nimda --authenticationDatabase admin --db test --collection RPO; date

Open Atlas Filter: {docType : "AFTER_CORRUPTION"}

Selecting the “Backup" button on the left hand pane

Selecting the green “RESTORE OR DOWNLOAD” button next to the database used for the demo

In the grey pane on the right hand side of the screen, select “POINT IN TIME” under the heading “Select Restore Point"

Enter today's date. (If you click in the date box a calendar widget will appear)

Enter the time from step #2. You may need to convert to the correct time zone.

Press Next and then press the “CHOOSE CLUSTER TO RESTORE TO” button and select the current cluster.

Measurement

Once the restore is complete, validate the results by using Compass (or the Atlas Collection Browser) to show:

    The total number of documents in the collection is 1000
    All the documents in the collection have {docType: “BEFORE_CORRUPTION”}
    None of the documents in the collection have {docType: “AFTER_CORRUPTION”}
