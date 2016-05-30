package com.opencartis.georfid;

import java.util.ArrayList;

public class Field {
    private int id;
    private int elementTypeId;
    private String name;
    private int fieldType;
    private int valuesListId;
    private ArrayList<String> values= new ArrayList<String>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getElementTypeId() {
        return elementTypeId;
    }

    public void setElementTypeId(int elementTypeId) {
        this.elementTypeId = elementTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public int getValuesListId() {
        return valuesListId;
    }

    public void setValuesListId(int valuesListId) {
        this.valuesListId = valuesListId;
    }
}
