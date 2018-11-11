package com.sttt;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class Board extends View {

    private Game game; // reference variable to game activity
    private Handler h; // handler for inserting delay
    private Paint bg, grid, markers1, markers2, textl, texts; // colors of fields. lines and texts
    private Rect b1 = new Rect(), b2 = new Rect(), b3 = new Rect(), b4 = new Rect(),
            b5 = new Rect(), b6 = new Rect(), b7 = new Rect(), b8 = new Rect(), b9 = new Rect(); // buttons of the game
    private Rect[] rect = {b1, b2, b3, b4, b5, b6, b7, b8, b9}; // rect array made for easy access to searched value
    private Rect name1 = new Rect(), name2 = new Rect(), points1 = new Rect(), points2 = new Rect(),
            move = new Rect(), rounds = new Rect(); // fields for information in the game
    private Random r1 = new Random(); // variable to generate CPU moves if any options are not available
    private boolean ch, // "ch" - true if one method of AI is called (stop calling next methods of AI)
            over, // "over" - true if the round is over (avoid call method of AI)
            process, // "process" - true if CPU is moving (block possibility to move by player)
            start = true, // to know who starts every round
            myMove = true; // move for first player or move for human player
    private float mx1, my1; // center of text in buttons for X and Y
    private int width, height, // dimensions of phone screen
            rest, w, // "rest" difference between height and width, "w" dimension of one button
            r, rr, x, y; // round limit, random number, coor x, coor y
    private int res1 = 0, res2 = 0, draws = 0, line = 0, mov = 0; // result points, line to show winning fields and number of CPU moves
    private int side = 3, delay = 1000; // side of board and break between moves in a single game
    private int fields[][] = new int[side][side]; // an array of values for game board
    private String symbol1, symbol2, p1, p2, rg; // markers, players' names and round limit as a string

    public Board(Context context) {
        super(context);

        // read settings
        basic();

        // obtaining dimensions of the screen
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y + getBar1() - getBar2();
        w = width / 3;
        rest = height - width;

        // setting positions of rectangles
        move.set(0, 0, width, (int) (rest * 0.3f));
        b1.set(0, (int) (rest * 0.3f), w, w + (int) (rest * 0.3f));
        b2.set(w, (int) (rest * 0.3f), w * 2, w + (int) (rest * 0.3f));
        b3.set(w * 2, (int) (rest * 0.3f), width, w + (int) (rest * 0.3f));
        b4.set(0, w + (int) (rest * 0.3f), w, w * 2 + (int) (rest * 0.3f));
        b5.set(w, w + (int) (rest * 0.3f), w * 2, w * 2 + (int) (rest * 0.3f));
        b6.set(w * 2, w + (int) (rest * 0.3f), width, w * 2 + (int) (rest * 0.3f));
        b7.set(0, w * 2 + (int) (rest * 0.3f), w, width + (int) (rest * 0.3f));
        b8.set(w, w * 2 + (int) (rest * 0.3f), w * 2, width + (int) (rest * 0.3f));
        b9.set(w * 2, w * 2 + (int) (rest * 0.3f), width, width + (int) (rest * 0.3f));
        name1.set(0, width + (int) (rest * 0.3f), width / 2, width + (int) (rest * 0.6f));
        name2.set(width / 2, width + (int) (rest * 0.3f), width, width + (int) (rest * 0.6f));
        points1.set(0, width + (int) (rest * 0.6f), width / 2, width + (int) (rest * 0.8f));
        points2.set(width / 2, width + (int) (rest * 0.6f), width, width + (int) (rest * 0.8f));
        rounds.set(0, width + (int) (rest * 0.8f), width, height);

        bg = new Paint();
        bg.setColor(Color.WHITE);

        grid = new Paint();
        grid.setColor(Color.BLACK);

        markers1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        markers1.setColor(Color.BLUE);
        markers1.setStrokeWidth(10.0f);

        markers2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        markers2.setColor(Color.RED);

        textl = new Paint();
        textl.setTextSize((int) (rest * 0.3f) * 0.6f);

        texts = new Paint();
        texts.setTextSize((int) (rest * 0.2f) * 0.6f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        String who;

        canvas.drawRect(0, 0, getWidth(), getWidth(), bg);

        mark(markers1);
        mark(markers2);
        marktext(markers1);
        marktext(markers2);

        text(textl);
        text(texts);

        Paint.FontMetrics tx1 = textl.getFontMetrics();
        float ft1 = Math.abs(Math.abs(tx1.descent) - Math.abs(tx1.ascent));

        Paint.FontMetrics tx2 = texts.getFontMetrics();
        float ft2 = Math.abs(Math.abs(tx2.descent) - Math.abs(tx2.ascent));

        for (Rect bn : rect) canvas.drawRect(bn, bg);
        canvas.drawRect(move, grid);
        canvas.drawRect(name1, markers1);
        canvas.drawRect(name2, markers2);
        canvas.drawRect(points1, grid);
        canvas.drawRect(points2, grid);
        canvas.drawRect(rounds, grid);

        // horizontal lines
        canvas.drawLine(0, w + (int) (rest * 0.3f), width, w + (int) (rest * 0.3f), grid);
        canvas.drawLine(0, w * 2 + (int) (rest * 0.3f), width, w * 2 + (int) (rest * 0.3f), grid);

        // vertical lines
        canvas.drawLine(w, (int) (rest * 0.3f), w, width + (int) (rest * 0.3f), grid);
        canvas.drawLine(w * 2, (int) (rest * 0.3f), w * 2, width + (int) (rest * 0.3f), grid);

        // check board array then draw symbols of found values
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                if (fields[i][j] == 1) {
                    canvas.drawText(symbol1, mx1 * (i * 2 + 1), (int) (rest * 0.3f) + my1 + j * w, markers1);
                } else if (fields[i][j] == 2) {
                    canvas.drawText(symbol2, mx1 * (i * 2 + 1), (int) (rest * 0.3f) + my1 + j * w, markers2);
                }
            }
        }

        // show who moves
        if (myMove) {
            who = p1;
        } else {
            who = p2;
        }

        canvas.drawText(who + "'s " + getResources().getString(R.string.move), width / 2, ft1 + ((int) (rest * 0.3f) - ft1) / 2, textl); // move
        canvas.drawText(p1, width / 4, width + (int) (rest * 0.6f) - ((int) (rest * 0.3f) - ft1) / 2, textl); //player1
        canvas.drawText(p2, width / 4 * 3, width + (int) (rest * 0.6f) - ((int) (rest * 0.3f) - ft1) / 2, textl); //player2
        canvas.drawText(String.valueOf(res1), width / 4, width + (int) (rest * 0.8f) - ((int) (rest * 0.2f) - ft2) / 2, texts); //0
        canvas.drawText(String.valueOf(res2), width / 4 * 3, width + (int) (rest * 0.8f) - ((int) (rest * 0.2f) - ft2) / 2, texts); //0
        canvas.drawText(String.valueOf(res1 + res2 + draws) + " / " + rg, width / 2, height - ((int) (rest * 0.2f) - ft2) / 2, texts); // rounds

        // mark the winning fields
        switch (line) {
            case 1:
                canvas.drawLine(0, w / 2 + (int) (rest * 0.3f), width, w / 2 + (int) (rest * 0.3f), markers1);
                break;
            case 2:
                canvas.drawLine(0, w / 2 + w + (int) (rest * 0.3f), width, w / 2 + w + (int) (rest * 0.3f), markers1);
                break;
            case 3:
                canvas.drawLine(0, w / 2 + w * 2 + (int) (rest * 0.3f), width, w / 2 + w * 2 + (int) (rest * 0.3f), markers1);
                break;
            case 4:
                canvas.drawLine(w / 2, (int) (rest * 0.3f), w / 2, (int) (rest * 0.3f) + width, markers1);
                break;
            case 5:
                canvas.drawLine(w + w / 2, (int) (rest * 0.3f), w + w / 2, (int) (rest * 0.3f) + width, markers1);
                break;
            case 6:
                canvas.drawLine(w * 2 + w / 2, (int) (rest * 0.3f), w * 2 + w / 2, (int) (rest * 0.3f) + width, markers1);
                break;
            case 7:
                canvas.drawLine(0, (int) (rest * 0.3f), width, width + (int) (rest * 0.3f), markers1);
                break;
            case 8:
                canvas.drawLine(width, (int) (rest * 0.3f), 0, width + (int) (rest * 0.3f), markers1);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!process) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (int i = 0; i < rect.length; i++) {
                        if (rect[i].contains(x, y)) {
                            int u1 = (i + side) % side;
                            int u2 = i / side;
                            if (Game.players == 1 && fields[u1][u2] == 0) {
                                fields[u1][u2] = 1;
                                myMove = !myMove;
                            } else if (Game.players == 2) {
                                if (myMove && fields[u1][u2] == 0) {
                                    fields[u1][u2] = 1;
                                    myMove = false;
                                } else if (!myMove && fields[u1][u2] == 0) {
                                    fields[u1][u2] = 2;
                                    myMove = true;
                                }
                            }
                            win();
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (Game.players == 1 && !over && !myMove) {
                        process = true;
                        h.postDelayed(new Runnable() {
                            public void run() {
                                cpu();
                                win();
                                invalidate();
                                myMove = true;
                                process = false;
                                over = false;
                            }
                        }, delay);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
            }
        }
        return true;
    }

    private void basic() {
        SharedPreferences sp = getContext().getSharedPreferences(Settings.PREFS_NAME, Context.MODE_PRIVATE);
        int t, m, g;

        t = sp.getInt("turn", Settings.turn);
        m = sp.getInt("marker", Settings.marker);
        g = sp.getInt("games", Settings.games);
        p1 = sp.getString("player1", Settings.sn1);
        p2 = sp.getString("player2", Settings.sn2);

        if (g == 4) {
            r = -1;
            rg = "∞";
        } else if (g == 1){
            r = 3;
            rg = String.valueOf(r);
        } else {
            r = g * g + 1;
            rg = String.valueOf(r);
        }

        if (m == 0) {
            symbol1 = "X";
            symbol2 = "O";
        } else {
            symbol1 = "O";
            symbol2 = "X";
        }

        if (Game.players == 1) {
            p2 = "CPU";
            h = new Handler();
            if (t == 1) {
                myMove = false;
                start = false;
                process = true;
                h.postDelayed(new Runnable() {
                    public void run() {
                        cpu();
                        invalidate();
                        myMove = !myMove;
                        process = false;
                    }
                }, delay);
            }
        }
    }

    private void mark(Paint p) {
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(width * 0.3f);
        p.setTextAlign(Paint.Align.CENTER);
    }

    private void marktext(Paint p) {
        Paint.FontMetrics fmxo = p.getFontMetrics();
        float fm = Math.abs(Math.abs(fmxo.descent) - Math.abs(fmxo.ascent));
        mx1 = w / 2;
        my1 = w - (w - fm) / 2;
    }

    private void text(Paint p) {
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);
    }

    private int getBar1() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) result = getResources().getDimensionPixelSize(resourceId);

        return result;
    }

    private int getBar2() {
        boolean hasMenuKey = ViewConfiguration.get(getContext()).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) return getResources().getDimensionPixelSize(resourceId);

        return 0;
    }

    // checking winning fields
    private void win() {
        sum();
        int ratio;
        for (int i = 0; i < sum().length; i++) {
            ratio = sum()[i][0] * sum()[i][1] * sum()[i][2];
            if (ratio == 1 || ratio == 8) {
                wd(ratio / 8 + 1);
                line = i + 1;
            }
        }
        if (full(fields)) wd(0);
    }

    // dialog when the round is over
    private void wd(int f) {
        over = true;
        final Dialog ad = new Dialog(getContext());
        ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ad.setContentView(R.layout.result);
        TextView msg = (TextView)ad.findViewById(R.id.tv);
        ad.getWindow().setLayout((int) (width * 0.8f), (int) (width * 0.4f));
        ad.setCancelable(false);

        switch (f) {
            case 0:
                msg.setText(getResources().getString(R.string.draw));
                draws++;
                break;
            case 1:
                msg.setText(Settings.sn1 + " " + getResources().getString(R.string.win));
                res1++;
                break;
            case 2:
                msg.setText(Settings.sn2 + " " + getResources().getString(R.string.win));
                res2++;
                break;
        }
        Button next = (Button) ad.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBoard();
                if (res1 + res2 + draws == r) {
                    end(); // end of game when the rounds reach the limit
                } else if (Game.players == 1 && !myMove) {
                    process = true;
                    h.postDelayed(new Runnable() {
                        public void run() {
                            cpu();
                            invalidate();
                            process = false;
                            myMove = true;
                            over = false;
                        }
                    }, delay);
                }
                ad.dismiss();
            }
        });
        ad.show();
    }

    // dialog when the game is over
    private void end() {
        final Dialog eg = new Dialog(getContext());
        eg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        eg.setContentView(R.layout.end);
        TextView msg = (TextView)eg.findViewById(R.id.tv);
        eg.getWindow().setLayout((int) (width * 0.8f), (int) (width * 0.4f));
        eg.setCancelable(false);

        if (res1 == res2) {
            msg.setText(getResources().getString(R.string.drawx));
        } else if (res1 > res2) {
            msg.setText(getResources().getString(R.string.p1) + " " + getResources().getString(R.string.winx));
        } else if (res1 < res2) {
            msg.setText(getResources().getString(R.string.p2) + " " + getResources().getString(R.string.winx));
        }
        Button newGame = (Button) eg.findViewById(R.id.ng);
        Button gameOver = (Button) eg.findViewById(R.id.go);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearBoard();
                res1 = 0;
                res2 = 0;
                draws = 0;
                eg.dismiss();
            }
        });

        gameOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = (Game) getContext();
                game.finish();
            }
        });
        eg.show();
    }

    // true if board is full
    private boolean full(int[][] array) {
        for (int[] z : array) {
            for (int zx : z) {
                if (zx == 0) return false;
            }
        }
        return true;
    }

    // true if board is empty
    private boolean zero(int[][] array) {
        for (int[] z : array) {
            for (int zx : z) {
                if (zx != 0) return false;
            }
        }
        return true;
    }

    // clear the board
    private void clearBoard() {
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                fields[i][j] = 0;
            }
        }
        line = 0;
        mov = 0;
        start = !start;
        myMove = start;
        invalidate();
    }

    // possible winning lines
    private int[][] sum() {
        int[][] all;
        int[] f1 = new int[]{fields[0][0], fields[1][0], fields[2][0]};
        int[] f2 = new int[]{fields[0][1], fields[1][1], fields[2][1]};
        int[] f3 = new int[]{fields[0][2], fields[1][2], fields[2][2]};
        int[] f4 = new int[]{fields[0][0], fields[0][1], fields[0][2]};
        int[] f5 = new int[]{fields[1][0], fields[1][1], fields[1][2]};
        int[] f6 = new int[]{fields[2][0], fields[2][1], fields[2][2]};
        int[] f7 = new int[]{fields[0][0], fields[1][1], fields[2][2]};
        int[] f8 = new int[]{fields[2][0], fields[1][1], fields[0][2]};
        all = new int[][]{f1, f2, f3, f4, f5, f6, f7, f8};

        return all;
    }

    // checking lines for attack and defence
    private void lines(int[][] a, int[] ad) {
        if (!ch) {
            for (int i = 0; i < sum().length; i++) {
                for (int j = 0; j < side; j++) {
                    if (a[i][0] * 100 + a[i][1] * 10 + a[i][2] == ad[j]) {
                        ch = true;
                        switch (i) {
                            case 0:
                                fields[j][i] = 2;
                                return;
                            case 1:
                                fields[j][i] = 2;
                                return;
                            case 2:
                                fields[j][i] = 2;
                                return;
                            case 3:
                                fields[i - 3][j] = 2;
                                return;
                            case 4:
                                fields[i - 3][j] = 2;
                                return;
                            case 5:
                                fields[i - 3][j] = 2;
                                return;
                            case 6:
                                fields[j][j] = 2;
                                return;
                            case 7:
                                fields[i - j - 5][j] = 2;
                                return;
                        }
                    }
                }
            }
        }
    }

    // checking possible best ways to move
    private int nexts(int i) {
        int[] n = new int[0];

        switch (i) {
            case 0:
                n = new int[]{i, i + 1};
                break;
            case 1:
                n = new int[]{i - 1, i, i + 1};
                break;
            case 2:
                n = new int[]{i - 1, i};
                break;
        }
        return r1.nextInt(n.length);
    }

    // choose move from possible solutions from method "nexts"
    private int[] ins (int f) {
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                if (fields[i][j] == f) {
                    rr = r1.nextInt(2);
                    switch (rr) {
                        case 0:
                            x = nexts(i);
                            y = j;
                            break;
                        case 1:
                            x = i;
                            y = nexts(j);
                            break;
                    }
                }
            }
        }
        return new int[] {x, y};
    }

    // CPU moves
    private void cpu() {
        ch = false;
        sum();
        int[] ata = {22, 202, 220};
        int[] def = {11, 101, 110};
        int diag = 121;
        int[] fxy;

        if (mov == 0) {
            if (zero(fields)) {
                x = r1.nextInt(2) * 2;
                y = r1.nextInt(2) * 2;
            } else {
                fxy = ins(1);
                x = fxy[0];
                y = fxy[1];
                while (fields[x][y] != 0) {
                    fxy = ins(1);
                    x = fxy[0];
                    y = fxy[1];
                    if(fields[x][y] == 0) {
                        fields[x][y] = 2;
                        return;
                    }
                }
            }
            fields[x][y] = 2;
        } else if (mov == 1) {
            lines(sum(), ata);
            lines(sum(), def);

            if (!ch) {
                if (fields[1][1] == 0) {
                    x = 1;
                    y = 1;
                } else if (fields[0][0] * 100 + fields[1][1] * 10 + fields[2][2] == diag ||
                        fields[2][0] * 100 + fields[1][1] * 10 + fields[0][2] == diag) {
                    int q = r1.nextInt(2);
                    int p = r1.nextInt(2);
                    switch (q) {
                        case 0:
                            fields[p][p + 1] = 2;
                            return;
                        case 1:
                            fields[p + 1][p] = 2;
                            return;
                    }
                } else {
                    fxy = ins(2);
                    x = fxy[0];
                    y = fxy[1];
                    while (fields[x][y] != 0) {
                        fxy = ins(2);
                        x = fxy[0];
                        y = fxy[1];
                        if(fields[x][y] == 0) {
                            fields[x][y] = 2;
                            return;
                        }
                    }
                }
                fields[x][y] = 2;
            }
        } else {
            lines(sum(), ata);
            lines(sum(), def);

            if (!ch) {
                ArrayList<Integer> alx = new ArrayList<>();
                ArrayList<Integer> aly = new ArrayList<>();
                alx.clear();
                aly.clear();
                for (int i = 0; i < side; i++) {
                    for (int j = 0; j < side; j++) {
                        if (fields[i][j] == 0) {
                            alx.add(i);
                            aly.add(j);
                        }
                    }
                }
                rr = r1.nextInt(alx.size());
                fields[alx.get(rr)][aly.get(rr)] = 2;
            }
        }
        mov++;
    }
}