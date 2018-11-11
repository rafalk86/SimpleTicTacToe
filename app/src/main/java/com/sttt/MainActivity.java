package com.sttt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// A simple project of tic tac toe game
// Author: Rafał Kluziński

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button game1, game2, sets, info;

        game1 = (Button) findViewById(R.id.p1);
        game2 = (Button) findViewById(R.id.p2);
        sets = (Button) findViewById(R.id.settings);
        info = (Button) findViewById(R.id.info);

        game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame(1);
            }
        });

        game2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame(2);
            }
        });

        sets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Information.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void newGame(int p) {
        Intent i = new Intent(MainActivity.this, Game.class);
        if(p == 1) {
            i.putExtra("players", 1);
        }
        else if(p == 2) {
            i.putExtra("players", 2);
        }
        startActivity(i);
    }

    private void back() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(getString(R.string.quit));
        adb.setMessage(getString(R.string.qmes));
        adb.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        adb.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }
}
