
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
    //private Map<Bitmap, Integer> gameboard;
    private Bitmap[] gameboard;

    private static PuzzleManager instance = null;

    private PuzzleManager() {
        emptyRow = 3;
        emptyCol = 3;
        rowsCols = 4;
        emptyId = 15;
    }

    public static PuzzleManager getInstance() {
        if (instance == null) {
            instance = new PuzzleManager();
        }
        return instance;
    }

    public void setEmptyId(int newId) {
        emptyId = newId;
    }

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

    public void moveTile(int tile) {

        Log.d("PuzzleManager", "Move Tile: " + gameboard[tile].toString() + " and index: " + tile);
        Log.d("PuzzleManager", "Empty Tile: " + gameboard[emptyId].toString() + " and index: " + emptyId);

        Bitmap move = gameboard[tile];
        //Bitmap tempBit = Bitmap.createBitmap(move);
        Bitmap empty = gameboard[emptyId];

        gameboard[tile] = empty;
        gameboard[emptyId] = move;

        int temp = emptyId;
        emptyId = tile;
        tile = temp;

        Log.d("PuzzleManager", "Move Tile: " + gameboard[tile].toString() + " and index: " + tile);
        Log.d("PuzzleManager", "Empty Tile: " + gameboard[emptyId].toString() + " and index: " + emptyId);
    }
    public Bitmap find(Bitmap b) {
        for (int i = 0; i < gameboard.length; i++) {
            if (b.equals(gameboard[i])) {
                return gameboard[i];

            }
        }
        return null;
    }
}
