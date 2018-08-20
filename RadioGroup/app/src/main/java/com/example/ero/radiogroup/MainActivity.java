package com.example.ero.radiogroup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    private RadioButton internal;
    private RadioButton external;
    private EditText fileName;
    private EditText fileText;
    private String path;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileName = findViewById(R.id.edit_text1);
        fileText = findViewById(R.id.edit_text2);
        final Button save = findViewById(R.id.save);
        final Button read = findViewById(R.id.read);
        internal = findViewById(R.id.radio1);
        external = findViewById(R.id.radio2);
        requestPermissions();
        saveClick(save);
        readClick(read);
    }

    private void readClick(Button read) {
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileName.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.input_name, Toast.LENGTH_LONG).show();
                } else if (fileText.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.input_text, Toast.LENGTH_LONG).show();
                } else if (filePath() == null) {
                    Toast.makeText(MainActivity.this, R.string.save_file, Toast.LENGTH_LONG).show();
                } else {
                    final File file = new File(filePath());
                    if (filePath() != null && file.exists()) {
                        try {
                            final FileInputStream fileInputStream = new FileInputStream(file);
                            final DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
                            String buffer;
                            while ((buffer = bufferedReader.readLine()) != null) {
                                data = buffer;
                            }
                            dataInputStream.close();
                            fileText.setText(data);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "NULL", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void saveClick(Button save) {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath() != null) {
                    final File file = new File(filePath());
                    try {
                        final FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write(fileText.getText().toString().getBytes());
                        fileOutputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Checked Internal or External", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String filePath() {
        if (internal.isChecked()) {
            path = getFilesDir().getAbsolutePath() + "/" + fileName.getText().toString();
        } else if (external.isChecked()) {
            path = Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" +
                    fileName.getText().toString();
        }
        return path;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }
        }
    }
}
