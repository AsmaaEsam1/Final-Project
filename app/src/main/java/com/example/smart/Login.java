package com.example.smart;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;

public class Login extends AppCompatActivity implements ZoomSDKAuthenticationListener {
    private EditText mEdtUserName;
    private EditText mEdtPassword;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEdtUserName = findViewById(R.id.etUser);
        mEdtPassword = findViewById(R.id.etPassword);

        mBtnLogin = findViewById(R.id.btLogin2);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                String userName = mEdtUserName.getText().toString().trim();
                String password = mEdtPassword.getText().toString().trim();
                if(userName.length() == 0 || password.length() == 0) {
                    Toast.makeText(Login.this, "You need to enter user name and password.", Toast.LENGTH_LONG).show();
                    return;
                }
                ZoomSDK zoomSDK = ZoomSDK.getInstance();
                if(!(zoomSDK.loginWithZoom(userName, password) == ZoomApiError.ZOOM_API_ERROR_SUCCESS)) {
                    Toast.makeText(Login.this, "ZoomSDK has not been initialized successfully or sdk is logging in.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Login.this, "ZoomSDK has been initialized successfully or sdk is logging in.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Login.this, ZoomActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


    @Override
    public void onZoomSDKLoginResult(long result) {
        if(result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            Toast.makeText(this, "Login successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Login failed result code = " + result, Toast.LENGTH_SHORT).show();
        }
        mBtnLogin.setVisibility(View.VISIBLE);

    }

    @Override
    public void onZoomSDKLogoutResult(long result) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }



    @Override
    public void onZoomAuthIdentityExpired() {

    }

}
