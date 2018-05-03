package com.example.android.cardstuff;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private ArrayList<String> cards;
    private GridLayout playerHand;
    private GridLayout enemyHand;
    private GridLayout field;
    private GridLayout enemyField;
    private HashMap<ImageView, Integer> test;
    private boolean pTurn;
    private boolean gameStart;
    private TextView playerPoints;
    private TextView enemyPoints;
    private boolean isClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playerHand = findViewById(R.id.grid);
        enemyHand = findViewById(R.id.opponentHand);
        field = findViewById(R.id.field2);
        enemyField = findViewById(R.id.field1);
        isClickable = true;

        playerPoints = findViewById(R.id.pPoints);
        enemyPoints = findViewById(R.id.ePoints);

        test = new HashMap<ImageView, Integer>();
        pTurn = true;
        gameStart = false;

        cards = new ArrayList<String>();

        AssetManager manager = getAssets();

        try {
            for(String filename : manager.list("playingCards")) { //The empty string is the sub-directory where the files are located. Because we just put it into the folder without a directory, we leave it blank.
                cards.add(filename);
            }
        } catch(IOException e) {
            Toast.makeText(this, "Bad assets!", Toast.LENGTH_LONG).show();
        }

        loadCards();
    }

    public void loadCards() {
        Random r = new Random();
        try {
            for(int i = 0; i < 9; i++) {
                ImageView imageview = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, playerHand, false);
                ImageView imageview1 = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, enemyHand, false);

                int x = r.nextInt(cards.size());  //picks a card to put in the player hand by random
                int y = r.nextInt(cards.size()); //picks a card to put in the enemy hand by random

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
    /**
     * This method takes the (ImageView) v from the player's hand and puts it into the middle if
     * it is the player's turn.
     * @param v = the imageview/card that was clicked
     */
    public void imagePressed(View v) {
        GridLayout g = (GridLayout) v.getParent();
        if(gameStart && pTurn && g.equals(playerHand)) {
            pTurn = false;
            int x;
            if(test.get(v) == 11) {
                cardEleven(g, (ImageView) v);
                x = 0;
            } else if(test.get(v) == 12) {
                cardTwelve(g, (ImageView) v);
                x = 0;
            } else if(test.get(v) == 13) {
                cardThirteen(g, (ImageView) v);
                x = 0;
            } else {
                g.removeView(v); //removes the card from whomever's hand.

                ImageView temp = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, field, false);
                temp.setImageDrawable(((ImageView) v).getDrawable());
                field.addView(temp); // adds the card to the middle/field

                test.put(temp, test.get(v));
                test.remove(v);

                x = test.get(temp);
            }
            updateGame(x);
        }
    }

    /**
     * The method should first update the result of the playerTurn.
     * This method performs the opponent's turn. If the opponent is unable to proceed or both are
     * out of cards, the game ends by declaring the winner.
     */
    public void updateGame(int x) {
        boolean playable = false;

        for(int i = 0; i < enemyHand.getChildCount(); i++) {
            int temp = test.get(enemyHand.getChildAt(i));
            if(temp == 11 && x >= 8) { // play the jack to get rid of their card
                cardEleven(enemyHand, (ImageView) enemyHand.getChildAt(i));
                pTurn = true;
                playerPoints.setText(getValue(field) + "");
                playable = true;
                break;

            } else if(temp == 12 && x >= 8 && test.get(enemyField.getChildAt(enemyField.getChildCount() - 1)) <= 5) { // Play the queen and swap cards.
                cardTwelve(enemyHand, (ImageView) enemyHand.getChildAt(i));
                pTurn = true;
                playerPoints.setText(getValue(field) + "");
                enemyPoints.setText(getValue(enemyField) + "");
                playable = true;
                break;

            } else if(temp == 13 && test.get(enemyField.getChildAt(enemyField.getChildCount() - 1)) >= 8) { // play the king and repeat your previous card.
                cardThirteen(enemyHand, (ImageView) enemyHand.getChildAt(i));
                pTurn = true;
                enemyPoints.setText(getValue(enemyField) + "");
                playable = true;
                break;

            } else if(temp <= 10 && temp + getValue(enemyField) >= getValue(field)) { //out of the numeric cards, will play a card if you can beat player score.
                ImageView imageview = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, enemyField, false);

                imageview.setImageDrawable(((ImageView) enemyHand.getChildAt(i)).getDrawable());

                test.put(imageview, test.get((ImageView) enemyHand.getChildAt(i)));
                test.remove((ImageView) enemyHand.getChildAt(i));

                enemyHand.removeViewAt(i);
                enemyField.addView(imageview);
                pTurn = true;
                playable = true;

                break;
            }
        }

        /**
         * We left the loop which means there are no numerical cards that can win against the oponent's
         * value, but we might still have royal cards so...
         */

        if(!playable) {
            for(int i = 0; i < enemyHand.getChildCount(); i++) {
                int temp = test.get(enemyHand.getChildAt(i));
                if(temp == 11) {
                    cardEleven(enemyHand, (ImageView) enemyHand.getChildAt(i));
                } else if(temp == 12) {
                    cardTwelve(enemyHand, (ImageView) enemyHand.getChildAt(i));
                } else {
                    cardThirteen(enemyHand, (ImageView) enemyHand.getChildAt(i));
                }
                playable = true;
                break;
            }
        }

        /**
         * If we reach here then we have no numerical cards that can win and no royal cards to
         * manipulate anything, so it's a loss.
         */

        if(!playable) {
            Toast.makeText(this, "PLAYER WINS!", Toast.LENGTH_LONG).show();

            // resetGame();
        }

        enemyPoints.setText(getValue(enemyField) + "");
        playerPoints.setText(getValue(field) + "");

    }


    /**
     * Starts the game and adds two cards that are played for each player. This method also makes sure
     * that the player and enemy do not start off with the same value cards.
     * @param v
     */
    public void setGameStart(View v) {

        if(isClickable) {
            isClickable = false;
            try {
                ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.start));
                ImageView imageview = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, field, false);
                ImageView imageview1 = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, enemyField, false);

                Random r = new Random();
                int x = r.nextInt(cards.size());
                int y = r.nextInt(cards.size());
                imageview.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("playingCards/" + cards.get(x))));
                imageview1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("playingCards/" + cards.get(y))));

                int j = 0;
                String temp = "";
                while (Character.isDigit(cards.get(x).charAt(j))) {
                    temp += cards.get(x).charAt(j);
                    j++;
                }
                int t = Integer.parseInt(temp);
                if (t >= 11) {   //This is to avoid adding a card that won't actually be used for its value.
                    t %= 10;
                    String nCard = t + "c.png";
                    imageview.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("playingCards/" + nCard)));
                }
                test.put(imageview, t);

                j = 0;
                temp = "";
                while (Character.isDigit(cards.get(y).charAt(j))) {
                    temp += cards.get(y).charAt(j);
                    j++;
                }
                t = Integer.parseInt(temp);
                if (t >= 11) {
                    t %= 10;
                    String nCard = t + "c.png";
                    imageview1.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("playingCards/" + nCard)));
                }
                test.put(imageview1, t);

                field.addView(imageview);
                enemyField.addView(imageview1);

                if (getValue(field) == getValue(enemyField)) { // they can't have the same starting value.
                    Toast.makeText(this, "Same value", Toast.LENGTH_LONG).show();
                    field.removeAllViews();
                    enemyField.removeAllViews();
                    test.remove(imageview);
                    test.remove(imageview1);
                    setGameStart(v);

                } else if (getValue(field) > getValue(enemyField)) {
                    updateGame(test.get(imageview));
                    gameStart = true;
                } else {
                    gameStart = true; // allows the player to go.
                }
            } catch (IOException e) {
                Toast.makeText(this, "Assets not found. Game failed to start!", Toast.LENGTH_LONG).show();
            }
        }

    }

    public int getValue(GridLayout g) {
        int value = 0;
        for(int i = 0; i < g.getChildCount(); i++) {
            int x = test.get(g.getChildAt(i));
            if(x <= 10)
                value += test.get(g.getChildAt(i));
            else
                value += test.get(g.getChildAt(i)) % 10;
        }

        return value;
    }

    /**
     * This method removes the card from the hand of the person who used it, g is that person's hand, and
     * then removes the most recently played card of the other guy.
     * @param g the gridlayout that corresponds to the hand that played this card
     * @param v the actual card so that I can easily remove it from g.
     */
    public void cardEleven(GridLayout g, ImageView v) {
        g.removeView(v);
        if(g.equals(enemyHand))
            field.removeView(field.getChildAt(field.getChildCount() - 1));
        else
            enemyField.removeView(enemyField.getChildAt(enemyField.getChildCount() - 1));
    }

    /**
     * This method swaps the most recently played card from both fields.
     * @param g the hand that played the card
     * @param v the card that was played
     */
    public void cardTwelve(GridLayout g, ImageView v) { //causes error. getValue points to a null object apparently.
        g.removeView(v);

        ImageView temp1 = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, enemyField, false);
        ImageView temp2 = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, field, false);

        //The imageviews to add
        test.put(temp1, test.get(field.getChildAt(field.getChildCount() - 1)));
        test.put(temp2, test.get(enemyField.getChildAt(field.getChildCount() - 1)));

        temp1.setImageDrawable(((ImageView) field.getChildAt(field.getChildCount() - 1)).getDrawable());
        temp2.setImageDrawable(((ImageView) enemyField.getChildAt(enemyField.getChildCount() - 1)).getDrawable());

        //remove the imageviews that are already there.
        field.removeView(field.getChildAt(field.getChildCount() - 1));
        enemyField.removeView(enemyField.getChildAt(enemyField.getChildCount() - 1));

        test.remove(field.getChildAt(field.getChildCount() - 1));
        test.remove(enemyField.getChildAt(field.getChildCount() - 1));

        //add the views
        field.addView(temp2);
        enemyField.addView(temp1);
    }

    public void cardThirteen(GridLayout g, ImageView v) {
        g.removeView(v);

        String tempCard;
        if(g.equals(enemyHand))
            tempCard = test.get(enemyField.getChildAt(enemyField.getChildCount() - 1)) + "c.png";
        else
            tempCard = test.get(field.getChildAt(field.getChildCount() - 1)) + "c.png";

        ImageView imageview = (ImageView) getLayoutInflater().inflate(R.layout.image_layout, g, false);

        try {
            imageview.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("playingCards/" + tempCard)));
        } catch(IOException e) {
            Toast.makeText(this, "King method failed!", Toast.LENGTH_LONG).show();
        }

        if(g.equals(enemyHand))
            enemyField.addView(imageview);
        else
            field.addView(imageview);

        test.put(imageview, test.get(enemyField.getChildAt(enemyField.getChildCount() - 1)));
        test.remove(v);
    }

    /**
     * Consider using the TextViews to keep track of the points for the fields instead of having to
     * call the getValue method every time to find out.
     *
     * Make a smarter AI that plays low numerical cards if possible and uses 11 or 12 to get rid of
     * opponent's high numerical cards. Also, saves lowest card if 13 exists in hand so that it can
     * be used towards the end.
     *
     * Create the code that actually causes J(11), Q(12), and K(13) to do the things that they are
     * supposed to do, and put these in separate methods so that we might be able to call a method
     * regardless of who is using the card.
     */
}