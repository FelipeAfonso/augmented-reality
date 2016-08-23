package codswork.ifspra.connection;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/*** Created by Felipe on 16/08/2016. */
    public class Client {

        String dstAddress;
        int dstPort;
        String response = "";

        Client() {
              try {
              dstAddress = getPublicIpAddress();
        }catch (Exception e){}
        dstPort = 1209;
    }

    public String getPublicIpAddress() throws MalformedURLException,IOException {
        URL connection = new URL("http://checkip.amazonaws.com/");
        URLConnection con = connection.openConnection();
        String str = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        str = reader.readLine();
        return str;
    }
}