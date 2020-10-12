package be.sanderdevos.mongo.lab;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

        public static void main(String[] args) {
            logger.info("Booting application.");
            ApplicationContext ctx = SpringApplication.run(Application.class, args);
            logger.info("Exiting application.");
            SpringApplication.exit(ctx);
        }

    @Override
    public void run(ApplicationArguments  args) throws Exception {
        logger.info("Running main command line.");
        logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
        logger.info("NonOptionArgs: {}", args.getNonOptionArgs());
        logger.info("OptionNames: {}", args.getOptionNames());

        Params params = Params.from(args);
        logger.info("Params: {}", params);

        MongoClient mongoClient = getMongoClient(params.getUri());
        MongoCollection collection = mongoClient.getDatabase(params.getDb()).getCollection(params.getCollection());

        if (params.getMode() == )

        //example1Find(customerCollection);


        logger.info("Running main command line finished.");

    }

    private MongoClient getMongoClient(String uri) {
        ConnectionString connString = new ConnectionString(uri);

        final MongoClientSettings settings = MongoClientSettings
                .builder()
                .applicationName("mongoService")
                .applyConnectionString(connString)
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                //.writeConcern(new WriteConcern("majority").withWTimeout(2500, TimeUnit.MILLISECONDS))
                //.readConcern(new ReadConcern(ReadConcernLevel.MAJORITY))
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

        return mongoClient;
    }
}
