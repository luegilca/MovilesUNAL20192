package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class AndroidTicTacToeActivity extends AppCompatActivity {

    private TicTacToeGame mGame;
    private Button[] mBoardButtons;
    private TextView mInfoTextView;
    private TextView mTiesTextView;
    private TextView mComputerTextView;
    private TextView mHumanTextView;
    private Boolean mGameOver;
    private Random mRand;
    private Integer tiedGames;
    private Integer humanGames;
    private Integer computerGames;
    private BoardView mBoardView;
    private MediaPlayer mHumanMediaPlayer;
    private MediaPlayer mComputerMediaPlayer;
    private boolean turn;
    static final int DIALOG_QUIT_ID = 1;
    static final int ABOUT_DIALOG_ID = 2;

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch( View v, MotionEvent event ){

            // Determine which cell was touched
            int col = (int) event.getX( ) / mBoardView.getBoardCellWidth( );
            int row = (int) event.getY( ) / mBoardView.getBoardCellHeight( );
            int pos = row * 3 + col;

            if( !mGameOver && setMove( TicTacToeGame.HUMAN_PLAYER, pos ) ){

                if( !turn )
                    return false;

                // If no winner yet, let the computer make a move
                mHumanMediaPlayer.start( );

                int winner = mGame.checkForWinner( );
                if( winner == 0 ){
                    turn = false;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            mComputerMove( );
                            mBoardView.invalidate( );
                            winner();
                        }
                    }, 1000);
                }else{
                    winner();
                }
            }

            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tiedGames = 0;
        humanGames = 0;
        computerGames = 0;


        mInfoTextView = findViewById(R.id.information);
        mTiesTextView = findViewById(R.id.ties);
        mComputerTextView = findViewById(R.id.computer);
        mHumanTextView = findViewById(R.id.human);

        mGame = new TicTacToeGame();
        mBoardView = findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mRand = new Random();

        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        startNewGame();

    }

    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate();   // Redraw the board
        mGameOver = false;
        turn = true;
        boolean humanGoesFirst = mRand.nextBoolean();
        if( humanGoesFirst ) {
            mInfoTextView.setText(R.string.first_human);
        }
        else {
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.turn_human);
        }
        refreshLabels();
    }

    public void winner( ){
        int winner = mGame.checkForWinner( );
        if( winner == 0 ){
            mInfoTextView.setText( R.string.turn_human );
        }else if( winner == 1 ){
            mInfoTextView.setText( R.string.result_tie );
            mGame.setWins( 1 );
            mTiesTextView.setText( "Ties: " + mGame.getWins( 1 ).toString( ) );
            mGameOver = true;
        }else if (winner == 2) {
            mInfoTextView.setText(R.string.result_human_wins);
            mGame.setWins( 0 );
            mHumanTextView.setText( "Human: " + mGame.getWins( 0 ).toString( ) );
            mGameOver = true;
        }else {
            mInfoTextView.setText(R.string.result_computer_wins);
            mGame.setWins( 2 );
            mComputerTextView.setText( "Android: " + mGame.getWins( 2 ).toString( ) );
            mGameOver = true;
        }
    }

    public int mComputerMove( ){
        mInfoTextView.setText( R.string.turn_computer );
        int move = mGame.getComputerMove( );
        setMove( TicTacToeGame.COMPUTER_PLAYER, move );
        mComputerMediaPlayer.start( );
        turn = true;
        return mGame.checkForWinner( );
    }


    private void refreshLabels() {
        mTiesTextView.setText(getString(R.string.ties_score, tiedGames));
        mComputerTextView.setText(getString(R.string.computer_score, computerGames));
        mHumanTextView.setText(getString(R.string.human_score, humanGames));
    }

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate();   // Redraw the board
            return true;
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings

            mSoundOn = mPrefs.getBoolean("sound", true);

            String difficultyLevel = mPrefs.getString("difficulty_level",
                    getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }
}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.settings:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.about_dialog:
                showDialog(ABOUT_DIALOG_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        switch(id) {
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog

                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AndroidTicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();

                break;

            case ABOUT_DIALOG_ID:
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
        }


        return dialog;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.human);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.android);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }


}
