package net.inkuk.simple_article.database;

import net.inkuk.simple_article.util.UserContext;

public class DataBaseClientPool {

    private final int clientCount = 10;

    private final DataBaseClient [] clientArray = new DataBaseClient[clientCount];

    private static DataBaseClientPool instance = null;


    private DataBaseClientPool(){

    }

    public static DataBaseClient getClient(final long userId){

        if(instance == null) {

            instance = new DataBaseClientPool();
            for(int i = 0; i < instance.clientArray.length; ++i)
                instance.clientArray[i]= new DataBaseClient();
        }

        return instance.clientArray[(int)(userId % (instance.clientCount - 1))];
    }


    public static DataBaseClient getClient(){

        if(instance == null) {

            instance = new DataBaseClientPool();
            for(int i = 0; i < instance.clientArray.length; ++i)
                instance.clientArray[i]= new DataBaseClient();
        }

        return instance.clientArray[instance.clientCount - 1];
    }


    public static void closeAll(){

        for(DataBaseClient client : instance.clientArray)
            client.close();
    }
}
