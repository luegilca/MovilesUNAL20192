package co.edu.unal.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tiedGames = 0;
        humanGames = 0;
        computerGames = 0;

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mTiesTextView = (TextView) findViewById(R.id.ties);
        mComputerTextView = (TextView) findViewById(R.id.computer);
        mHumanTextView = (TextView) findViewById(R.id.human);

        mGame = new TicTacToeGame();
        mRand = new Random();

        startNewGame();

    }

    private void startNewGame() {
        mGame.clearBoard();
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        mGameOver = false;
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

    private class ButtonClickListener implements View.OnClickListener {
        int location;

        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);

                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }

                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mInfoTextView.setText(R.string.result_tie);
                    tiedGames += 1;
                    mGameOver = true;
                }
                else if (winner == 2) {
                    mInfoTextView.setText(R.string.result_human_wins);
                    humanGames += 1;
                    mGameOver = true;
                }
                else {
                    mInfoTextView.setText(R.string.result_computer_wins);
                    computerGames += 1;
                    mGameOver = true;
                }
                refreshLabels();
            }
        }
    }

    private void refreshLabels() {
        mTiesTextView.setText(getString(R.string.ties_score, tiedGames));
        mComputerTextView.setText(getString(R.string.computer_score, computerGames));
        mHumanTextView.setText(getString(R.string.human_score, humanGames));
    }

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }

}
