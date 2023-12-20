package com.example.filestorage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("filestorage");
    }
    private native byte[] encryptFile(byte[] plaintext, int len);
    private native byte[] decryptFile(byte[] ciphertext, int len);

    private FileAdapter adapter;
    private ArrayList<String> encryptedFiles;
    private ArrayList<String> currentSelectedItems;
    private File cryptSdPath;
    private File decryptSdPath;
    private final String ENC_SUFFUX = ".encrypted";

    private File getDirSdPath(String dirName) {
        File sdPath = Environment.getExternalStorageDirectory();

        sdPath = new File(sdPath.getAbsolutePath() + File.separator + dirName);
        Path path = Paths.get(sdPath.getAbsolutePath());
        if (!Files.exists(path)) {
            sdPath.mkdir();
        }

        return sdPath;
    }

    private void loadFilesList() {
        File[] files = cryptSdPath.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                continue;

            String name = file.getName();
            if (name.endsWith(ENC_SUFFUX)) {
                name = name.replace(ENC_SUFFUX, "");
            }
            else{
                try {
                    FileInputStream fis = new FileInputStream(file);
                    int size = fis.available();
                    byte[] in = new byte[size];
                    fis.read(in);
                    fis.close();

                    byte[] out = encryptFile(in, size);

                    FileOutputStream fos = new FileOutputStream(file + ENC_SUFFUX);
                    fos.write(out);
                    fos.close();

                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            encryptedFiles.add(name);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
        startActivity(intent);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        currentSelectedItems = new ArrayList<>();
        encryptedFiles = new ArrayList<>();
        cryptSdPath = getDirSdPath("crypt");
        decryptSdPath = getDirSdPath("decrypt");

        loadFilesList();
        adapter = new FileAdapter(encryptedFiles, new FileAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(String item) {
                currentSelectedItems.add(item);
            }

            @Override
            public void onItemUncheck(String item) {
                currentSelectedItems.remove(item);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void onClick(View view) {
        Intent newIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(newIntent);
    }

    public void onClickRefresh(View view) {
        encryptedFiles.clear();
        loadFilesList();
        adapter.notifyDataSetChanged();
    }

    public void onClickDecrypt(View view) {
        for (String fileName : currentSelectedItems) {
            try {
                FileInputStream fis = new FileInputStream(cryptSdPath + File.separator + fileName + ENC_SUFFUX);
                int size = fis.available();
                byte[] in = new byte[size];
                fis.read(in);
                fis.close();

                byte[] out = decryptFile(in, size);

                FileOutputStream fos = new FileOutputStream(decryptSdPath + File.separator + fileName);
                fos.write(out);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        currentSelectedItems.clear();
        adapter.resetAll();
    }
}