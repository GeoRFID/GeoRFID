package com.opencartis.georfid;

import com.opencartis.georfid.DataBase.DatabaseObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;

public class User extends DatabaseObject implements Serializable {
    private String name;
    private String login;
    private String password;
    private boolean admin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Boolean IsValid() {
        try {
            OpenConnection();

            statement = connection.createStatement();
            String md5= Crypt.md5(this.getPassword());

            String sql = "SELECT \"nombre\",\"tipoUsuario\" FROM \"GeoRFID\".\"Usuario\" WHERE \"usuario\"='" + this.getLogin() + "' AND \"contrasena\"='" + md5 + "'";
            ResultSet result = statement.executeQuery(sql);

            this.name = "";
            if (result.next()) {
                this.name = result.getString("nombre");
                this.admin = result.getInt("tipoUsuario")==1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        return this.name != "";
    }
}
