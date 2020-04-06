package com.example.smart;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telecom.PhoneAccount;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {
    EditText editNameCaller, editPhoneNumber;
    Button btnCalling;
    ToggleButton btnRecording;
    String[] permissions ={
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (String premission:permissions) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, premission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
            }
        }
      //  editNameCaller = findViewById(R.id.name_caller);
        editPhoneNumber = findViewById(R.id.phone_number);
        btnCalling = findViewById(R.id.btn_calling);
        btnCalling.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                String number = editPhoneNumber.getText().toString().trim();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ number));
                startActivity(callIntent);
            }
        });
        btnRecording = findViewById(R.id.record);
        btnRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((ToggleButton) v).isChecked();
                if (checked){
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.RECORD_AUDIO) + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) +
                            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)+
                            ContextCompat.checkSelfPermission(MainActivity.this,
                                    Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(MainActivity.this, "You have already granted this premission!", Toast.LENGTH_SHORT).show();
                    } else {
                        requestRecordPremission();
                    }
                    // Start Service to Stop Record

                    Intent intent = new Intent(MainActivity.this, Recording.class);
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "Call Recording Start", Toast.LENGTH_SHORT).show();

                }
                else {
                 // Stop Service to Stop Record
                    Intent intent = new Intent(MainActivity.this, Recording.class);
                    stopService(intent);
                    Toast.makeText(getApplicationContext(), "Call Recording Stop", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(MainActivity.this,RecodingsActivity.class);
                    startActivity(intent1);
                }
            }
        });
    }
    public void requestRecordPremission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.RECORD_AUDIO)||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.CALL_PHONE)||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)||ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
            new AlertDialog.Builder(this)
                    .setTitle("premission needed")
                    .setMessage("You should allow this premission!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
                        }

                    })
                    .setNegativeButton("cancal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Premission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Premission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
