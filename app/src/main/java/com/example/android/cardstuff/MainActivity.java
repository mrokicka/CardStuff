package com.example.android.cardstuff;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private ArrayList<String> cards;
    private Random rand;
    private GridLayout playerHand;
    private int[] playerV;
    private GridLayout enemyHand;
    private int[] enemyV;
    private GridLayout field;
    private int[] fieldV;
    private HashMap<ImageView, Integer> test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerHand = findViewById(R.id.grid);
        enemyHand = findViewById(R.id.opponentHand);
        field = findViewById(R.id.field);

        playerV = new int[9];
        enemyV = new int[9];
        fieldV = new int[9];
        test = new HashMap<ImageView, Integer>();

        cards = new ArrayList<String>();
        rand = new Random();

        AssetManager manager = getAssets();

        try {
            for(String filename : manager.list("playingCards")) { //The empty string is the sub-directory where the files are located. Because we just put it into the folder without a directory, we leave it blank.
                cards.add(filename);
            }
        } catch(IOException e) {
            Toast.makeText(this, "Bad assets!", Toast.LENGTH_LONG).show();
        }


        Random r = new Random();
        try {
            for(int i = 0; i < 9; i++) {
                ImageView imageview = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, playerHand, false);
                ImageView imageview1 = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, enemyHand, false);

                int x = r.nextInt(cards.size());  //picks a card to put in the player hand by random
                int y = r.nextInt(cards.size()); //picks a card to put in the enemy hand by random

                playerV[i] = x;
                enemyV[i] = y;

                imageview.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("playingCards/" + cards.get(x))));
                imageview1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("playingCards/" + cards.get(y))));

                playerHand.addView(imageview);
                enemyHand.addView(imageview1);

                int j = 0;
                String temp = "";
                while(Character.isDigit(cards.get(x).charAt(j))) {
                    temp += cards.get(x).charAt(j);
                    j++;
                }
                test.put(imageview, Integer.parseInt(temp));

                j = 0;
                temp = "";
                while(Character.isDigit(cards.get(y).charAt(j))) {
                    temp += cards.get(y).charAt(j);
                    j++;
                }
                test.put(imageview1, Integer.parseInt(temp));
            }
        } catch(IOException e) {
            Toast.makeText(this, "Assets file does not exist.", Toast.LENGTH_LONG).show();
        }


    }

    public void imagePressed(View v) {
        GridLayout g = (GridLayout) v.getParent();
        if(!g.equals(field)) {
            g.removeView(v); //removes the card from whomever's hand.
            field.addView(v); //adds it to the middle -- ERRORs occasionally for some reason:  IllegalArgumentException column indices (start + span) mustn't exceed the column count.

            int x = test.get(v);
            Toast.makeText(this, "The value of this view was " + x, Toast.LENGTH_LONG).show();
            updateGame();
        }
    }

    public void updateGame() {

    }

}
