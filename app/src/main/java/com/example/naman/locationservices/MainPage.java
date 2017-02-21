package com.example.naman.locationservices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainPage extends AppCompatActivity {

    private static final String TAG ="MAIN_CODE" ;
    TextView textView;
    TextView tv2;
    EditText editText;
    EditText editText2;
    Button button;
    Intent intent;
    String APIurl;
    String time;
    String getEditText;
    String getTimeInterval;



    static int REQUEST_PERM = 445;

    /*String reqInternetPerm[] = new String[]{
            android.Manifest.permission.INTERNET,
    };*/


    String reqLocationPerm[] = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        setupUI();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getEditText = editText.getText().toString();
                getTimeInterval = editText2.getText().toString();

                if(TextUtils.isEmpty(getTimeInterval)&&TextUtils.isEmpty(getEditText) )
                    Toast.makeText(getApplicationContext(),"Fields cannot be empty!",Toast.LENGTH_SHORT).show();

                else
                {
                    if(TextUtils.isEmpty(getTimeInterval))
                        Toast.makeText(getApplicationContext(), "TimeInterval can not be empty!", Toast.LENGTH_SHORT).show();

                  else  if (TextUtils.isEmpty(getEditText)) {
                        Toast.makeText(getApplicationContext(), "URL field can not be empty!", Toast.LENGTH_LONG).show();
                    }

                    else if (!URLUtil.isValidUrl(getEditText)) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid URL", Toast.LENGTH_LONG).show();
                    }

                    else {

                        if (hasLocationPerm()) {
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            APIurl = editText.getText().toString();
                            intent.putExtra("API", APIurl);
                            time=editText2.getText().toString();
                            intent.putExtra("Time",time);
                            startActivity(intent);
                        }
                        else
                        {
                            askLocationPerm();

                            intent = new Intent(getApplicationContext(), MainActivity.class);
                            APIurl = editText.getText().toString();
                            intent.putExtra("API", APIurl);
                            time=editText2.getText().toString();
                            intent.putExtra("Time",time);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }


    private boolean hasLocationPerm() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private void askLocationPerm() {
        ActivityCompat.requestPermissions(this, reqLocationPerm, REQUEST_PERM);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERM) {

            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: CALL PERMISSION GRANTED");
                }
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void setupUI() {
        textView = (TextView) findViewById(R.id.tv);
       // tv2 = (TextView) findViewById(R.id.tv2);
        editText = (EditText) findViewById(R.id.edit_text);
        editText2 = (EditText) findViewById(R.id.edit_text2);
        button = (Button) findViewById(R.id.bt);
    }
}