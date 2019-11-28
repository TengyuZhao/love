package com.parbat.cnad.adsdk.httpurlconnection_json;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView img_cover;
    private TextView tv_note;
    private ImageView img_url;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler ( ) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage ( msg );
            if (msg.what == 0x001) {
                String data = (String) msg.obj;
                try {
                    JSONObject jsonObject = new JSONObject ( data );
                    String cover = jsonObject.getString ( "cover" );
                    Glide.with ( MainActivity.this ).load ( cover ).into ( img_cover );
                    JSONArray photos = jsonObject.getJSONArray ( "photos" );
                    for (int i = 0; i < photos.length ( ); i++) {
                        JSONObject o1 = (JSONObject) photos.get ( 0 );
                        String note = o1.getString ( "note" );
                        tv_note.setText ( note );
                        String imgurl = o1.getString ( "imgurl" );
                        Glide.with ( MainActivity.this ).load ( imgurl ).into ( img_url );
                    }

                } catch (JSONException e) {
                    e.printStackTrace ( );
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        initView ( );
        initData ( );
    }

    private void initData() {
        new Thread ( ) {
            @Override
            public void run() {
                super.run ( );

                StringBuilder sb = new StringBuilder ( );
                HttpURLConnection con =null;
                try {
                    URL url = new URL ( "http://c.3g.163.com/photo/api/set/0006/2136404.json" );
                    con = (HttpURLConnection) url.openConnection ( );

                    con.setRequestMethod ( "GET" );
                    con.setDoOutput ( false );
                    con.setDoInput ( true );
                    con.connect ( );

                    if (con.getResponseCode ( ) == 200) {
                        InputStream inputStream = con.getInputStream ( );
                        byte[] bys = new byte[1024];
                        int length = -1;
                        while ((length = inputStream.read ( bys )) != -1) {
                            sb.append ( new String ( bys, 0, length ) );
                        }

                        con.disconnect ( );
                    }

                    Message message = handler.obtainMessage ( );
                    message.what = 0x001;
                    message.obj = sb.toString ( );
                    handler.sendMessage ( message );
                } catch (Exception e) {
                    e.printStackTrace ( );
                }
                finally {
                    try {
                        if (con != null) {
                            con.disconnect ( );//关闭连接
                            con = null;
                        }
                    } catch (Exception e) {

                    }
                }
            }

        }.start ( );


    }

    private void initView() {
        img_cover = (ImageView) findViewById ( R.id.img_cover );
        tv_note = (TextView) findViewById ( R.id.tv_note );
        img_url = (ImageView) findViewById ( R.id.img_url );
    }
}
