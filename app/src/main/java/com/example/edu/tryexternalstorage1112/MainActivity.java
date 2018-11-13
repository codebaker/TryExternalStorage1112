package com.example.edu.tryexternalstorage1112;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    int REQUEST_CODE = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.buttonRead)).setOnClickListener(this);
        ((Button) findViewById(R.id.buttonWrite)).setOnClickListener(this);
        ((Button) findViewById(R.id.buttonReadPublic)).setOnClickListener(this);
        ((Button) findViewById(R.id.buttonWritePublic)).setOnClickListener(this);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v) {
        EditText editTextFileName = findViewById(R.id.editText);
        EditText editTextFileContents = findViewById(R.id.editText3);

        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED) == false){
            Log.e("ExternalStorageState","FALSE");
            return;
        }
        File fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);;
        File file= null;
        byte[] buffer = null;
        try {
            switch (v.getId()){
                case R.id.buttonRead:
                    file = new File(getExternalFilesDir(null),editTextFileName.getText().toString());
                    fileInputStream = new FileInputStream(file);
                    buffer = new byte[fileInputStream.available()];
                    fileInputStream.read(buffer);
                    editTextFileContents.setText(new String(buffer));
                    break;
                case R.id.buttonWrite:
                    file = new File(getExternalFilesDir(null),editTextFileName.getText().toString());
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(editTextFileContents.getText().toString().getBytes());
                    editTextFileContents.setText("");
                    break;
                case R.id.buttonReadPublic:
                    if (!fileDir.exists()) {
                        Log.e("fileDir",fileDir.getPath()+"is not found.");
                        break;
                    }
                    file = new File(fileDir,editTextFileName.getText().toString());
                    fileInputStream = new FileInputStream(file);
                    buffer = new byte[fileInputStream.available()];
                    fileInputStream.read(buffer);
                    editTextFileContents.setText(new String(buffer));
                    break;
                case R.id.buttonWritePublic:
                    if (!fileDir.exists()) fileDir.mkdirs();
                    file = new File(fileDir,editTextFileName.getText().toString());
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(editTextFileContents.getText().toString().getBytes());
                    editTextFileContents.setText("");
                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //예외가 발생했어도 반듯이 실행해야 하는 코드.
            //예를 들어... file이나 db의 close()문
            try{
                if(fileInputStream!=null){
                    fileInputStream.close();
                }
                if(fileOutputStream!=null){
                    fileOutputStream.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
