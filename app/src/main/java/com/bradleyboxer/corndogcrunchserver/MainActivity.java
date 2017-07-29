package com.bradleyboxer.corndogcrunchserver;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    static String message = "";
    static boolean newMessage = false;
    static Server server;
    int messageProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onStartButton(View v) {
        String port = ((EditText)findViewById(R.id.portText)).getText().toString();
        ((TextView)findViewById(R.id.textView)).setText("Server started! Connect to me at "+getHostIp()+":"+port);

        server = new Server();
        server.start(port);

        Thread displayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.interrupted()) {
                    if(newMessage) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = ((TextView)findViewById(R.id.textView));
                                if(messageProgress<15) {
                                    textView.setText(message+"\n"+textView.getText());
                                    messageProgress++;
                                } else {
                                    textView.setText(message);
                                    messageProgress = 0;
                                }
                            }
                        });
                        newMessage = false;
                    }
                    try {Thread.sleep(250);} catch(InterruptedException e) {}
                }
            }
        });
        displayThread.start();
    }


    public String getHostIp() {
        WifiManager wifiMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        return ip;
    }
}
