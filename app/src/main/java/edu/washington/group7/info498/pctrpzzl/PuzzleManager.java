
/**
 * Created by Marcus_2 on 3/4/2015.
 */

package edu.washington.group7.info498.pctrpzzl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PuzzleManager {
    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_UP = 2;
    public static final int DIRECTION_DOWN = 3;

    public static final int[] DIRECTION_X = {-1, 0, +1, 0};
    public static final int[] DIRECTION_Y = {0, -1, 0, +1};

    private int[] tiles;
    private int emptySpace;

    private Random random = new Random();

    // start with a 4x4 puzzle for simplicity sake
    private int width;
    private int height;

    // initialize the manager with a 4x4 grid
    public void initManager() {
        // start with 4x4 for simplicity sake
        this.width = 4;
        this.height = 4;

        // array because lazy for now
        tiles = new int[this.width * this.height];

        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = i;
        }
        emptySpace = tiles.length - 1;
    }

    // set the tiles with given tiles, set empty space appropriately
    public void setTiles(int[] tiles) {
        this.tiles = tiles;
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == tiles.length - 1) {
                emptySpace = i;
                break;
            }
        }
    }

    public int[] getTiles() {
        return tiles;
    }

    public int getColumn(int tile) {
        return tile % width;
    }

    public int getRow(int tile) {
        return tile / width;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDistance() {
        int dist = 0;

        for(int i = 0; i < tiles.length; i++) {
            dist += Math.abs(i - tiles[i]);
        }
        return dist;
    }

    public int getMoves() {
        int x = getColumn(emptySpace);
        int y = getRow(emptySpace);

        boolean left = x > 0;
        boolean right = x < width - 1;
        boolean up = y > 0;
        boolean down = y < height - 1;

        // bitwise OR and shift so that we can recover which directions are possible
        // based on the bitwise AND
        return (left ? 1 : 0) |
                (right ? 1 << DIRECTION_RIGHT : 0) |
                (up ? 1 << DIRECTION_UP : 0) |
                (down ? 1 << DIRECTION_DOWN : 0);
    }

    // add and pick a random move, excluding the empty space
    // bitwise AND to ensure unique moves
    private int pickRandomMove(int empty) {
        List<Integer> moves = new ArrayList<Integer>(4);
        int possibleMoves = getMoves() & ~empty;

        if((possibleMoves & 1) > 0) {
            moves.add(DIRECTION_LEFT);
        }

        if((possibleMoves & (1 << DIRECTION_UP)) > 0) {
            moves.add(DIRECTION_UP);
        }

        if((possibleMoves & (1 << DIRECTION_RIGHT)) > 0) {
            moves.add(DIRECTION_RIGHT);
        }

        if((possibleMoves & (1 << DIRECTION_DOWN)) > 0) {
            moves.add(DIRECTION_DOWN);
        }

        return moves.get(random.nextInt(moves.size()));
    }
}
