package com.opencartis.georfid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    Button btnLogin;
    User user;
    EditText editTextLogin;
    EditText editTextPassword;
    CheckBox chkRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        editTextLogin = (EditText) findViewById(R.id.editTextLoginUser);
        editTextPassword = (EditText) findViewById(R.id.editTextLoginPassword);
        chkRememberMe = (CheckBox) findViewById(R.id.chkRememberMe);


        Settings settings = new Settings();
        settings.LoadPreferences(getApplicationContext());
        String loginUser = Settings.getLoginUser();
        String loginPassword = Settings.getLoginPassword();

        editTextLogin.setText(loginUser);
        editTextPassword.setText(loginPassword);
        chkRememberMe.setChecked(loginPassword != "");


        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            user = new User();
                                            user.setLogin(editTextLogin.getText().toString());
                                            user.setPassword(editTextPassword.getText().toString());

                                            Settings settings = new Settings();
                                            settings.LoadPreferences(getApplicationContext());

                                            CheckUserTask checkUserTask = new CheckUserTask();
                                            checkUserTask.execute(user);
                                        }
                                    }

        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CheckUserTask extends AsyncTask<User, Void, Void> {

        @Override
        public Void doInBackground(com.opencartis.georfid.User... users) {
            users[0].IsValid();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String message;
            String name = user.getName();

            Settings settings = new Settings();

            if (!chkRememberMe.isChecked()) {
                settings.SaveLoginPassword(getApplicationContext(), "");
            }

            if (name != "") {

                settings.SaveLoginUser(getApplicationContext(), user.getLogin());

                if (chkRememberMe.isChecked()) {
                    settings.SaveLoginPassword(getApplicationContext(), user.getPassword());
                }

                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref",0);
                SharedPreferences.Editor editor= pref.edit();
                editor.putBoolean("admin", user.isAdmin());
                editor.commit();

                message = getString(R.string.session_started) + name;
                Intent intent = new Intent(Login.this, SelectOption.class);
                startActivity(intent);
            } else {
                message = getString(R.string.user_no_valid);
            }

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
