package com.opencartis.georfid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.nio.channels.Selector;

public class SelectOption extends AppCompatActivity {

    Button btnReadElement;
    Button btnNewElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        btnReadElement = (Button) findViewById(R.id.btnReadElement);

        btnReadElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectOption.this, ReadElement.class);
                startActivity(intent);
            }

        });

        btnNewElement = (Button) findViewById(R.id.btnNewElement);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
        boolean isAdmin=pref.getBoolean("admin",false);

        if(isAdmin) {
            btnNewElement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectOption.this, NewElement.class);
                    startActivity(intent);
                }
            });
        }
        else
        {
            btnNewElement.setEnabled(false);
        }
    }

}
