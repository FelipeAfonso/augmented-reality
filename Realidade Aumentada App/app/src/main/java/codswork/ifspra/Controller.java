package codswork.ifspra;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import codswork.ifspra.pojo.Ordered;
import codswork.ifspra.pojo.Product;

/**
 * Created by Felipe on 12/07/2016.
 */
public class Controller {

    //public static boolean progress = false;




    private final static Target target = new Target(){
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            bitmapHelper = bitmap;
            Log.d("Load Image", "Loaded: " + from.toString());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("Load Image", "Failed");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("Load Image", "Prepare");
        }
    };






    public static boolean isFastBuyChecked;// = false;
    public static boolean isFastRemChecked;// = false;
    public static int ClientTable = 0; //Set the number of client table
    public static int primaryKey = 0; //Key of order of Client
    public static int ActiveRaItem = -1;
    public static boolean idUser; //Key of order of Client


    public static final String Par_Client = "client"; //Definindo uma constante para a o objeto CONTATO e para a tabela do banco CONTATO


    //------Get Authentication data of client
    public static boolean DataBaseCreate = false; // Controll if the database was created
    public static boolean AuthenticationJsonData = false; // json with data of client from the server
    public static String idClient = "";
    public static String Name = "";
    public static String Email = "";
    public static String StreetName = "";
    public static String Number = "";
    public static String ZipCode = "";
    public static String NameNeighborhood = "";
    public static String NameCity = "";
    public static String Complement = "";
    public static boolean loggedUser_in = false;





    public static void SetPrimaryKey(){ //Used in CartFragment class inside the method makeOrderSetTable
        Random rnd = new Random();
        int num = rnd.nextInt(100000);
        primaryKey = num;
    }


    public static int RandonGenerate(){ //Used in Ordered
        Random rnd = new Random();
        int num = rnd.nextInt(10000);
        return num;
    }


    public static int active_id = -2;

    public static final String EndPointWsRest = "http://julianoblanco-001-site3.ctempurl.com";

    public static ArrayList<Product> ProductsList = new ArrayList<>();

    public static HashMap<Integer, Bitmap> ProductsBitmapList = new HashMap<>();

    public static Ordered Carrinho = new Ordered(); //Forma temporaria de manusear o carrinho

    private static Bitmap bitmapHelper = null;

    /*public static Bitmap getBitmap(final Product p, final Context c, String url){
        Log.d("Load Image", "Loading from: " + url);
        Picasso.with(c)
                .load(url)//.resize(150, 150)
                .into(target);
        return bitmapHelper;
    }*/

    public static Bitmap getBitmap(final Product p, final Context c, String url){
        Log.d("Load Image", "Loading from: " + url);
        Bitmap b;
        try{
            b = Picasso.with(c).load(url).get();
        }catch(IOException e){
            Log.d("Load Image", "Failed load from: " + url);
            b=null;
        }
        return b;
    }


    public static void saveCount(Context c){
        SharedPreferences settings = c.getSharedPreferences("PrefFile", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("OrderedIdCount", Ordered.id_count);
        editor.putBoolean("isFastBuyChecked", isFastBuyChecked);
        editor.putBoolean("isFastRemChecked", isFastRemChecked);
        editor.commit();
    }

    public static int getCount(Context c){
        SharedPreferences settings = c.getSharedPreferences("PrefFile", 0);

        isFastRemChecked = settings.getBoolean("isFastRemChecked", false);
        isFastBuyChecked = settings.getBoolean("isFastBuyChecked", false);

        return settings.getInt("OrderedIdCount", 0);
    }

    public static void vibrateShort(Context c){
        Vibrator vibe = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
    }
    public static void vibrateLong(Context c){
        Vibrator vibe = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(300);
    }


    /*
    public static void LoadJSONintoDB(final Activity context){
        //progress = false;

        final ProgressDialog loading = ProgressDialog.show(context, "Carregando Dados", "Por favor, aguarde...", false, false);

        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(EndPointWsRest).build();
        RestInterface productApi = restAdapter.create(RestInterface.class);

            productApi.getWSRestProduct(new Callback<List<DAOProduct>>() {
                @Override
                public void success(List<DAOProduct> products, Response response) {

                    ProductsList = (ArrayList) products;
                    for (DAOProduct p: ProductsList) {
                        Database db = Database.getInstance(context.getApplicationContext());
                        if(db.getProduct(p.getIdProduct())==null) {
                            db.createProduct(p);
                        }

                        final DAOProduct product = p;
                        Target loadTarget = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                ProductsBitmapList.put(product.getIdProduct(), bitmap);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) { }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) { }
                        };

                        Picasso.with(context.getApplicationContext())
                                .load(EndPointWsRest + p.getPicture1()).resize(150, 150)
                                .into(loadTarget);
                    }
                    loading.dismiss();

                }

                @Override
                public void failure(RetrofitError error) {
                    loading.dismiss();
                    Toast.makeText(context.getApplicationContext(),
                            "Não foi possivel carregar os dados no APP - "
                                    + error.getMessage() + error.getCause(),
                            Toast.LENGTH_LONG).show();
                }
            });

        //progress = true;

    }

    */
}
