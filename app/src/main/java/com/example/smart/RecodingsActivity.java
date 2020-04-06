package com.example.smart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecodingsActivity extends AppCompatActivity {
    ListView listOfRecords;
   final  ArrayList <records> ListRecords = new ArrayList<records>();
    recordAdapter adapter ;
    File file;
private static final String MEDIA_PATH = new String(
            Environment.getExternalStorageDirectory() + "/AudioRecorder");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recodings);
        listOfRecords = findViewById(R.id.list_recordings);
        file = new File(MEDIA_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        File list[] = file.listFiles();
        for( int i=0; i< list.length; i++)
        {
            ListRecords.add( new records(list[i].getName(),list[i].getPath()));
        }
        adapter = new recordAdapter(this,  ListRecords);
        listOfRecords.setAdapter(adapter);
        listOfRecords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                records path = ListRecords.get(position);
                Intent intent = new Intent(RecodingsActivity.this,TextofRecordActivity.class);
                intent.putExtra("path",path.getRecordPath());
                Toast.makeText(RecodingsActivity.this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
            }
        });
    }


}
