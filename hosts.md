# Hosts

Facilitating direct access to individual, cluster and shard nodes.

## mongos

    mongo --host "192.168.103.100:26000" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"

### config server replica set

    mongo --host "m103-csrs/192.168.103.100:26001" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:26001" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:26002" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:26003" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"

## m103-repl cluster

    mongo --host "m103-repl/192.168.103.100:27001" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:27001" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:27002" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:27003" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"

## m103-repl-2 cluster

    mongo --host "m103-repl-2/192.168.103.100:27004" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:27004" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:27005" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
    mongo --host "192.168.103.100:27006" -u "m103-admin" -p "m103-pass" --authenticationDatabase "admin"
