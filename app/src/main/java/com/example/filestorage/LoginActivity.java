package com.example.filestorage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LoginActivity extends AppCompatActivity {

    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editPassword = findViewById(R.id.editPassword);
    }

    public void onClick(View view) {
        byte[] pwdHash= new byte[32];
        byte[] actualHash = new byte[32];

        try {
            Editable password = editPassword.getText();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            pwdHash = digest.digest(password.toString().getBytes());
            password.clear();

            File digestFile = getExternalFilesDir(null);
            digestFile = new File(digestFile + File.separator + "digest");
            FileInputStream inputStream = new FileInputStream(digestFile);
            inputStream.read(actualHash);
            inputStream.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (!Arrays.equals(pwdHash, actualHash)){
            Toast.makeText(LoginActivity.this, R.string.wrong_password, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent newIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(newIntent);
    }

}