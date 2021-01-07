package com.livingstonei2p.otpdummy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import  com.livingstonei2p.otpdummy.OTP;

import java.io.IOException;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {
    final int PICKFILE_RESULT_CODE = 1;
    private String mKeyFilePath;
    private boolean toDecrypt=false;
    public String getPath(Uri uri) {

        String path = null;
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if(cursor == null){
            path = uri.getPath();
        }
        else{
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndexOrThrow(projection[0]);
            path = cursor.getString(column_index);
            cursor.close();
        }
        return ((path == null || path.isEmpty()) ? (uri.getPath()) : path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
                Uri content_describer = data.getData();
                mKeyFilePath = new String(getPath(content_describer));
            }
        }catch(Throwable exc){
            System.out.println("ERROR: onActivityResult;");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //View encryptedDecrypted = findViewById(R.id.encryptedDecrypted);

        Button buttonEncrypt = (Button) findViewById(R.id.button_encrypt);
        Button buttonDecrypt = (Button) findViewById(R.id.button_decrypt);

        Button buttonKeyFile = (Button) findViewById(R.id.button_select_keyfile);
        //EditText textToEncrypt = (EditText)findViewById(R.id.toEncrypt);
        EditText toEncrypt = (EditText)findViewById(R.id.toEncrypt);
        Editable text = toEncrypt.getText();
        EditText encryptedDecrypted = (EditText)findViewById(R.id.encryptedDecrypted);
        EditText offset = (EditText)findViewById(R.id.offset);


            //Buttons
        buttonDecrypt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    toDecrypt = true;
                    buttonEncrypt.performClick();
                    String data = encryptedDecrypted.getText().toString();
                    encryptedDecrypted.setText(data.substring(0, data.length() - 2));
                }catch(Throwable exc){
                    encryptedDecrypted.setText( "Error: "+exc.toString() );
                }
            }
        });
        buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (mKeyFilePath == null){
                        encryptedDecrypted.setText( "Error: select your keyFile" );
                        return;
                    }
                    int localOffset = Integer.parseInt( offset.getText().toString() );

                    OTP crypter = new OTP(mKeyFilePath);
                    if(localOffset > 0){
                        crypter.setOffset(localOffset);
                    }
                    String data = crypter.doCryptDecrypt(text.toString()
                            .replace('\n', ' '),toDecrypt);
                    toDecrypt=false;
                    encryptedDecrypted.setText( data );
                    //crypter.ReSizeFile();
                    offset.setText("New your offset: "+crypter.getOffset());
                }catch(IOException exception){
                    encryptedDecrypted.setText( "Error: "+exception.toString() );
                }catch(NumberFormatException exception){
                    encryptedDecrypted.setText( "Error: "+exception.toString() );
                }catch(Throwable exc){
                    encryptedDecrypted.setText( "Error: "+exc.toString() );
                }

            }
        });
        buttonKeyFile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(chooseFile, PICKFILE_RESULT_CODE);
            }
        });
    }
}