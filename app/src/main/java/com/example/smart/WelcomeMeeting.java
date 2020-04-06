package com.example.smart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import us.zoom.sdk.ZoomApiError;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitializeListener;

public class WelcomeMeeting extends AppCompatActivity implements Constants, ZoomSDKInitializeListener, ZoomSDKAuthenticationListener, View.OnClickListener {
    private Button login;
    private ZoomSDK mZoomSDK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_meeting);

        mZoomSDK = ZoomSDK.getInstance();
        if(mZoomSDK.isLoggedIn()) {
            finish();
            showMainActivity();
            return;        }

        login = findViewById(R.id.btlogIn);
        login.setOnClickListener(this);
        if(savedInstanceState == null) {
            mZoomSDK.initialize(this, APP_KEY, APP_SECRET, WEB_DOMAIN, this);
        }

        if(mZoomSDK.isInitialized()) {
            login.setVisibility(View.VISIBLE);
        }
        else {
            login.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btlogIn) {
            showLoginActivity();
        }
    }

    @Override
    public void onZoomSDKLoginResult(long result) {
        if((int)result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
            showMainActivity();
            finish();
        } else {
            login.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onZoomSDKLogoutResult(long result) {

    }

    @Override
    public void onZoomIdentityExpired() {

    }

    @Override
    public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
        if( errorCode != ZoomError.ZOOM_ERROR_SUCCESS) {
            Toast.makeText(this, "Failed to initialize Zoom SDK. Error: " + errorCode + ", internalErrorCode=" + internalErrorCode, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Initialize Zoom SDK successfully.", Toast.LENGTH_LONG).show();

            if(mZoomSDK.tryAutoLoginZoom() == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
                mZoomSDK.addAuthenticationListener(this);
                login.setVisibility(View.GONE);
            } else {
                login.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onZoomAuthIdentityExpired() {

    }
    private void showLoginActivity() {
        Intent intent = new Intent(WelcomeMeeting.this, Login.class);
        startActivity(intent);
    }

    private void showMainActivity() {
        Intent intent = new Intent(WelcomeMeeting.this, ZoomActivity.class);
        startActivity(intent);
    }
}
