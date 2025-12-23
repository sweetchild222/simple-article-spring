package net.inkuk.simple_article.database;

import net.inkuk.simple_article.util.UserContext;
import org.jetbrains.annotations.NotNull;

public class DataBaseClientPool {

    private final int clientCount = 10;

    private final DataBaseClient [] clientArray = new DataBaseClient[clientCount];

    private static DataBaseClientPool instance = null;

    private DataBaseClientPool(){

    }

    public static DataBaseClient getClient(final long userId){

        if(instance == null)
            instance = createInstance();

        return instance.clientArray[(int)(userId % (instance.clientCount - 1))];
    }


    private static @NotNull DataBaseClientPool createInstance(){

        DataBaseClientPool clientPool = new DataBaseClientPool();
        for(int i = 0; i < clientPool.clientArray.length; ++i)
            clientPool.clientArray[i]= new DataBaseClient();

        return clientPool;
    }


    public static DataBaseClient getClient(){

        if(instance == null)
            instance = createInstance();

        return instance.clientArray[instance.clientCount - 1];
    }


    public static void closeAll(){

        if(instance != null)
            for(DataBaseClient client : instance.clientArray)
                client.close();
    }
}
