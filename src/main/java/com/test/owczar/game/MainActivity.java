package com.test.owczar.game;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class MainActivity extends AppCompatActivity {

    Bitmap[] takenPhotos;
    String[] takenPhotosPaths;
    static int takenImgsCounter = 0;
    DatabaseHelper myDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        takenPhotos = new Bitmap[3];
        takenPhotosPaths = new String[3];
        myDb.deleteData();
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        String fileName = "pic_" + takenImgsCounter + ".jpg";
        File imgPath = new File(directory, fileName);
        FileOutputStream outputPath = null;
        try {
            outputPath = new FileOutputStream(imgPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 80, outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert outputPath != null;
                outputPath.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(myDb.insertData(directory.getAbsolutePath(), fileName))
            Toast.makeText(MainActivity.this, "Image saved", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
        return directory.getAbsolutePath();
    }


    public void StartGame(View view)
    {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, ++takenImgsCounter);
        }
        if(takenImgsCounter > 3) {
            myDb.deleteData();
            takenImgsCounter = 1;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            assert extras != null : "Received data is null";
            takenPhotos[takenImgsCounter-1] = (Bitmap)(extras.get("data"));
            takenPhotosPaths[takenImgsCounter-1] = saveToInternalStorage(takenPhotos[takenImgsCounter-1]);
        }
    }






}
