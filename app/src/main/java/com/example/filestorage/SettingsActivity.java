package com.example.filestorage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    private EditText editOldPassword;
    private EditText editNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editOldPassword = findViewById(R.id.editOldPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
    }

    public void OnClick(View view) {
        File digestFile = null;
        MessageDigest digest = null;
        byte[] pwdHash= new byte[32];
        byte[] actualHash = new byte[32];

        Editable oldPassword = editOldPassword.getText();
        try {
            digest = MessageDigest.getInstance("SHA-256");
            pwdHash = digest.digest(oldPassword.toString().getBytes());

            digestFile = getExternalFilesDir(null);
            digestFile = new File(digestFile + File.separator + "digest");
            FileInputStream inputStream = new FileInputStream(digestFile);
            inputStream.read(actualHash);
            inputStream.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (!Arrays.equals(pwdHash, actualHash)){
            Toast.makeText(SettingsActivity.this, R.string.wrong_old_password, Toast.LENGTH_SHORT).show();
            oldPassword.clear();
            return;
        }

        Editable newPassword = editNewPassword.getText();
        if (newPassword.toString().equals("")) {
            Toast.makeText(SettingsActivity.this, R.string.missing_new_password, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            pwdHash = digest.digest(newPassword.toString().getBytes());

            FileOutputStream outputStream = new FileOutputStream(digestFile);
            outputStream.write(pwdHash);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();
    }

    public void OnClickBack(View view) {
        finish();
    }

}