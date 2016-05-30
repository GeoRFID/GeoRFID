package com.opencartis.georfid;

import android.graphics.Bitmap;

import com.opencartis.georfid.DataBase.DatabaseObject;
import com.opencartis.georfid.DataBase.Property;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Element extends DatabaseObject implements Serializable {
    private int id;
    private double latitude;
    private double longitude;
    private String tag;
    private String base64Image;
    private int elementType;
    private ArrayList<Integer> taskIds= new ArrayList<Integer>();
    private ArrayList<Property> properties = new ArrayList<Property>();

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<Property> properties) {
        this.properties = properties;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getImage() {
        if (this.base64Image != null && this.base64Image != "") {
            return Utils.decodeBase64(this.base64Image);
        } else {
            return null;
        }
    }

    public void setImage(Bitmap image) {

        this.base64Image = Utils.encodeToBase64(image, Bitmap.CompressFormat.JPEG, 40);
    }

    public ArrayList<Integer> getTaskIds() {
        return taskIds;
    }

    public Element() {

    }

    private int GetNextId() {
        int nextId = 0;

        try {
            statement = connection.createStatement();

            String consultaSQL = "SELECT MAX(\"id\") FROM \"GeoRFID\".\"Elemento\"";

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

    public boolean Read() {
        try {
            OpenConnection();

            statement = connection.createStatement();
            String sql = "SELECT \"id\",\"latitud\",\"longitud\",\"imagen\",\"tipo\" FROM \"GeoRFID\".\"Elemento\" WHERE \"etiqueta\"='" + this.tag + "'";
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) {
                this.id = result.getInt("id");
                this.latitude = result.getDouble("latitud");
                this.longitude = result.getDouble("longitud");
                this.elementType = result.getInt("tipo");
                this.base64Image = result.getString("imagen");
            }

            ElementType elementType = new ElementType();
            elementType.setId(this.elementType);

            CloseConnection();

            this.properties = elementType.getProperties(this.getId(),this.getElementType());

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

        if(this.id>0)
        {
            Task task= new Task();
            this.taskIds = task.GetElementTask(this.id);
        }

        return this.id > 0;
    }

    public boolean Create() {
        int result = 0;

        try {
            OpenConnection();
            int nextId = GetNextId();

            if (nextId > 0) {
                statement = connection.createStatement();

                this.setId(0);

                String sql = "INSERT INTO \"GeoRFID\".\"Elemento\"(\"id\",\"etiqueta\",\"latitud\",\"longitud\",\"imagen\",\"tipo\") VALUES (" + nextId + ",'" + this.tag + "'," + this.latitude + "," + this.longitude + ",'" + this.base64Image + "'," + this.elementType + ")";
                result = statement.executeUpdate(sql);

                if (result > 0) {
                    this.setId(nextId);

                    for (int i = 0; i < this.getProperties().size(); i++) {
                        Property property = this.getProperties().get(i);

                        sql = "INSERT INTO \"GeoRFID\".\"ValorCampo\"(\"elementoId\",\"campoTipoElementoId\",\"valor\") VALUES (" + nextId + "," + property.getId() + ",'" + property.getValue() + "')";

                        result = statement.executeUpdate(sql);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        System.err.println("----- PostgreSQL query ends correctly!-----");

        return result > 0;
    }

    public boolean Delete() {
        int result = 0;

        try {
            OpenConnection();

            if (id > 0) {
                statement = connection.createStatement();

                String sql = "DELETE FROM \"GeoRFID\".\"Elemento\" WHERE \"id\"=" + id + ";";
                sql += "DELETE FROM \"GeoRFID\".\"ValorCampo\" WHERE \"elementoId\"=" + id + ";";
                result = statement.executeUpdate(sql);

                if (result > 0) {
                    this.setId(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        System.err.println("----- PostgreSQL query ends correctly!-----");

        return result > 0;
    }
}