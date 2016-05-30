package com.opencartis.georfid;

import com.opencartis.georfid.DataBase.DatabaseObject;

import java.sql.ResultSet;
import java.util.ArrayList;

public class TaskType extends DatabaseObject {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TaskState> GetStates() {
        ArrayList<TaskState> states = new ArrayList<TaskState>();

        try {
            OpenConnection();

            statement = connection.createStatement();
            String sql = "SELECT \"id\", \"nombre\" FROM \"GeoRFID\".\"EstadoTarea\"";

            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                TaskState state = new TaskState();
                state.setId(result.getInt("id"));
                state.setName(result.getString("nombre"));

                states.add(state);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        return states;
    }

    public ArrayList<TaskType> GetTypes() {
        ArrayList<TaskType> types = new ArrayList<TaskType>();

        try {
            OpenConnection();

            statement = connection.createStatement();
            String sql = "SELECT \"id\", \"nombre\" FROM \"GeoRFID\".\"TipoTarea\"";

            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                TaskType type = new TaskType();
                type.setId(result.getInt("id"));
                type.setName(result.getString("nombre"));

                types.add(type);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        return types;
    }
}