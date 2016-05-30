package com.opencartis.georfid.DataBase;

import com.opencartis.georfid.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseObject {

    protected Connection connection = null;
    protected Statement statement = null;

    private String ConnectionString() {
        String dbserver = Settings.getDbServer();
        String dbUser = Settings.getDbUser();
        String dbPass = Settings.getDbPass();
        String cadenaConexion = "jdbc:postgresql://" + dbserver + "?" + "user=" + dbUser + "&password=" + dbPass;

        return cadenaConexion;
    }

    protected boolean OpenConnection() {
        try {
            Class.forName("org.postgresql.Driver");

            if (connection != null) {
                CloseConnection();
            }

            connection = DriverManager.getConnection(ConnectionString());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            return false;
        }
    }

    protected boolean CloseConnection() {
        CloseStatement();

        if (connection != null) {
            try {
                connection.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }

        connection = null;
        return false;
    }

    private void CloseStatement() {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
        }
    }
}
