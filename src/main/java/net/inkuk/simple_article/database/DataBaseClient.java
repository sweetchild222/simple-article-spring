package net.inkuk.simple_article.database;

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

            System.out.println(e.toString());
        }

        return null;
    }


    public void close() {

        if(this.connection != null) {

            try {
                if (!this.connection.isClosed()) {
                    this.connection.close();
                    this.connection = null;
                }
                else
                    System.out.println("non closed");
            }
            catch(SQLException e){
                System.out.println(e.toString());
            }
        }
    }


    public Map<String, Object> selectRow(String sql) {

        try {

            ResultSet resultSet = execute(sql);

            if (resultSet == null)
                return null;

            Map<String, Object> map = convertMap(resultSet);

            resultSet.close();

            return map;
        }
        catch (SQLException e) {

            System.out.println(e.toString());
        }

        return null;

    }


    private ResultSet execute(String sql) throws SQLException {

        if(this.connection != null) {

            if(this.connection.isClosed()) {
                this.connection = null;
            }
        }

        if(this.connection == null) {
            this.connection = this.connect();
        }

        if(this.connection == null)
            return null;

        Statement statement = this.connection.createStatement();

        if(statement == null)
            return null;

        statement.closeOnCompletion();

        return statement.executeQuery(sql);
    }


    private Map<String, Object> convertMap(ResultSet resultSet) throws SQLException {

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

                case Types.INTEGER:
                    map.put(columnName, resultSet.getLong(i));
                    break;

                default:
                    System.out.println(columnName + ": abnormal type " + String.valueOf(columnType));
                    return null;
            }
        }

        return map;
    }
}