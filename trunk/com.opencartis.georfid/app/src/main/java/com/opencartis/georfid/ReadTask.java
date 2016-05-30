package com.opencartis.georfid;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.opencartis.georfid.DataBase.Property;

import java.util.ArrayList;

public class ReadTask extends AppCompatActivity {

    private Task task= new Task();
    private TextView txtReadTaskType;
    private TextView txtReadTaskDescription;
    private ImageView imageViewReadTaskPhoto;
    private Spinner spinnerReadTaskState;
    private ArrayList<TaskType> types;
    private ArrayList<TaskState> states;
    private boolean isAdmin;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        this.context=this;

        txtReadTaskType = (TextView)findViewById(R.id.txtReadTaskType);
        txtReadTaskDescription = (TextView)findViewById(R.id.txtReadTaskDescription);
        imageViewReadTaskPhoto = (ImageView)findViewById(R.id.imageViewReadTaskPhoto);
        spinnerReadTaskState = (Spinner)findViewById(R.id.spinnerReadTaskState);

        spinnerReadTaskState .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String spinnerValue = spinnerReadTaskState.getSelectedItem().toString();

                for (int i = 0; i < states.size(); i++) {
                    if (states.get(i).getName() == spinnerValue) {
                        task.setStateId(states.get(i).getId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        task.setId((int) getIntent().getExtras().get("taskId"));

        ReadTaskTask readElementTask = new ReadTaskTask();
        readElementTask.execute(task);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateTaskStateTask updateTaskStateTask = new UpdateTaskStateTask();
                updateTaskStateTask.execute(task);
            }
        });

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
        isAdmin=pref.getBoolean("admin",false);

        if(!isAdmin) {
            fab.setVisibility(View.INVISIBLE);
            spinnerReadTaskState.setEnabled(false);
        }
    }

    private class ReadTaskTask extends AsyncTask<Task, Void, Void> {

        @Override
        public Void doInBackground(com.opencartis.georfid.Task... tasks) {

            TaskType taskType= new TaskType();
            types = taskType.GetTypes();

            states = taskType.GetStates();

            tasks[0].Read();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            int id = task.getId();

            String taskTypeName= types.get(0).getName();

            for(int i=0;i<types.size();i++)
            {
                if(types.get(i).getId()== task.getTaskType())
                {
                    taskTypeName= types.get(i).getName();
                }
            }

            int selectedIndex=0;
            ArrayList<String> stateNames= new ArrayList<String>();

            for(int i=0;i<states.size();i++)
            {
                stateNames.add(states.get(i).getName());

                if(states.get(i).getId()== task.getStateId())
                {
                    selectedIndex=i;
                }
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, stateNames);

            spinnerReadTaskState .setAdapter(dataAdapter);
            spinnerReadTaskState .setSelection(selectedIndex);


            txtReadTaskType.setText(taskTypeName);
            txtReadTaskDescription.setText(task.getDescription());

            if (id > 0) {
                Bitmap image = task.getImage();
                if (image != null) {
                    imageViewReadTaskPhoto .setImageBitmap(image);
                }
            }
        }
    }

    private class UpdateTaskStateTask extends AsyncTask<Task, Void, Void> {

        @Override
        public Void doInBackground(com.opencartis.georfid.Task... tasks) {

            TaskType taskType= new TaskType();
            types = taskType.GetTypes();

            states = taskType.GetStates();

            tasks[0].UpdateState();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(ReadTask.this, SelectOption.class);
            startActivity(intent);
        }
    }

}
