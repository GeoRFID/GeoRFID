package com.opencartis.georfid;

import com.opencartis.georfid.DataBase.DatabaseObject;
import com.opencartis.georfid.DataBase.Property;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ElementType extends DatabaseObject {
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

    public ArrayList<ElementType> GetTypes() {
        ArrayList<ElementType> types = new ArrayList<ElementType>();

        try {
            System.err.println("PASO 1");
            OpenConnection();

            statement = connection.createStatement();
            String sql = "SELECT \"id\", \"nombre\" FROM \"GeoRFID\".\"TipoElemento\"";

            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                ElementType type = new ElementType();
                type.setId(result.getInt("id"));
                type.setName(result.getString("nombre"));

                types.add(type);
            }

            System.err.println("PASO 2");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        return types;
    }

    public ArrayList<Field> getFields() {
        ArrayList<Field> fields = new ArrayList<Field>();

        try {
            OpenConnection();

            statement = connection.createStatement();
            String sql = "SELECT \"id\", \"nombre\", \"tipo\", \"listaValoresId\" FROM \"GeoRFID\".\"CampoTipoElemento\" WHERE \"tipoElementoId\"=" + this.getId() + " ORDER BY \"id\" ASC";

            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                Field field = new Field();
                field.setId(result.getInt("id"));
                field.setName(result.getString("nombre"));
                field.setFieldType(result.getInt("tipo"));
                field.setValuesListId(result.getInt("listaValoresId"));

                fields.add(field);
            }

            statement.close();

            for (int i = 0; i < fields.size(); i++) {
                statement = connection.createStatement();
                sql = "SELECT \"id\", \"valor\" FROM \"GeoRFID\".\"CampoListaValores\" WHERE \"id\"=" + fields.get(i).getValuesListId();

                result = statement.executeQuery(sql);
                while (result.next()) {
                    if (fields.get(i).getFieldType() == 2) {
                        //Values lists
                        String value = result.getString("valor");
                        fields.get(i).getValues().add(value);
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

        return fields;
    }

    public ArrayList<Property> getProperties(int elementId, int elementTypeId) {
        ArrayList<Property> properties = new ArrayList<Property>();

        try {
            OpenConnection();

            statement = connection.createStatement();

            String sql= "SELECT \"id\", c.\"tipoElementoId\", c.\"nombre\", c.\"tipo\",c.\"listaValoresId\", v.\"valor\" FROM \"GeoRFID\".\"CampoTipoElemento\" AS c LEFT OUTER JOIN \"GeoRFID\".\"ValorCampo\" AS v ON c.\"id\"=v.\"campoTipoElementoId\" AND v.\"elementoId\"=" + elementId + " WHERE c.\"tipoElementoId\"= '" + elementTypeId + "' ORDER BY \"id\"";

            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                Property property = new Property();
                property.setId(result.getInt("id"));
                property.setName(result.getString("nombre"));
                property.setFieldType(result.getInt("tipo"));
                property.setValuesListId(result.getInt("listaValoresId"));
                property.setValue(result.getString("valor"));

                properties.add(property);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.err.println("Error: Cant connect!");
        } finally {
            CloseConnection();
        }

        return properties;
    }
}