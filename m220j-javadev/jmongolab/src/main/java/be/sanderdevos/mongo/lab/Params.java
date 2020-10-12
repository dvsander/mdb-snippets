package be.sanderdevos.mongo.lab;

import org.springframework.boot.ApplicationArguments;

import javax.validation.constraints.NotNull;
import java.util.EnumSet;

public class Params {

    private String uri;
    private String db;
    private String collection;
    private Mode mode;

    enum Mode {
        BULK,
        STREAM
    }

    private Params(String uri, String db, String collection, Mode mode){
        this.uri = uri;
        this.db = db;
        this.collection = collection;
        this.mode = mode;
    }

    public static Params from(@NotNull ApplicationArguments args){
        String uri = args.getOptionValues("uri").stream().findFirst().orElse("mongodb://localhost/test");
        String db = args.getOptionValues("db").stream().findFirst().orElse("test");
        String coll = args.getOptionValues("c").stream().findFirst().orElse("data");
        String modeParam = args.getOptionValues("mode").stream().findFirst().orElse(Mode.STREAM.name());

        Mode mode;
        try {
            mode = Mode.valueOf(modeParam);
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid value for option 'mode', value '" + modeParam + "'.");
        }

        return new Params(uri, db, coll, mode);
    }

    @Override
    public String toString() {
        return "Params{" +
                "uri='" + uri + '\'' +
                ", db='" + db + '\'' +
                ", collection='" + collection + '\'' +
                ", mode=" + mode +
                '}';
    }

    public String getCollection() {
        return collection;
    }

    public String getUri() {
        return uri;
    }

    public String getDb() {
        return db;
    }

    public Mode getMode() {
        return mode;
    }
}
