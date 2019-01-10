package com.test.owczar.game;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;

import static android.graphics.BitmapFactory.decodeStream;

public class Game extends AppCompatActivity {


    boolean[] isClicked = {false, false, false, false, false, false};
    ImageView[] availableThumbnails;
    Bitmap[] takenPhotos;
    String[] takenPhotosPaths;
    DatabaseHelper myDb;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        myDb = new DatabaseHelper(this);

        takenPhotos = new Bitmap[3];
        takenPhotosPaths = new String[3];
        Cursor data = myDb.getAllData();

        availableThumbnails = new ImageView[6];
        availableThumbnails[0] = findViewById(R.id.imageView);
        availableThumbnails[1] = findViewById(R.id.imageView2);
        availableThumbnails[2] = findViewById(R.id.imageView3);
        availableThumbnails[3] = findViewById(R.id.imageView4);
        availableThumbnails[4] = findViewById(R.id.imageView5);
        availableThumbnails[5] = findViewById(R.id.imageView6);
        shuffleArray(availableThumbnails);
        setupListeners(availableThumbnails);
        if (data.getCount() != 0) {
            int i = 0;
            while (data.moveToNext() && i<3) {
                takenPhotosPaths[i] = data.getString(1);
                takenPhotos[i++] = loadImageFromStorage(data.getString(1), data.getString(2));
            }
        }
    }


    private Bitmap loadImageFromStorage(String path, String fileName) {
        try {
            File fileToLoad = new File(path, fileName);
            return decodeStream(new FileInputStream(fileToLoad));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean userPickedTwoImgs(boolean[] arr) {
        int clickedImgs = 0;
        for (boolean arrayItem : arr) {
            if(arrayItem)
                clickedImgs++;
        }
        return clickedImgs >= 2;
    }

    public boolean Match() {
        for(int i = 0; i < 5; i+=2) {
            if(isClicked[i] && isClicked[i+1]) {
                fadeOutAndHideImage(availableThumbnails[i]);
                fadeOutAndHideImage(availableThumbnails[i+1]);
                isClicked[i] = false;
                isClicked[i+1] = false;
                return true;
            }
        }
        return false;
    }

    public void fadeOutAndHideImage(final ImageView img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.INVISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        img.startAnimation(fadeOut);
    }

    public void restoreQuestionMarks(final ImageView img) {
        Animation restoreQMark = new AlphaAnimation(1, 0);
        restoreQMark.setInterpolator(new AccelerateInterpolator());
        restoreQMark.setDuration(500);
        restoreQMark.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setImageResource(R.drawable.ic_action_name);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });
        img.startAnimation(restoreQMark);
    }

    public void setupListeners(final ImageView[] arr) {
        for (final ImageView imgView : arr) {
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(takenPhotos[0] == null || takenPhotos[1] == null || takenPhotos[2] == null) {
                        return;
                    }
                    for(int i = 0, j = 0; i <= 5; i++) {
                        if(i % 2 == 0 && i != 0) j++;
                        if (v == availableThumbnails[i]) {
                            imgView.setImageBitmap(takenPhotos[j]);
                            isClicked[i] = true;
                        }
                    }
                    if(!Match()) {
                        if(userPickedTwoImgs(isClicked)) {
                            int i = 0;
                            for (final ImageView img : arr) {
                                if(isClicked[i++])
                                    restoreQuestionMarks(img);
                            }
                            Arrays.fill(isClicked, false);
                        }
                    }
                }
            });
        }
    }


    public void restartGame(View view) {
        shuffleArray(availableThumbnails);
        setupListeners(availableThumbnails);
        for (final ImageView img : availableThumbnails) {
            restoreQuestionMarks(img);
            img.setVisibility(View.VISIBLE);
        }
        Arrays.fill(isClicked, false);
    }

    public void shuffleArray(ImageView[] arr) {
        Random rand = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            ImageView a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    public void Menu(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
