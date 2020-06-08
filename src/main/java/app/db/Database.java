package app.db;

import java.sql.*;

public class Database{

    Connection c = null;
    Statement stat = null;



    public Database() {
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:logs.db");
                stat = c.createStatement();
            } catch ( Exception e ) {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
            System.out.println("Opened database successfully");

            createTable();

    }
    public void close() throws SQLException {
        stat.close();
        c.close();
    }

    public void createTable() {
        try {

            String sql = "CREATE TABLE IF NOT EXISTS LOGS " +
                    "( NAME           TEXT PRIMARY KEY   NOT NULL, " +
                    " NUMBER            INTEGER     NOT NULL) ";
            stat.executeUpdate(sql);

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }

    }
    public void insertOrReplace(String name, int number) throws SQLException {

        PreparedStatement preparedStatement = c.prepareStatement("INSERT INTO logs (name, number) " +
                "VALUES(?, ?) ON CONFLICT(name) DO UPDATE SET number = number+1");
        preparedStatement.setString(1, name.toLowerCase());
        preparedStatement.setInt(2, number);
        preparedStatement.execute();
    }
    public int findNumberByName(String name) throws SQLException {
        PreparedStatement preparedStatement = c.prepareStatement("SELECT number FROM logs WHERE name = ?");
        preparedStatement.setString(1, name.toLowerCase());
        ResultSet rs = preparedStatement.executeQuery();
        int number = 0;
        while ( rs.next() ) {
            number = rs.getInt("number");
        }
        return number;

    }
    public void select() throws SQLException {
        PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM logs");

        ResultSet rs = preparedStatement.executeQuery();
        int number = 0;
        while ( rs.next() ) {
            String name = rs.getString("name");
            number = rs.getInt("number");
            System.out.println(name+" "+ number );
        }
    }

    public static void main(String[] args) throws SQLException {
        Database database = new Database();
       database.select();

    }
}
