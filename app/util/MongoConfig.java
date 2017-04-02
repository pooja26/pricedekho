package util;

import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import play.Play;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pooja_mahapatra on 24/03/17.
 */
public class MongoConfig {
    private static final Morphia morphia=new Morphia();
    private static final MongoClient mongoClient=new MongoClient(Play.application().configuration().getString("mongodb.host"),Play.application().configuration().getInt("mongodb.port"));
    private static Datastore datastore=morphia.createDatastore(mongoClient,Play.application().configuration().getString("mongodb.database"));

    static {
        ensureIndexes();
    }

    private static void ensureIndexes(){
        morphia.mapPackage("models");
        datastore.ensureIndexes();

    }

    public static Datastore getDB(){
        return datastore;
    }

    public static List<ObjectId> getObjectIds(List<String> ids){
        List<ObjectId> objectIds=new ArrayList<>();
        for (int i=0;i<objectIds.size();i++){
            objectIds.add(new ObjectId(ids.get(i)));
        }
        return objectIds;
    }
}
