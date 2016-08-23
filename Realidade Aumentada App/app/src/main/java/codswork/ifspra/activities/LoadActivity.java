package codswork.ifspra.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.RestInterface;
import codswork.ifspra.pojo.Ordered;
import codswork.ifspra.pojo.Product;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoadActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_load);
        Ordered.id_count = Controller.getCount(LoadActivity.this);
    }

    @TargetApi(21)
    private void setStatusBarColor(){
        getWindow().setStatusBarColor(Color.parseColor("#599d29"));
    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.e("Util", "Start: " + Calendar.getInstance().getTime().toString());

        final boolean logged_in = true;

        //final ProgressDialog loading = ProgressDialog.show(LoadActivity.this, "Carregando Dados", "Por favor, aguarde...", false, false);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Controller.EndPointWsRest).build();
        RestInterface productApi = restAdapter.create(RestInterface.class);

        productApi.getWSRestProduct(new Callback<List<Product>>() {
            @Override
            public void success(List<Product> products, Response response) {
                ((TextView)findViewById(R.id.tv_loading)).setText("Conferindo lista de produtos");
                Controller.ProductsList = (ArrayList) products;

                for (Product p: Controller.ProductsList) {
                    if(p.getDAO().getProduct(LoadActivity.this, p.getIdProduct())==null) {
                        p.getDAO().createProduct(LoadActivity.this);
                        Controller.DataBaseCreate = true;
                    }
                    //p.setImg(Controller.getBitmap(p, LoadActivity.this, Controller.EndPointWsRest + "/Images/Products/" + p.getPicture1()));
                }

                HandlerThread handlerThread = new HandlerThread("Loading Thread");
                handlerThread.start();

                Handler handler = new Handler(handlerThread.getLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Product p: Controller.ProductsList) {
                            p.setImg(Controller.getBitmap(p, LoadActivity.this,  Controller.EndPointWsRest + "/Images/Products/" + p.getPicture1()));
                        }
                    }
                });

                //loading.dismiss();
                if(logged_in) {
                    Intent i = new Intent(LoadActivity.this, MainActivity.class);
                    startActivity(i);
                }else{

                }
                Log.e("Util", "End: " + Calendar.getInstance().getTime().toString());
            }

            @Override
            public void failure(RetrofitError error) {
                //loading.dismiss();
                //Toast.makeText(getApplicationContext(),
                //        "Não foi possivel carregar os dados no APP - "
                //                + error.getMessage() + error.getCause(),
                //        Toast.LENGTH_LONG).show();
                ((TextView)findViewById(R.id.tv_loading)).setText("Algum erro ocorreu, confira sua conexão");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(Controller.getCount(LoadActivity.this) != Ordered.id_count) {
            Controller.saveCount(LoadActivity.this);
        }
    }
}
