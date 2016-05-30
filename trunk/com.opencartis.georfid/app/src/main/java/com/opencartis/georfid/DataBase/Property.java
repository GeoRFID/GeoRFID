package com.opencartis.georfid.DataBase;

import com.opencartis.georfid.Field;

public class Property extends Field {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Property fromField(Field field)
    {
        Property property= new Property();

        property.setId(field.getId());
        property.setName(field.getName());
        property.setFieldType(field.getFieldType());
        property.setValuesListId(field.getValuesListId());
        property.setElementTypeId(field.getElementTypeId());

        return property;
    }
}
