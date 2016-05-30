package com.opencartis.georfid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NewTask extends AppCompatActivity {

    private static ArrayList<TaskType> types;
    private Context context;
    private Spinner spinner;
    private Task task= new Task();
    private Button btnTakePhoto;
    private ImageView imgTakenPhoto;
    private EditText txtDescription;
    private Intent i;
    final static int cons = 0;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_task);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(R.string.app_name);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                    fab.setEnabled(false);
                    Toast.makeText(getBaseContext(), R.string.savingincident, Toast.LENGTH_LONG).show();

                    CreateTaskTask createElement = new CreateTaskTask();
                    task.setElementoId((int) getIntent().getExtras().get("elementId"));
                    task.setDescription(txtDescription.getText().toString());
                    createElement.execute(task);
                }
            });

            spinner = (Spinner) findViewById(R.id.spinner2);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String spinnerValue = spinner.getSelectedItem().toString();

                for (int i = 0; i < types.size(); i++) {
                    if (types.get(i).getName() == spinnerValue) {
                        task.setTaskType(types.get(i).getId());
                    }
                }
            }

            @Override
           public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        context = this;

        if(types==null) {
            GetTaskTypesTask getElementTypesTaks = new GetTaskTypesTask();
            getElementTypesTaks.execute();
        }

        txtDescription= (EditText)findViewById(R.id.editTextTaskDescription);

        imgTakenPhoto = (ImageView) findViewById(R.id.imageViewCreteTaskTakenPhoto);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhotoTask);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = Utils.getOutputMediaFileUri(Utils.MEDIA_TYPE_IMAGE); // create a file to save the image
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                startActivityForResult(i, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                try {
                    //Bitmap img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);

                    //int w= img.getWidth();
                    //int h = img.getHeight();

                    int newW= 1600;
                    int newH = 1200;
                    Bitmap reduced=Utils.getResizedBitmap( newW, newH, fileUri.getPath());
                    Bitmap reducedView=Utils.getResizedBitmap(400,300,fileUri.getPath());

                    imgTakenPhoto.setImageBitmap(reducedView);
                    task.setImage(reduced);

                    //img.recycle();
                }
                catch (Exception exc)
                {
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    private class GetTaskTypesTask extends AsyncTask<Element, Void, Void> {
        @Override
        public Void doInBackground(com.opencartis.georfid.Element... elements) {

            if(types==null) {
                TaskType taskType = new TaskType();
                types = taskType.GetTypes();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            List<String> list = new ArrayList<String>();

            int selectedIndex=0;
            for (int i = 0; i < types.size(); i++) {
                list.add(types.get(i).getName());
                if(types.get(i).getId()== task.getTaskType())
                {
                      selectedIndex=i;
                }
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, list);

            spinner.setAdapter(dataAdapter);
            spinner.setSelection(selectedIndex);
        }
    }

    private class CreateTaskTask extends AsyncTask<Task, Void, Void> {
        @Override
        public Void doInBackground(com.opencartis.georfid.Task... tasks) {
            tasks[0].Create();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);

            String message;
            if (task.getId() > 0) {
                message = getString(R.string.tasksaved) + task.getId();
            } else {
                message = getString(R.string.errorsavingtask);
            }

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(NewTask.this, SelectOption.class);
            startActivity(intent);
        }
    }
}
