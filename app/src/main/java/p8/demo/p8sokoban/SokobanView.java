package p8.demo.p8sokoban;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SokobanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    // Declaration des images
    private Bitmap yellow;
    private Bitmap sky;
    private Bitmap empty;
    private Bitmap orange;
    private Bitmap win;
    private Bitmap withe;
    private Bitmap fond;
    private Bitmap chrono;
    private long beginChrono, endChrono, beginChronoDialog, endChronoDialog ;
    boolean loadNextLevel  = false;
    boolean replay         = false;
    boolean displayDialog1 = true;
    p8_Sokoban object;
    p8_Sokoban object_1;


    public double positionx = 0, positiony = 0;
    int xx = 0;
    int yy = 0;
    int   nbTouch = 0;
    Paint text = new Paint();
    int   level = 0;


    private Resources mRes;
    private Context mContext;

    int[][] carte;

    int carteTopAnchor;
    int carteLeftAnchor;

    static final int carteWidth    = 6;
    static final int carteHeight   = 7;
    static final int sizeCST = 53;

    static final int CST_empty  = 0;
    static final int CST_yellow = 1;
    static final int CST_sky    = 2;
    static final int CST_orange = 3;


    int[][][] ref = {
            {
                    {CST_empty, CST_empty, CST_empty, CST_empty, CST_empty, CST_empty},
                    {CST_empty, CST_empty, CST_empty, CST_empty, CST_empty, CST_empty},
                    {CST_empty, CST_empty, CST_sky, CST_empty, CST_empty, CST_empty},
                    {CST_empty, CST_yellow, CST_orange, CST_sky, CST_empty, CST_empty},
                    {CST_empty, CST_yellow, CST_orange, CST_yellow, CST_empty, CST_empty},
                    {CST_empty, CST_orange, CST_yellow, CST_orange, CST_yellow, CST_empty},
                    {CST_empty, CST_yellow, CST_orange, CST_sky, CST_sky, CST_empty}
            }, {
            {CST_empty, CST_empty, CST_empty, CST_empty, CST_empty, CST_empty},
            {CST_empty, CST_empty, CST_empty, CST_empty, CST_empty, CST_empty},
            {CST_empty, CST_sky, CST_orange, CST_orange, CST_empty, CST_empty},
            {CST_empty, CST_orange, CST_yellow, CST_orange, CST_empty, CST_empty},
            {CST_empty, CST_yellow, CST_orange, CST_sky, CST_empty, CST_empty},
            {CST_empty, CST_yellow, CST_sky, CST_orange, CST_sky, CST_empty},
            {CST_yellow, CST_orange, CST_sky, CST_orange, CST_yellow, CST_yellow}
    }, {
            {CST_empty, CST_empty, CST_empty, CST_empty, CST_empty, CST_empty},
            {CST_empty, CST_empty, CST_empty, CST_empty, CST_empty, CST_empty},
            {CST_empty, CST_empty, CST_orange, CST_empty, CST_empty, CST_empty},
            {CST_empty, CST_empty, CST_sky, CST_empty, CST_orange, CST_empty},
            {CST_empty, CST_empty, CST_sky, CST_sky, CST_orange, CST_empty},
            {CST_empty, CST_yellow, CST_yellow, CST_sky, CST_yellow, CST_empty},
            {CST_empty, CST_orange, CST_sky, CST_orange, CST_orange, CST_empty}
    }};


    private boolean in = true;
    private Thread cv_thread;
    SurfaceHolder holder;


    public SokobanView(Context context, AttributeSet attrs) {

        super(context, attrs);
        object   = new p8_Sokoban();
        object_1 = new p8_Sokoban();

        holder = getHolder();
        holder.addCallback(this);

        mContext = context;
        mRes     = mContext.getResources();

        yellow = BitmapFactory.decodeResource(mRes, R.drawable.yellow);
        sky    = BitmapFactory.decodeResource(mRes, R.drawable.sky);
        orange = BitmapFactory.decodeResource(mRes, R.drawable.orange);
        empty  = BitmapFactory.decodeResource(mRes, R.drawable.empty);
        win    = BitmapFactory.decodeResource(mRes, R.drawable.win);
        chrono = BitmapFactory.decodeResource(mRes, R.drawable.chrono);
        withe  = BitmapFactory.decodeResource(mRes, R.drawable.withe);
        fond   = BitmapFactory.decodeResource(mRes, R.drawable.fond);

        // creation of the thread
        cv_thread = new Thread(this);

        setFocusable(true);
    }


    public void startChronoDialog(){
        beginChronoDialog = System.currentTimeMillis();
    }

    public void stopChronoDialog(){
        endChronoDialog = System.currentTimeMillis();
    }

    public double getChronoDialog() {
        return ((endChronoDialog - beginChronoDialog) / 1000);
    }



    public void startChrono(){
        beginChrono = System.currentTimeMillis();
    }

    public void stopChrono(){
        endChrono = System.currentTimeMillis();
    }

    public double getChrono() {
        return ((endChrono - beginChrono) / 1000);
    }




    // load a level from our table
    private void loadlevel()
    {
        for (int i = 0; i < carteWidth; i++)
        {
            for (int j = 0; j < carteHeight; j++)
            {
                carte[j][i] = ref[level][j][i];
            }
        }
        startChrono();
    }

    // initialisation du jeu
    public void initparameters() {
        carte = new int[carteHeight][carteWidth];
        loadlevel();
        carteTopAnchor = 150;
        carteLeftAnchor = (getWidth()) / carteWidth;

        if ((cv_thread != null) && (!cv_thread.isAlive()))
        {
            cv_thread.start();
            Log.e("-TEST-", "cv_thread.start()");
        }
    }


    // dessin du gagne si gagne
    private void paintWin(Canvas canvas) {
        canvas.drawBitmap(win, carteLeftAnchor + 30, carteTopAnchor + 20, null);
    }
    private void paintChrono(Canvas canvas) {
        canvas.drawBitmap(chrono, getWidth()-40, 10 , null);
    }

    private void paintWithe(Canvas canvas) {
        canvas.drawBitmap(withe, -10, -10, null);
    }


    // dessin de la carte du jeu
    private void paintcarte(Canvas canvas)
    {
        for (int i = 0; i < carteHeight; i++)
        {
            for (int j = 0; j < carteWidth; j++)
            {
                switch (carte[i][j])
                {
                    case CST_sky:
                        canvas.drawBitmap(sky, j * sizeCST, carteTopAnchor + i * sizeCST, null);
                        break;
                    case CST_empty:
                        canvas.drawBitmap(empty, j * sizeCST, carteTopAnchor + i * sizeCST, null);
                        break;
                    case CST_orange:
                        canvas.drawBitmap(orange, j * sizeCST, carteTopAnchor + i * sizeCST, null);
                        break;
                    case CST_yellow:
                        canvas.drawBitmap(yellow, j * sizeCST, carteTopAnchor + i * sizeCST, null);
                        break;
                }
            }
        }
    }

    //dessin du fond
    private void paintFond(Canvas canvas) {
        canvas.drawBitmap(fond, 1, 1, null);
    }
    private void paintMessage(Canvas canvas)
    {
        int time = (int) getChrono();
        text.setTextSize(15);
        text.setStyle(Paint.Style.FILL_AND_STROKE);
        if (nbTouch == 2)
        {
            startChronoDialog();
            if (isWon())
            {
                if(level == 2)
                { paintWithe(canvas);paintWin(canvas); }
            }
            else
            {
                object_1.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        if (displayDialog1) {
                            if (level !=2 ) {dialogReplay();}
                            displayDialog1 = false;
                        }
                    }
                });

                if (replay) {
                    nbTouch = 0;
                    loadlevel();
                    displayDialog1 = false;
                    replay = false;
                }
            }
        }


        if (nbTouch == 1)
        {
             if(!isWon())
            {
                displayDialog1 = true;
                text.setColor(Color.WHITE);
                canvas.drawText("Il vous reste 1 déplacement à faire", 12, carteTopAnchor / 5, text);
                paintChrono(canvas);
                canvas.drawText(""+ time + "", getWidth()-30, 60, text);
            }
        }

        if (nbTouch == 0)
        {
            text.setColor(Color.WHITE);
            canvas.drawText("Il vous reste 2 déplacement à faire", 12, carteTopAnchor / 5, text);
            paintChrono(canvas);
            canvas.drawText(""+ time + "", getWidth()-30, 60, text);
        }

    }

    //Identifies whether the game is won
    public boolean isWon()
    {
        boolean won = true;
        int i = 0;
        int j = 0;

        while (won & i < carteHeight)
        {
            j = 0;
            while (won & j < carteWidth)
            {
                if (carte[i][j] != CST_empty)
                {
                    won = false;
                }
                j++;
            }
            i++;
        }
        return won;
    }

    private void nDraw(Canvas canvas) {
        paintFond(canvas);
        paintMessage(canvas);

        if (isWon()) {
            object.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (displayDialog1) {
                        if(level != 2) {
                            dialogReplay();
                        }
                        displayDialog1 = false;
                    }
                }
            });

            if (loadNextLevel) {
                level++;
                loadlevel();
                paintcarte(canvas);

                nbTouch = 0;
                loadNextLevel = false;
            }
        }

        //s'il n'a pas gagné ( soit perdu - soit début du jeu )
        else {
            paintcarte(canvas);
            delet();
        }
    }

    // callback sur le cycle de vie de la surfaceview
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged " + width + " - " + height);
        initparameters();
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
    }


    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
    }


    public void run() {
        Canvas c = null;
        while (in) {
            try {
                cv_thread.sleep(300);
                stopChrono();
                stopChronoDialog();

                try {
                    c = holder.lockCanvas(null);
                    nDraw(c);
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch (Exception e) {
                Log.e("-> RUN <-", "PROBLEME DANS RUN");
            }
        }
    }




    public void delet()
    {
        boolean vert=true;
        boolean hor=true;
        do{
            hor = false;
            for (int i = 0; i < 7; i++) {
                boolean bool1 = true;
                for (int j = 0; j < 4; j++) {
                    if (carte[i][j] == carte[i][j + 1] && carte[i][j] == carte[i][j + 2] && carte[i][j] != CST_empty) {
                        hor = true;
                        for (int k = j + 3; k < 6; k++) {
                            while (bool1) {
                                bool1 = false;
                                if (carte[i][j] == carte[i][k]) {
                                    bool1 = true;
                                    carte[i][k] = CST_empty;
                                    int b = i;
                                    while (i > 0) {
                                        carte[i][k] = carte[i - 1][k];
                                        carte[i - 1][k] = CST_empty;
                                        i--;
                                    }
                                    i = b;
                                    k++;
                                }
                            }
                        }
                        carte[i][j] = CST_empty;
                        carte[i][j + 1] = CST_empty;
                        carte[i][j + 2] = CST_empty;


                        if (i > 0) {
                            int a = i;
                            while (i > 0) {
                                carte[i][j] = carte[i - 1][j];
                                carte[i - 1][j] = CST_empty;
                                i--;
                            }
                            i = a;
                            while (i > 0) {
                                carte[i][j + 1] = carte[i - 1][j + 1];
                                carte[i - 1][j + 1] = CST_empty;
                                i--;
                            }
                            i = a;
                            while (i > 0) {
                                carte[i][j + 2] = carte[i - 1][j + 2];
                                carte[i - 1][j + 2] = CST_empty;
                                i--;
                            }
                            i = a;
                        }

                    }
                }
            }


            hor = false;
            for (int i = 0; i < 7; i++) {
                boolean bool1 = true;
                for (int j = 0; j < 4; j++) {
                    if (carte[j][i] == carte[j + 1][i] && carte[j][i] == carte[j + 2][i] && carte[j][i] != CST_empty) {
                        hor = true;
                        int s = carte[j][i];
                        carte[j][i] = CST_empty;

                        int b = j;
                        while (j > 0) {
                            carte[j][i] = carte[j - 1][i];
                            carte[j - 1][i] = CST_empty;
                            j--;
                        }
                        carte[j + 1][i] = CST_empty;
                        j = b;
                        while (j > 0) {
                            carte[j + 1][i] = carte[j][i];
                            carte[j][i] = CST_empty;
                            j--;
                        }
                        carte[j + 2][i] = CST_empty;
                        j = b;
                        b = j;
                        while (j > 0) {
                            carte[j + 2][i] = carte[j + 1][i];
                            carte[j + 1][i] = CST_empty;
                            j--;
                        }
                        j = b;


                        for (int k = j + 3; k < 7; k++) {
                            while (bool1) {
                                bool1 = false;
                                if (s == carte[k][i]) {
                                    bool1 = true;
                                    carte[k][i] = CST_empty;
                                    int o = k;
                                    while (k > 0) {
                                        carte[k][i] = carte[k - 1][i];
                                        carte[k - 1][i] = CST_empty;
                                        k--;
                                    }
                                    k = o;
                                    k++;
                                }
                            }
                        }

                    }
                }
            }
        }while(hor || vert);
    }




    private void dialogReplay(){
        AlertDialog.Builder about = new AlertDialog.Builder(mContext);


    try{
        Thread.sleep(2000);
    }catch(Exception e){}

        if(isWon()) {
            about.setTitle("Congratulations");
        }
        else {
            about.setTitle("Lose :/");
        }
            TextView l_viewabout = new TextView(mContext);
            l_viewabout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            l_viewabout.setPadding(10, 10, 10, 10);
            l_viewabout.setTextSize(20);

        if(isWon()) {
            l_viewabout.setText("Voulez vous continuer ?");
        }
        else {
            l_viewabout.setText("Voulez vous rejouer ?");
        }
            l_viewabout.setMovementMethod(LinkMovementMethod.getInstance());
            about.setView(l_viewabout);


        about.setPositiveButton("oui", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isWon()) {loadNextLevel = true;}
                else        {replay = true;}

            }

        });
        about.setNegativeButton("non", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(isWon()) {
                    loadNextLevel = false; object.finish();
                }
                else        {
                    replay = false;object.finish();
                }
            }

        });
        about.show();
    }

}
