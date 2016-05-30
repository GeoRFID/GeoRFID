package com.opencartis.georfid;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.opencartis.georfid.DataBase.Property;

import java.util.ArrayList;

public class ReadElement extends AppCompatActivity {


    Element element;
    ArrayList<ElementType> types;
    Button btnReadElement;
    Button btnDeleteElement;
    Button btnCreateTask;
    Button btnViewTask;
    ImageView readElementImage;
    TextView textViewReadElementTag;
    private NfcAdapter mAdapter;
    private boolean mInWriteMode;
    LinearLayout readElementDataContent;
    TextView textViewReadElementType;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_element);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        context = this;

        // grab our NFC Adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        readElementImage = (ImageView) findViewById(R.id.readElementImage);
        textViewReadElementTag = (TextView) findViewById(R.id.textViewReadElementTag);
        textViewReadElementType = (TextView) findViewById(R.id.textViewReadElementType);
        readElementDataContent = (LinearLayout) findViewById(R.id.readElementDataContent);
        btnReadElement = (Button) findViewById(R.id.btnReadElement);
        btnDeleteElement = (Button) findViewById(R.id.btnRemoveElement);
        btnDeleteElement.setVisibility(View.INVISIBLE);
        btnCreateTask= (Button) findViewById(R.id.btnCreateTask);
        btnCreateTask.setVisibility(View.INVISIBLE);
        btnViewTask= (Button) findViewById(R.id.btnViewTask);
        btnViewTask.setVisibility(View.INVISIBLE);

        btnReadElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getApplicationContext();
                Settings settings = new Settings();
                settings.LoadPreferences(context);

                Toast.makeText(getApplicationContext(), R.string.read_rfid_nfc_instructions, Toast.LENGTH_LONG).show();
                enableWriteMode();
            }
        });

        btnDeleteElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context context = getApplicationContext();
                Settings settings = new Settings();
                settings.LoadPreferences(context);

                AlertDialog.Builder builder = new AlertDialog.Builder(ReadElement.this);

                builder.setTitle(R.string.confirm);
                builder.setMessage(R.string.areyousuredeleteelement);

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        dialog.dismiss();

                        DeleteElementTask deleteElementTask = new DeleteElementTask();
                        deleteElementTask.execute(element);
                    }

                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadElement.this, NewTask.class);
                intent.putExtra("elementId", element.getId());
                startActivity(intent);
            }
        });

        btnViewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadElement.this, ReadTask.class);
                intent.putExtra("taskId", element.getTaskIds().get(0));
                startActivity(intent);
            }
        });
    }

    private void enableWriteMode() {
        mInWriteMode = true;

        // set up a PendingIntent to open the app when a tag is scanned
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] filters = new IntentFilter[]{tagDetected};

        mAdapter.enableForegroundDispatch(this, pendingIntent, filters, null);
    }

    /**
     * Called when our blank tag is scanned executing the PendingIntent
     */
    @Override
    public void onNewIntent(Intent intent) {
        if (mInWriteMode) {
            mInWriteMode = false;

            // write to newly scanned tag
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            readTagId(tag);
        }
    }

    private void readTagId(Tag tag) {
        byte[] id = tag.getId();
        String hex = getHex(id);

        textViewReadElementTag.setText(hex);

        Toast.makeText(getBaseContext(), R.string.readingelementdata, Toast.LENGTH_LONG).show();

        element = new Element();
        element.setTag(hex);

        ReadElementTask readElementTask = new ReadElementTask();
        readElementTask.execute(element);
    }


    private void disableWriteMode() {
        mAdapter.disableForegroundDispatch(this);
    }

    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private class ReadElementTask extends AsyncTask<Element, Void, Void> {

        @Override
        public Void doInBackground(com.opencartis.georfid.Element... elements) {

            ElementType elementType = new ElementType();
            types = elementType.GetTypes();

            elements[0].Read();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);

            int id = element.getId();

            textViewReadElementTag.setText(element.getTag());

            if (id > 0) {
                Bitmap image = element.getImage();
                if (image != null) {
                    readElementImage.setImageBitmap(image);
                }

                String elementTypeName="";
                for(int i=0;i<types.size();i++)
                {
                    if(types.get(i).getId()==element.getElementType())
                    {
                        elementTypeName=types.get(i).getName();
                        break;
                    }
                }

                textViewReadElementType.setText(elementTypeName);

                readElementDataContent.removeViews(2,readElementDataContent.getChildCount()-2);

                for (int i = 0; i < element.getProperties().size(); i++) {
                    Property property = element.getProperties().get(i);

                    LinearLayout horizontalLayout = new LinearLayout(context);
                    horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

                    TextView textView = new TextView(ReadElement.this);
                    textView.setWidth(250);
                    textView.setText(property.getName());

                    horizontalLayout.addView(textView);

                    textView = new TextView(ReadElement.this);
                    //textView.setWidth(150);
                    textView.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));

                    switch (property.getFieldType()) {
                        case 3: {
                            if (property.getValue() == null || property.getValue()=="0") {
                                textView.setText(R.string.no);
                            } else {
                                textView.setText(R.string.yes);
                            }
                            break;
                        }
                        default: {
                            if (property.getValue() != null) {
                                textView.setText(property.getValue());
                            }
                            break;
                        }
                    }


                    horizontalLayout.addView(textView);

                    readElementDataContent.addView(horizontalLayout);

                    btnDeleteElement.setVisibility(View.VISIBLE);

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
                    boolean isAdmin=pref.getBoolean("admin",false);

                    btnDeleteElement.setEnabled(isAdmin);

                    if(element.getTaskIds().size()>0)
                    {
                        btnViewTask.setVisibility(View.VISIBLE);
                        btnViewTask.setEnabled(element.getTaskIds().size() > 0);
                    }
                    else
                    {
                        btnCreateTask.setVisibility(View.VISIBLE);
                        btnCreateTask.setEnabled(isAdmin);
                    }
                }

            } else {
                String message;
                message = getString(R.string.no_data_tag);
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class DeleteElementTask extends AsyncTask<Element, Void, Void> {

        @Override
        public Void doInBackground(com.opencartis.georfid.Element... elements) {

            elements[0].Delete();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            int id = element.getId();
            String message;

            if (id > 0) {
                message = getString(R.string.error_deleting_element);
            } else {

                message = getString(R.string.element_deleted);
            }
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(ReadElement.this, SelectOption.class);
            startActivity(intent);
        }
    }
}
