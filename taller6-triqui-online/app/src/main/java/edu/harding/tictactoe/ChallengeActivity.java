package edu.harding.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChallengeActivity extends AppCompatActivity {

    public final static String HOST = " https://tictac-moviles2019.herokuapp.com/online/";
    public final static String GAMES = "games";
    public final static String NEW = "new/";
    public final static String JOIN = "join/";
    public final static String WAIT = "wait/";
    public final static String PLAY = "play/";
    public final static String DELETE = "delete/";

    private ArrayAdapter<String> adapter;
    private ListView listView;
    public static String game;
    public static String idGamer;
    public static String idOpponent;
    public static String turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        listView = findViewById(R.id.games_available);
        adapter = new ArrayAdapter<>( this, android.R.layout.select_dialog_singlechoice );

        for( String key : getAvaiableGames( ) ){
            adapter.add( key );
        }

        listView.setAdapter( adapter );

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener( ){
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id){
                ChallengeActivity.idOpponent = (String) adapter.getItemAtPosition( position );
            }
        });

        final Button joinButton = findViewById( R.id.join );
        joinButton.setOnClickListener( new View.OnClickListener( ){
            public void onClick(View v) {
                if( ChallengeActivity.idOpponent == null || ChallengeActivity.idOpponent.equals( "" ) ) {
                    Toast.makeText(getApplicationContext(), "You should be select one game", Toast.LENGTH_SHORT).show();
                    return;
                }
                EditText nickname = findViewById( R.id.nickname );
                idGamer = nickname.getText( ).toString( ).trim( );
                if( idGamer == null || idGamer.equals( "" ) ){
                    nickname.setError( "This field can not be Empty" );
                    return;
                }
                ChallengeActivity.game = idOpponent;

                JSONObject json = new JSONObject( );
                try {
                    json.put("idGamer", idGamer);
                    json.put("idOpponent", idOpponent);
                }catch( Exception e ){
                    e.printStackTrace( );
                }

                try {
                    turn = (String) jsonResponse( HOST + JOIN + game, "POST", json.toString( ) ).get("turn");
                }catch( Exception e ) {
                    e.printStackTrace( );
                }
                finish( );
            }
        });

        final Button createButton = findViewById( R.id.create );
        createButton.setOnClickListener( new View.OnClickListener( ){
            public void onClick(View v) {
                EditText nickname = findViewById( R.id.nickname );
                idGamer = nickname.getText( ).toString( ).trim( );
                if( idGamer == null || idGamer.equals("") ){
                    nickname.setError( "This field can not be Empty" );
                    return;
                }
                ChallengeActivity.game = idGamer;
                JSONObject json = jsonResponse( HOST + NEW + game, "GET", null );
                try {
                    ChallengeActivity.idOpponent = (String) json.get( "invited" );
                    ChallengeActivity.turn = (String) json.get("turn");
                }catch( Exception e ){
                    e.printStackTrace( );
                }
                finish( );
            }
        });

    }

    private List<String> getAvaiableGames( ){
        JSONObject jsonObj = jsonResponse( HOST + GAMES, "GET", null );
        System.out.println(jsonObj);
        List<String> list =  new ArrayList<>( );
        try {
            Iterator<String> iterator = jsonObj.keys( );

            while( iterator.hasNext( ) ){
                list.add( iterator.next( ) );
            }
        }
        catch( Exception e ) {

        }
        return list;
    }

    public static JSONObject jsonResponse(String url, String method, String request ){
        HttpConnection connection = new HttpConnection( );
        JSONObject json = null;
        try{
            System.out.println(url);
            json = new JSONObject( connection.execute( url, method, request ).get( ) );
        }catch(Exception e){}

        return json;
    }

    public static String stringResponse( String url, String method, String json ){
        HttpConnection connection = new HttpConnection( );
        String response = null;
        try {
            response = connection.execute( url, method, json ).get( );
        }catch( Exception e ){
            e.printStackTrace( );
        }
        return response;

    }

}
