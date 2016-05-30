package com.opencartis.georfid;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.opencartis.georfid.DataBase.Property;

import java.util.ArrayList;
import java.util.List;

public class NewElementData extends AppCompatActivity {

    Element element;
    ArrayList<Field> fields = new ArrayList<Field>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_element_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setEnabled(false);
                Toast.makeText(getBaseContext(), R.string.savingelement, Toast.LENGTH_LONG).show();

                LinearLayout linear = (LinearLayout) findViewById(R.id.dynamicContentLayout);

                for (int i = 0; i < fields.size(); i++) {

                    View childView = linear.getChildAt(i + 1);
                    LinearLayout rowLayout = (LinearLayout) childView;
                    View valueView = rowLayout.getChildAt(1);

                    Field field = fields.get(i);
                    String value = "";

                    switch (field.getFieldType()) {
                        case 2: {
                            // Lista de valores
                            Spinner spinnerValue = (Spinner) valueView;
                            value = spinnerValue.getSelectedItem().toString();
                            break;
                        }
                        case 3: {
                            // Checkbox
                            CheckBox checkBox = (CheckBox) valueView;
                            if (checkBox.isChecked()) {
                                value = "1";
                            } else {
                                value = "0";
                            }
                            break;
                        }
                        default: {
                            //Texto o numérico
                            EditText textValue = (EditText) valueView;
                            value = textValue.getText().toString();
                            break;
                        }
                    }

                    Property property = Property.fromField(field);
                    property.setValue(value);

                    element.getProperties().add(property);
                }

                CreateElementTask createElement = new CreateElementTask();
                createElement.execute(element);
            }
        });


        try {
            element = (Element) getIntent().getExtras().get("element");
            context = this;

            GetElementTypeFields getElementTypeFields = new GetElementTypeFields();
            getElementTypeFields.execute(element, null, null);
        }
        catch (Exception e)
        {
        }
    }

    private class GetElementTypeFields extends AsyncTask<Element, Void, Void> {
        @Override
        public Void doInBackground(com.opencartis.georfid.Element... elements) {

            ElementType elementType = new ElementType();
            elementType.setId(element.getElementType());
            fields = elementType.getFields();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            View editView;

            for (int i = 0; i < fields.size(); i++) {

                Field field = fields.get(i);

                LinearLayout horizontalLayout = new LinearLayout(context);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView = new TextView(NewElementData.this);
                textView.setWidth(150);
                textView.setText(field.getName());

                horizontalLayout.addView(textView);

                switch (field.getFieldType()) {
                    case 2: {
                        // Lista de valores
                        Spinner spinnerValues = new Spinner(NewElementData.this);
                        spinnerValues.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));

                        String[] arraySpinner = new String[field.getValues().size()];

                        for (int j = 0; j < field.getValues().size(); j++) {
                            arraySpinner[j] = field.getValues().get(j);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewElementData.this,
                                android.R.layout.simple_spinner_item, arraySpinner);
                        spinnerValues.setAdapter(adapter);

                        editView = spinnerValues;
                        break;
                    }
                    case 3: {
                        // Checkbox
                        CheckBox checkBox = new CheckBox(NewElementData.this);

                        editView = checkBox;
                        break;
                    }
                    default: {
                        // Texto
                        EditText editText = new EditText(NewElementData.this);
                        editText.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));

                        switch (field.getFieldType()) {
                            case 4: {
                                // Numérico entero
                                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            }
                            case 5: {
                                // Numérico decimal
                                editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                break;
                            }
                        }
                        editView = editText;
                        break;
                    }
                }

                horizontalLayout.addView(editView);

                LinearLayout linear = (LinearLayout) findViewById(R.id.dynamicContentLayout);
                linear.addView(horizontalLayout);
            }

        }
    }

    private class CreateElementTask extends AsyncTask<Element, Void, Void> {
        @Override
        public Void doInBackground(com.opencartis.georfid.Element... elements) {
            elements[0].Create();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);

            String message;
            if (element.getId() > 0) {
                message = getString(R.string.element_saved_with_id) + element.getId();
            } else {
                message = getString(R.string.error_saving_element);
            }

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(NewElementData.this, SelectOption.class);
            startActivity(intent);
        }
    }
}
