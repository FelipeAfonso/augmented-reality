package codswork.ifspar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	private Button btn_left;
	private Button btn_right;
	private TextView tv_output;
	private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tv_output= (TextView)findViewById(R.id.tv_output);
        btn_right= (Button)findViewById(R.id.btn_right);
        btn_left= (Button)findViewById(R.id.btn_left);
        progress= (ProgressBar)findViewById(R.id.progressBar1);
        
        btn_left.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						send("left", MainActivity.this);
					}
					
				}).start();
			}
        });
        btn_right.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new Thread(new Runnable(){
					@Override
					public void run() {
						send("righ", MainActivity.this);
					}
					
				}).start();
			}
        });
    }

	protected void send(String message, Activity act) {
		try {
			act.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					btn_left.setEnabled(false);
					progress.setVisibility(View.VISIBLE);				
				}
			});
			InetAddress ipAd = InetAddress.getByName("10.0.0.102");
			Log.d("TCP", "C: Connecting");
			act.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					progress.setProgress(1);
					tv_output.setText("Conectando ao servidor");
				}
			});
			Socket socket = new Socket(ipAd, 1209);
			DataOutputStream DOS = new DataOutputStream(socket.getOutputStream());
			DOS.write(message.getBytes());
			act.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					progress.setProgress(2);
					tv_output.setText("Enviando Mensagem ao servidor");
				}
			});
			//BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socket.close();
			act.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					progress.setProgress(3);
					tv_output.setText("Enviado com sucesso");
				}
			});
		} catch (UnknownHostException e) {
			act.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					tv_output.setText("Erro");
				}
			});
			Log.e("TCP", "S: Error", e);
		} catch (IOException e) {
			act.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					tv_output.setText("Erro");
				}
			});
			Log.e("TCP", "C: IOException", e); 
			e.printStackTrace();
		}finally{
			act.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					btn_left.setEnabled(true);
					progress.setVisibility(View.INVISIBLE);
					progress.setProgress(0);
					tv_output.setText("Comando enviado com sucesso. Envie outro comando ao servidor");
				}
			});
		}
	}
}
