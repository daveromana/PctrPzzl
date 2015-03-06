
/**
 * Created by Marcus_2 on 3/4/2015.
 */

package edu.washington.group7.info498.pctrpzzl;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PuzzleManager {

    private int emptyRow;
    private int emptyCol;
    private int rowsCols;
    private int emptyId;
    private Bitmap[] gameboard;

    private static PuzzleManager instance = null;

    private PuzzleManager() {
        emptyRow = 3;
        emptyCol = 3;
        rowsCols = 4;
        emptyId = 15;
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

    // TODO
    public void shuffle() {
        for (int i = 0; i < 1000; i++) {
            int[] possible = new int[]{emptyId - 1, emptyId + 1, emptyId - 4, emptyId + 4}; // left, right, up, down
        }
    }
    public int getEmptyId() {
        return emptyId;
    }
    public int getEmptyRow() {
        return emptyRow;
    }

    public int getEmptyCol() {
        return emptyCol;
    }

    public int getRowsCols() {
        return rowsCols;
    }

    public Bitmap[] getGameboard() {
        return gameboard;
    }

    public void setGameboard(Bitmap[] gameboard) {
        this.gameboard = gameboard;
    }

    // may not use this
    public Bitmap find(Bitmap b) {
        for (int i = 0; i < gameboard.length; i++) {
            if (b.equals(gameboard[i])) {
                return gameboard[i];

            }
        }
        return null;
    }
}
