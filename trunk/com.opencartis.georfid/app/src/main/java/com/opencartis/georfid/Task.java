package com.opencartis.georfid;

import android.graphics.Bitmap;

import com.opencartis.georfid.DataBase.DatabaseObject;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Task extends DatabaseObject implements Serializable {

    private int id;
    private int elementoId;
    private int taskType;
    private String base64Image;
    private String description;
    private int stateId;

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public Bitmap getImage() {
        if (this.base64Image != null && this.base64Image != "") {
            return Utils.decodeBase64(this.base64Image);
        } else {
            return null;
        }
    }

    public void setImage(Bitmap image) {

        this.base64Image = Utils.encodeToBase64(image, Bitmap.CompressFormat.JPEG, 100);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int GetNextId() {
        int nextId = 0;

        try {
            statement = connection.createStatement();

            String consultaSQL = "SELECT MAX(\"id\") FROM \"GeoRFID\".\"ElementoTarea\"";

            ResultSet resultado = statement.executeQuery(consultaSQL);

            if (resultado.next()) {
                nextId = resultado.getInt(1);
                nextId++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
            }
        }

        return nextId;
    }

    public boolean Create() {
        int result = 0;

        try {
            OpenConnection();
            int nextId = GetNextId();

            if (nextId > 0) {
                statement = connection.createStatement();

                this.setId(0);

                String sql = "INSERT INTO \"GeoRFID\".\"ElementoTarea\"(\"id\",\"elementoId\",\"tipoTareaId\",\"descripcion\",\"imagen\",\"estadoId\") VALUES (" + nextId + "," + this.elementoId + "," + this.getTaskType() + ",'" + this.description + "','" + this.base64Image + "',1)";
                result = statement.executeUpdate(sql);

                if (result > 0) {
                    this.setId(nextId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        return result > 0;
    }

    public boolean Read() {
        try {
            OpenConnection();

            statement = connection.createStatement();
            String sql = "SELECT \"id\",\"tipoTareaId\",\"descripcion\",\"imagen\",\"estadoId\" FROM \"GeoRFID\".\"ElementoTarea\" WHERE \"id\"='" + this.id + "'";
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) {
                this.id = result.getInt("id");
                this.taskType = result.getInt("tipoTareaId");
                this.description = result.getString("descripcion");
                this.base64Image = result.getString("imagen");
                this.setStateId(result.getInt("estadoId"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
            }

            CloseConnection();
        }

        return this.id > 0;
    }

    public boolean UpdateState()
    {
        try {
            OpenConnection();

            statement = connection.createStatement();
            String sql = "UPDATE \"GeoRFID\".\"ElementoTarea\" SET \"estadoId\"=" + this.getStateId() + " WHERE \"id\"='" + this.id + "'";
            ResultSet result = statement.executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
            }

            CloseConnection();
        }

        return true;
    }

    public ArrayList<Integer> GetElementTask(int elementId)
    {
        ArrayList<Integer> taskIds= new ArrayList<Integer>();

        try {
            OpenConnection();

            statement = connection.createStatement();
            String sql = "SELECT \"id\" FROM \"GeoRFID\".\"ElementoTarea\" WHERE \"elementoId\"='" + elementId + "' AND \"estadoId\"=1";
            ResultSet result = statement.executeQuery(sql);

            while (result.next()) {
                int taskId = result.getInt("id");
                taskIds.add(taskId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(e.getMessage());
                }
            }

            CloseConnection();
        }

        return taskIds;
    }


    public int getElementoId() {
        return elementoId;
    }

    public void setElementoId(int elementoId) {
        this.elementoId = elementoId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
    }
}
