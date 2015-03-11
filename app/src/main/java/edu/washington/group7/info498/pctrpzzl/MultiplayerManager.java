package edu.washington.group7.info498.pctrpzzl;

/**
 * Created by Marcus_2 on 3/10/2015.
 */
public class MultiplayerManager {

    private MultiplayerManager instance = null;
    private boolean playerOne;
    private boolean playerTwo;

    public MultiplayerManager() {
        playerOne = false;
        playerTwo = false;
    }

    public MultiplayerManager getInstance() {
        if (this.instance == null) {
            this.instance = new MultiplayerManager();
        }
        return this.instance;
    }

    public boolean isPlayerOneReady() { return playerOne; }
    public boolean isPlayerTwoReady() { return playerTwo; }
    public void setPlayerOneReady(boolean ready) { playerOne = ready; }
    public void setPlayerTwoReady(boolean ready) { playerTwo = ready; }


}
