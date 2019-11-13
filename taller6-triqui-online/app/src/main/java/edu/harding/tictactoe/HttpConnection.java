package edu.harding.tictactoe;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpConnection extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... strings) {

        try {
            URL url = new URL( strings[0] );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection( );
            httpURLConnection.setConnectTimeout( 180000 );
            httpURLConnection.setRequestMethod(strings[1]);
            if( httpURLConnection.getRequestMethod( ).equals( "POST" ) ){
                httpURLConnection.setDoOutput( true );
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.getOutputStream( ).write( strings[2].getBytes( "UTF-8" ) );
                httpURLConnection.getOutputStream( ).flush( );
                httpURLConnection.getOutputStream( ).close( );
            }
            httpURLConnection.connect( );
            int status = httpURLConnection.getResponseCode(  );
            StringBuffer stringBuffer = new StringBuffer( );
            if( status == HttpURLConnection.HTTP_OK ){
                InputStream inputStream = new BufferedInputStream( httpURLConnection.getInputStream( ) );
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
                String line = "";
                while( (line = bufferedReader.readLine( )) != null ){
                    stringBuffer.append( line );
                }
            }
            httpURLConnection.disconnect( );
            return stringBuffer.toString( );
        }catch( Exception e ){
            e.printStackTrace( );
        }
        return null;
    }
}
