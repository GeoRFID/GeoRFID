package com.opencartis.georfid;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewElement extends AppCompatActivity {

    Button btnReadRfid;
    Button btnTakePhoto;
    Button btnPosition;
    ImageView imgTakenPhoto;
    private NfcAdapter mAdapter;
    private boolean mInWriteMode;
    //Intent i;
    private Uri fileUri;
    private Element element = new Element();
    static ArrayList<ElementType> types;
    Context context;
    Spinner spinner;
    TextView textViewRfidValue;
    boolean manualLocation = false;
    LocationListener locationListener;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_element);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        textViewRfidValue = (TextView) findViewById(R.id.textViewRfidValue);
        imgTakenPhoto = (ImageView) findViewById(R.id.imageViewTakenPhoto);
        btnTakePhoto = (Button) findViewById(R.id.btnTakePhoto);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewElement.this, NewElementData.class);
                intent.putExtra("element", element);
                startActivity(intent);
            }
        });

        try {
            element = (Element) getIntent().getExtras().get("element");
            context = this;
            if (element != null) {

                if(element.getLongitude()!=0 && element.getLatitude()!= 0) {
                    manualLocation = true;

                    TextView textView = (TextView) findViewById(R.id.txtLocation);

                    textView.setTextColor(Color.rgb(0, 188, 0));
                    textView.setText(element.getLatitude() + "/" + element.getLongitude() + " (0)");
                }
                Bitmap elementImage= element.getImage();
                if(elementImage!=null)
                {
                    imgTakenPhoto.setImageBitmap(elementImage);
                }

                if(element.getTag()!=null && element.getTag()!="") {
                    textViewRfidValue.setText(element.getTag());
                    textViewRfidValue.setTextColor(Color.rgb(0, 188, 0));
                }
            }
        } catch (Exception e) {
            manualLocation = false;
        }

        // grab our NFC Adapter
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        btnReadRfid = (Button) findViewById(R.id.btnReadRfid);

        btnReadRfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), R.string.read_rfid_nfc_instructions,Toast.LENGTH_LONG).show();
                enableWriteMode();
            }

        });



        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.disableForegroundDispatch(NewElement.this);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                }

                locationManager.removeUpdates(locationListener);

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                {
                    fileUri = Utils.getOutputMediaFileUri(Utils.MEDIA_TYPE_IMAGE); // create a file to save the image
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                    startActivityForResult(takePictureIntent, Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }

        });

        btnPosition = (Button) findViewById(R.id.btnPosition);

        btnPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewElement.this, Map.class);
                intent.putExtra("element", element);
                startActivity(intent);
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String spinnerValue = spinner.getSelectedItem().toString();

                for (int i = 0; i < types.size(); i++) {
                    if (types.get(i).getName() == spinnerValue) {
                        element.setElementType(types.get(i).getId());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            //return;
        }

        /* Datos de los proveedores*/
        //List<String> listaProveedores = locationManager.getAllProviders();
        //LocationProvider provider1= locationManager.getProvider(listaProveedores.get(0));
        //int Accuracy= provider1.getAccuracy();
        //boolean TieneAltitud=provider1.supportsAltitude();
        /*-----*/

        /* Establezco criterios para buscar un proveedor*/
        //Criteria criteria= new Criteria();
        //criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //String mejorProveedor = locationManager.getBestProvider(criteria,true);
        /*-----*/

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if(!manualLocation) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }

        context = this;

        if(types==null) {
            GetElementTypesTask getElementTypesTaks = new GetElementTypesTask();
            getElementTypesTaks.execute();
        }
        else
        {
            SetTypes();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        if(!manualLocation) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }

        if (requestCode == Utils.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent

                try {
                    //Bitmap img = MediaStore.Images.Media.getBitmap(this.getContentResolver(), fileUri);

                    /*int w= img.getWidth();
                    int h = img.getHeight();
*/
                    int newW= 1600;
                    int newH = 1200;

                    Bitmap reduced=Utils.getResizedBitmap(newW,newH,fileUri.getPath());
                    Bitmap reducedView=Utils.getResizedBitmap(400,300,fileUri.getPath());

                    imgTakenPhoto.setImageBitmap(reducedView);
                    element.setImage(reduced);

                    //img.recycle();
                }
                catch (Exception exc)
                {
                    String message=exc.getMessage();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }

    private void setLocation(Location location) {
        TextView textView = (TextView) findViewById(R.id.txtLocation);

        if (location != null) {
            element.setLatitude(location.getLatitude());
            element.setLongitude(location.getLongitude());
            textView.setText(String.valueOf(location.getLatitude()) + " / " + String.valueOf(location.getLongitude()) + " (" + location.getAccuracy() + ")");
            textView.setTextColor(Color.rgb(0, 188, 0));
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        disableWriteMode();
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

    /**
     * Format a tag and write our NDEF message
     */
    private void readTagId(Tag tag) {
        byte[] id = tag.getId();
        String hex = getHex(id);

        textViewRfidValue.setText(hex);
        textViewRfidValue.setTextColor(Color.rgb(0, 188, 0));
        element.setTag(hex);
    }

    private class GetElementTypesTask extends AsyncTask<Element, Void, Void> {
        @Override
        public Void doInBackground(com.opencartis.georfid.Element... elements) {

            if (types == null) {
                try {
                    ElementType elementType = new ElementType();
                    types = elementType.GetTypes();
                } catch (Exception exc) {
                    Toast.makeText(getApplicationContext(), exc.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SetTypes();
        }
    }

    private void SetTypes()
    {
        List<String> list = new ArrayList<String>();

        int selectedIndex=0;
        for (int i = 0; i < types.size(); i++) {
            list.add(types.get(i).getName());
            if(types.get(i).getId()== element.getElementType())
            {
                selectedIndex=i;
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, list);

        spinner.setAdapter(dataAdapter);
        spinner.setSelection(selectedIndex);
    }
}

