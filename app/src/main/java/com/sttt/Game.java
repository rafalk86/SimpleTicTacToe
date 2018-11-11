package com.sttt;

import android.app.Activity;
import android.os.Bundle;

public class Game extends Activity {

    public static int players;

    @Override
    protected void onCreate(Bundle save) {
        super.onCreate(save);

        players = this.getIntent().getIntExtra("players", 1);

        Board board = new Board(this);
        setContentView(board);
    }
}
