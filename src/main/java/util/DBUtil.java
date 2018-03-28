package util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.*;

public class DBUtil {
        private static Connection conn;
    private static ComboPooledDataSource ds = new ComboPooledDataSource();

    static {
        try {
            ds.setDriverClass("com.mysql.jdbc.Driver");
            ds.setJdbcUrl("jdbc:mysql://localhost:3306/csdn?rewriteBatchedStatements=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull");
            ds.setUser("root");
            ds.setPassword("");
            ds.setMinPoolSize(5);
            ds.setMaxPoolSize(30);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


}
