
/**
 * Created by Marcus_2 on 3/4/2015.
 */

package edu.washington.group7.info498.pctrpzzl;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PuzzleManager {

    private int emptyId;
    private int[] gameboard;
    private static PuzzleManager instance = null;
    private int difficulty = 4; //default to difficulty of four, or medium

    // make this different difficult levels later on?
    private PuzzleManager() {
        emptyId = 15;
        gameboard = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    }

    // singleton
    public static PuzzleManager getInstance() {
        if (instance == null) {
            instance = new PuzzleManager();
        }
        return instance;
    }

    // set the ID when we need to later on
    public void setEmptyId(int newId) {
        emptyId = newId;
    }
    public int getEmptyId() {
        return emptyId;
    }
    public int[] getGameboard() {
        return gameboard;
    }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public int getDifficulty() { return difficulty; }
    public void setGameboard(int difficulty) {
        int[] newGameboard = new int[difficulty * difficulty];
        for (int i = 0; i < (difficulty * difficulty); i++) {
            newGameboard[i] = i;
        }
        gameboard = newGameboard;
    }

    public void swap(int index, int empty) {
        //Log.d("SWAP!", "" + Arrays.toString(gameboard));
        int temp = gameboard[index];
        gameboard[index] = gameboard[empty];
        gameboard[empty] = temp;
        //Log.d("SWAP!", "" + Arrays.toString(gameboard));
    }

    public void resetGameboard() {
        setGameboard(difficulty);
    }
    public boolean hasWon() {
        Log.d("GAMEBOARD HAS WON", Arrays.toString(gameboard));
        for (int i = 0; i < gameboard.length; i++) {
            if (gameboard[i] != i) {
                Log.d("Loser", "Still no winner");
                return false;
            }
        }
        Log.d("Winner", "Still no winner");
        return true;
    }
}
