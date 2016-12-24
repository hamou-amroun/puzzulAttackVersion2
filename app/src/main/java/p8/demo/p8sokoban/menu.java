package p8.demo.p8sokoban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;


public class menu  extends Activity {

    public Button play;
    public Button about;
    public Button instructions;
    public Button exit;
    public RadioGroup sound;
    public RadioButton sound_on;
    private static final int dialog_instructions  = 0;
    private static final int dialog_about         = 1;
    public  AlertDialog.Builder dialogBuilder;
    private boolean stopmusic=true;

    MediaPlayer music;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case dialog_instructions:
                // Create out AlterDialog
                dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Faire en sorte d'aligner 3 briques ou plus afin qu'elles disparaissent.\nVous avez un nombre limité de coups à jouer ");
                dialogBuilder.setCancelable(true);
                //dialogBuilder.setPositiveButton("OUI", new OkOnClickListener());
                dialogBuilder.setNegativeButton("Cancel", new CancelOnClickListener());
                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
                break;
            case dialog_about :
                // Create out AlterDialog
                dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setMessage("Puzzle Attack \nDeveloped by :\nAKKOU ALI \nAMROUN HAMOU");
                dialogBuilder.setCancelable(true);
                //dialogBuilder.setPositiveButton("OUI", new OkOnClickListener());
                dialogBuilder.setNegativeButton("Cancel", new CancelOnClickListener());
                AlertDialog dialog1 = dialogBuilder.create();
                dialog1.show();
                break;
        }
        return super.onCreateDialog(id);
    }

    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {

        }
    }


    /****************************************************/


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // initialise notre activity avec le constructeur parent
        super.onCreate(savedInstanceState);

        // charge le fichier menu.xml comme vue de l'activité
        setContentView(R.layout.menu);

        // recuperation de la vue une voie cree � partir de son id
        play         = (Button) findViewById(R.id.play);
        about        = (Button) findViewById(R.id.about);
        instructions = (Button) findViewById(R.id.instructions);
        sound        = (RadioGroup) findViewById(R.id.sound);
        exit         = (Button) findViewById(R.id.exit);
        sound_on     = (RadioButton)findViewById(R.id.sound_on);

        /****************************************************/
        final Intent puzzle=new Intent(this,p8_Sokoban.class);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopmusic=false;
                startActivity(puzzle);

            }
        });

        /*****************************************************/
        music= MediaPlayer.create(getBaseContext(),R.raw.son);
        music.start();


        /**************************************************/
        sound.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.sound_on :
                        music.start();
                        break;
                    case R.id.sound_off :
                        music.pause();

                        break;

                }
            }
        });
        /**********************************************/
        instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(dialog_instructions);
            }
        });
        /****************************************************/
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(dialog_about);
            }
        });
        /**************************************************/
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.pause();
                finish();
            }
        });
        /**************************************************/
    }


    /***************************************************/
    public void onResume(Bundle savedInstanceState){
        super.onResume();
    }
    public void onStop(){
        super.onStop();
        if (stopmusic) {
            music.pause();
        }
    }
    public void onRestart(){
        super.onRestart();
        if (sound_on.isChecked()){
            music.start();
        }else{
            music.pause();
        }
    }


}