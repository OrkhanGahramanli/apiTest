package apiSteps;

import java.sql.*;

public class DBConnection {

    static final String DB_URL = "jdbc:sqlserver://10.10.10.144;databaseName=KontaktHome;encrypt=true;trustServerCertificate=true";
    static final String USER = "orkhan_gahramanli";
    static final String PASS = "!O356254gg";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
