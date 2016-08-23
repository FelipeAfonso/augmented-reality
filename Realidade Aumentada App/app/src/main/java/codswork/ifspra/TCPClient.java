package codswork.ifspra;

/**
 * Created by Felipe on 17/08/2016.
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class TCPClient {

    //public static String ipAddress = "0.0.0.0";
    private static Socket socket;

    private static boolean Connect(){
        try {
            InetAddress ipAd = InetAddress.getByName(getPublicIpAddress());
            socket = new Socket(ipAd, 1209);
            Log.d("TCP","Conexão bem sucedida");
            return true;
        }catch (UnknownHostException e) {
            Log.e("TCP", "Erro", e);
            return false;
        } catch (IOException e) {
            Log.e("TCP", "IOException", e);
            e.printStackTrace();
            return false;
        }
    }

    public static void Send(String message){
        try {
            if(socket==null)
                Connect();
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.write(message.getBytes());
            Log.d("TCP","Enviando para o servidor: " + message);
            Close();
        }catch (IOException e) {
            Log.e("TCP", "IOException", e);
            e.printStackTrace();
        }
    }

    public static String Receive(){
        try {
            if(socket==null)
                Connect();
            //DataInputStream stream = new DataInputStream(socket.getInputStream());
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            //Log.d("TCP", stream.readUTF());
            //Log.d("TCP","Recebendo do servidor: " + reader.readLine());
            //return stream.readUTF();
            String temp = reader.readLine();
            Close();
            return temp;
        }catch (IOException e) {
            Log.e("TCP", "IOException", e);
            e.printStackTrace();
            return "error";
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }finally{ Close();}

    }

    public static Bitmap ReceiveImage(){
        try {
            if(socket==null)
                Connect();
            if(socket==null) Connect();
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            //clear
            buffer.flush();
            socket.close();
            byte[] b = buffer.toByteArray();
            Close();
            return BitmapFactory.decodeByteArray(b,0,b.length);
        }catch (IOException e) {
            Log.e("TCP", "IOException", e);
            e.printStackTrace();
            return null;
        }
    }

    private static void Close(){ try{socket.close();}catch (Exception e) {e.printStackTrace(); }}

    private static String getPublicIpAddress() throws MalformedURLException,IOException {
        URL connection = new URL("http://checkip.amazonaws.com/");
        URLConnection con = connection.openConnection();
        String str = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        str = reader.readLine();
        return str;
    }

    public static Boolean send(String message) {
        try {

            //connect
            Log.d("TCP", "Conectando");
            InetAddress ipAd = InetAddress.getByName(getPublicIpAddress());
            Socket socket = new Socket(ipAd, 1209);
            //send
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.write(message.getBytes());
            //clear
            socket.close();
            Log.d("TCP", "Mensagem enviada: " + message);
            return true;
        } catch (UnknownHostException e) {
            Log.e("TCP", "Erro", e);
        } catch (IOException e) {
            Log.e("TCP", "IOException", e);
            e.printStackTrace();
        }
        return false;
    }
    public static String send_recv(String message) {
        Socket socket = null;
        String response = new String();
        try {
            //connect
            Log.d("TCP", "Conectando");
            InetAddress ipAd = InetAddress.getByName(getPublicIpAddress());
            socket = new Socket(ipAd, 1209);
            //send
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.write(message.getBytes());
            //receive
            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            response = reader.readLine();
            Log.d("TCP", "Resposta: " + response);
            //clear
            socket.close();
            return response;
        } catch (UnknownHostException e) {
            response= e.toString();
            Log.e("TCP", "Erro", e);
        } catch (IOException e) {
            response= e.toString();
            Log.e("TCP", "IOException", e);
            e.printStackTrace();
        }
        return "error";
    }
    public static Boolean send_recv_img(String message) {
        Socket socket = null;
        try {
            //connect
            Log.d("TCP", "Conectando");
            InetAddress ipAd = InetAddress.getByName(getPublicIpAddress());
            socket = new Socket(ipAd, 1209);
            //send
            DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
            DOS.write(message.getBytes());
            //receive
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while((nRead = is.read(data, 0, data.length)) != -1){
                buffer.write(data, 0, nRead);
            }
            //clear
            buffer.flush();
            socket.close();
            //colocar a imagem no image view
            final byte[] b = buffer.toByteArray();

            return true;
        } catch (UnknownHostException e) {
            Log.e("TCP", "Erro", e);
        } catch (IOException e) {
            Log.e("TCP", "IOException", e);
            e.printStackTrace();
        }
        return false;
    }

}