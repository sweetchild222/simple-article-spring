package net.inkuk.simple_article.database;

public class DataBaseClientPool {

    private final int maxConnection = 10;

    private int index = 0;

    private final DataBaseClient [] clientArray = new DataBaseClient[maxConnection];

    private static DataBaseClientPool instance = null;


    private DataBaseClientPool(){

    }

    public static DataBaseClient getClient(){

        if(instance == null) {

            instance = new DataBaseClientPool();

            for(int i = 0; i < instance.clientArray.length; ++i)
                instance.clientArray[i]= new DataBaseClient();
        }

        return instance.clientArray[(instance.index++) % instance.maxConnection];
    }


    public static void closeAll(){

        for(DataBaseClient client : instance.clientArray)
            client.close();
    }
}
