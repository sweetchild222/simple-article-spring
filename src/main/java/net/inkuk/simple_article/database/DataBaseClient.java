package net.inkuk.simple_article.database;

import net.inkuk.simple_article.util.Log;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
//import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.*;
import java.util.Map;

@Component
public class DataBaseClient {

    private Connection connection = null;

    public Connection connect(){

        try {

            final String driver = "org.mariadb.jdbc.Driver";
            Class.forName(driver);

            final String ip = "13.124.193.201";
            final String port = "4335";
            final String name = "simple_article";

            final String url = "jdbc:mariadb://" + ip + ":" + port + "/" + name;
            final String user = "simple_article_api";
            final String password = "apiSweetchild@0617";

            return DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException | SQLException e) {

            Log.error(e.toString());
            return null;
        }
    }


    public void close() {

        if(this.connection != null) {

            try {
                this.connection.close();
                this.connection = null;
            }
            catch(SQLException e){

                Log.error(e.toString());
            }
        }
    }


    public Map<String, Object> getRow(String sql) {

        Log.info(sql);

        try {

            return executeSelect(sql);

        }
        catch (SQLException e) {

            if(e instanceof SQLNonTransientConnectionException)
                close();

            Log.error(e.toString());
            return null;
        }
    }



    public long postRow(String sql) {

        Log.info(sql);

        try {

            return executeInsert(sql);
        }
        catch (SQLException e) {

            if(e instanceof SQLNonTransientConnectionException)
                close();

            Log.error(e.toString());
            return -1;
        }
    }


    public int updateRow(String sql) {

        Log.info(sql);

        try {

            return executeUpdate(sql);
        }
        catch (SQLException e) {

            if(e instanceof SQLNonTransientConnectionException)
                close();

            Log.error(e.toString());
            return -1;
        }
    }


    private @Nullable Map<String, Object> executeSelect(String sql) throws SQLException {

        if(this.connection != null)
            if(this.connection.isClosed())
                this.connection = null;

        if(this.connection == null)
            this.connection = this.connect();

        if(this.connection == null)
            return null;

        Statement statement = this.connection.createStatement();

        if(statement == null)
            return null;

        statement.closeOnCompletion();

        ResultSet resultSet =  statement.executeQuery(sql);

        if (resultSet == null)
            return null;

        Map<String, Object> map = convertMap(resultSet);

        resultSet.close();

        return map;
    }


    private long executeInsert(String sql) throws SQLException {

        if(this.connection != null)
            if(this.connection.isClosed())
                this.connection = null;

        if(this.connection == null)
            this.connection = this.connect();

        if(this.connection == null)
            return -1;

        Statement statement = this.connection.createStatement();

        if(statement == null)
            return -1;

        statement.closeOnCompletion();

        int effectCount = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);

        if(effectCount == 0)
            return 0;
        else if(effectCount == 1) {

            ResultSet resultSet = statement.getGeneratedKeys();

            if (resultSet == null)
                return -1;

            return resultSet.first() ? resultSet.getLong(1) : -1;
        }
        else
            return -1;
    }


    private int executeUpdate(String sql) throws SQLException {

        if(this.connection != null)
            if(this.connection.isClosed())
                this.connection = null;

        if(this.connection == null)
            this.connection = this.connect();

        if(this.connection == null)
            return -1;

        Statement statement = this.connection.createStatement();

        if(statement == null)
            return -1;

        statement.closeOnCompletion();

        return statement.executeUpdate(sql);
    }


    private @Nullable Map<String, Object> convertMap(ResultSet resultSet) throws SQLException {

        if(!resultSet.first())
            return new java.util.HashMap<>(Map.of());

        final ResultSetMetaData metaData = resultSet.getMetaData();

        Map<String, Object> map = new java.util.HashMap<>(Map.of());

        for(int i = 1; i <= metaData.getColumnCount(); i++) {

            final int columnType = metaData.getColumnType(i);
            final String columnName = metaData.getColumnName(i);

            switch (columnType) {
                case Types.LONGNVARCHAR:
                case Types.CHAR:
                case Types.VARCHAR:
                    map.put(columnName, resultSet.getString(i));
                    break;

                case Types.TIMESTAMP:
                    map.put(columnName, resultSet.getTimestamp(i).getTime());
                    break;

                case Types.BIGINT:
                case Types.INTEGER:
                    map.put(columnName, resultSet.getLong(i));
                    break;

                case Types.TINYINT:
                    map.put(columnName, resultSet.getInt(i));
                    break;

                default:
                    Log.error(String.valueOf(columnType) + ": Unsupported type " + columnName);
                    return null;
            }
        }

        return map;
    }
}