package com.example.smart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.speech_to_text.v1.websocket.BaseRecognizeCallback;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

public class TextofRecordActivity extends AppCompatActivity {
    TextView textOfRecord,textWaiting;
    Button btnZoom, btnCalendar;
    Intent intentcome;
    SpeechToText speechToText;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textof_record);
        textOfRecord = findViewById(R.id.text_of_record);
        textWaiting = findViewById(R.id.text_waiting);
        btnZoom = findViewById(R.id.schedule_meeting_by_zoom);
        btnCalendar = findViewById(R.id.schedule_meeting_by_calendar);
        textWaiting.setVisibility(View.GONE);
        btnCalendar.setEnabled(false);
        btnZoom.setEnabled(false);
        intentcome = getIntent();
        String path = intentcome.getStringExtra("path");

        IamAuthenticator authenticator = new IamAuthenticator("19pXTKHtHKoVXac3FZkZjTIjgsPWPriWqIO8jox-I9Jz");
        speechToText = new SpeechToText(authenticator);
        speechToText.setServiceUrl("https://api.eu-gb.speech-to-text.watson.cloud.ibm.com/instances/bd9beda8-0fb7-4cfb-bfca-6270b8c07cf6");

        if (savedInstanceState != null){
            text = savedInstanceState.getString("text");
        }
                try {
            textWaiting.setVisibility(View.VISIBLE);
            RecognizeOptions recognizeOptions = null;
                recognizeOptions = new RecognizeOptions.Builder()
                        .audio(new FileInputStream(path))
                        .contentType(HttpMediaType.AUDIO_MP3)
                        .model("en-US_BroadbandModel")
                        .keywords(Arrays.asList("colorado", "tornado", "tornadoes"))
                        .keywordsThreshold((float) 0.5)
                        .interimResults(true)
                        .inactivityTimeout(2000)
                        .maxAlternatives(3)
                        .build();

            BaseRecognizeCallback baseRecognizeCallback = new BaseRecognizeCallback() {
                @Override
                public void onTranscription(SpeechRecognitionResults speechRecognitionResults) {
                    if (speechRecognitionResults.getResults() != null && !speechRecognitionResults.getResults().isEmpty()) {
                        text = speechRecognitionResults.getResults().get(0).getAlternatives().get(0).getTranscript();

                        Handler handler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                // Any UI task, example

                                textOfRecord.setText(text);
                                textWaiting.setText("Transcription Waiting.....");

                            }


                        };
                        handler.sendEmptyMessage(1);
                    }
                }
                @Override
                public void onDisconnected() {

                }

                @Override
                public void onTranscriptionComplete() {
                    Handler handler = new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            textWaiting.setVisibility(View.GONE);
                            btnCalendar.setEnabled(true);
                            btnZoom.setEnabled(true);                           }
                    };
                    handler.sendEmptyMessage(1);
                                 }
            };

            speechToText.recognizeUsingWebSocket(recognizeOptions,
                            baseRecognizeCallback);
                } catch(FileNotFoundException e) {
                    Toast.makeText(TextofRecordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextofRecordActivity.this,CalendarActivity.class);
                startActivity(intent);
            }
        });
        btnZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextofRecordActivity.this,WelcomeMeeting.class);
                startActivity(intent);
            }
        });

            }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("text",text);
    }

}