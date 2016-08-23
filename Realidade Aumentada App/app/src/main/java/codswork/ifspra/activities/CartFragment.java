package codswork.ifspra.activities;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import codswork.ifspra.CartInterface;
import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.RestInterface;
import codswork.ifspra.adapters.OrderedProductAdapter;
import codswork.ifspra.pojo.MessageBox;
import codswork.ifspra.pojo.Product;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Juliano on 12/07/2016.
 */
public class CartFragment extends Fragment implements CartInterface {

    View myView;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //if(Controller.isFastBuyChecked)
        //    menu.findItem(R.id.check_buy).setChecked(true);
        //if(Controller.isFastRemChecked)
        //    menu.findItem(R.id.check_rem).setChecked(true);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.cart_layout, container, false);
        // setHasOptionsMenu(true);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Carrinho");

        updateCart();

        ((Button) view.findViewById(R.id.btn_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller.Carrinho.clear();
                updateCart();
                Controller.vibrateShort(v.getContext());
            }
        });

        /*
        ((Button)view.findViewById(R.id.btn_end)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)
                        getView().getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                new AlertDialog.Builder(getView().getContext())
                        .setTitle("Deseja finalizar o pedido?")
                        .setPositiveButton("Sim",
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Controller.vibrateShort(getView().getContext());
                                        send_json();
                                        Controller.Carrinho.clear();
                                        updateCart();
                                        Controller.vibrateShort(getView().getContext());
                                    }
                                })
                        .setNegativeButton("Não",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .create().show();
            }
        });
*/


        ((Button) view.findViewById(R.id.btn_end)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Controller.loggedUser_in) { //Verify if user is authenticated)

                    if (Controller.ClientTable == 0) { //If the number of client table isn´t set call the method to set.
                        CheckoutTable(v).show();
                    }else{
                        CheckoutDirect(v).show();
                    }

                } else { //Redirect to login area


                    MessageBox.show(myView.getContext(), "Você não está logado", "Você será redirecionado para realizar a autenticação, ok?");

                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , new FillFragment())
                            .commit();

                }
            }
        });


        ((Button) view.findViewById(R.id.btn_makeOrder)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Controller.loggedUser_in) { //Verify if user is authenticated)
                    if (Controller.ClientTable == 0) { //If the number of client table isn´t set call the method to set.
                        makeOrderSetTable(v).show(); //keep the products in the cart
                    } else {
                        makeOrderDirect(v).show();
                    }

                } else { //Redirect to login area


                    MessageBox.show(myView.getContext(), "Você não está logado", "Você será redirecionado para realizar a autenticação, ok?");

                    android.app.FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame
                                    , new FillFragment())
                            .commit();

                }
            }

        });


    }


    private AlertDialog CheckoutTable(final View v) {
        LayoutInflater inflater = (LayoutInflater)
                v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NumberPicker npView = new NumberPicker(v.getContext());
        npView.setMinValue(1);
        npView.setMaxValue(20);
        return new AlertDialog.Builder(v.getContext())
                .setTitle("Selecione a sua mesa no restaurante")
                .setView(npView)
                .setPositiveButton("Finalizar pedido",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                boolean checkout = true; //no checkout the odered
                                Controller.vibrateShort(getView().getContext());
                                send_json(npView.getValue(), checkout); //Get the client table | Checkout indicates if the count to close or not
                                Controller.Carrinho.clear();
                                updateCart();
                                Controller.vibrateShort(getView().getContext());
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                .create();
    }





    private AlertDialog CheckoutDirect(final View v) {
        LayoutInflater inflater = (LayoutInflater)
                v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new AlertDialog.Builder(v.getContext())
                .setTitle("Deseja fechar a conta para a mesa:" + Controller.ClientTable + "?")
                .setPositiveButton("Fazer pedido",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                boolean checkout = true; //no checkout the odered
                                Controller.vibrateShort(getView().getContext());
                                send_json(Controller.ClientTable, checkout); //Get the client table | Checkout indicates if the count to close or not
                                Controller.Carrinho.clear();
                                updateCart();
                                Controller.vibrateShort(getView().getContext());
                                Toast.makeText(v.getContext(), "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                .create();
    }




    private AlertDialog makeOrderSetTable(final View v) { //Accumulates the products in the customer order
        LayoutInflater inflater = (LayoutInflater)
                v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NumberPicker npView = new NumberPicker(v.getContext());
        npView.setMinValue(1);
        npView.setMaxValue(20);
        return new AlertDialog.Builder(v.getContext())
                .setTitle("Selecione a sua mesa no bar")
                .setView(npView)
                .setPositiveButton("Fazer pedido",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                boolean checkout = false; //no checkout the odered
                                Controller.vibrateShort(getView().getContext());
                                Controller.ClientTable = npView.getValue();
                                Controller.SetPrimaryKey(); //Genarate a primaryKey
                                send_json(npView.getValue(), checkout); //Get the client table | Checkout indicates if the count to close or not
                                //Controller.Carrinho.clear();
                                updateCart();
                                Controller.vibrateShort(getView().getContext());
                                Toast.makeText(v.getContext(), "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                .create();
    }


    private AlertDialog makeOrderDirect(final View v) { //Accumulates the products in the customer order
        LayoutInflater inflater = (LayoutInflater)
                v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return new AlertDialog.Builder(v.getContext())
                .setTitle("Deseja realizar esse pedido para a mesa:" + Controller.ClientTable + "?")
                .setPositiveButton("Fazer pedido",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                boolean checkout = false; //no checkout the odered
                                Controller.vibrateShort(getView().getContext());
                                send_json(Controller.ClientTable, checkout); //Get the client table | Checkout indicates if the count to close or not
                                updateCart();
                                Controller.vibrateShort(getView().getContext());
                                Toast.makeText(v.getContext(), "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                .create();
    }









    @Override
    public void updateCart() {
        OrderedProductAdapter adapter = new OrderedProductAdapter(
                getActivity(),
                R.layout.ordered_product_item_file,
                Controller.Carrinho.getProducts(),
                this);

        ListView lv = (ListView)getView().findViewById(R.id.list);
        lv.setAdapter(adapter);


        TextView tv = (TextView)getView().findViewById(R.id.tv_value);
        tv.setText("R$ " + Controller.Carrinho.getValue());
    }

    private void send_json(int ClientTable, boolean checkout){ //ClientTable: Get client table
        //RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://www.pldlivros.com.br/").build();
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://julianoblanco-001-site3.ctempurl.com/").build();
        RestInterface api = adapter.create(RestInterface.class);
        api.insertUserJson(generate_json(ClientTable, checkout),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        BufferedReader reader = null;
                        String output = "";
                        try{
                            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                            output = reader.readLine();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("JSON", output);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                }

        );
    }

    private String generate_json(int ClientTable, boolean checkout){

        try{
            JSONObject cart = new JSONObject();

            cart.put("idFinalized", Controller.Carrinho.getId());
            cart.put("GeneralValueTotal", Controller.Carrinho.getValue());
            cart.put("GeneralQuantity", Controller.Carrinho.getQuantity());
            cart.put("Finalized", Controller.Carrinho.isStatusFinalized());
            cart.put("ClientId", 4); //Cod Client Test

            if (checkout) { //If the count is close, send to the server this information
                cart.put("StatusOrdered", 1); //Delivery, Produce, Delivery...
            }else{
                cart.put("StatusOrdered", 0); //Delivery, Produce, Delivery...
            }
            cart.put("StatusOrderedLocal", 1); //Inside or Outside of store
            cart.put("Note", ""); //Note about the ordered
            cart.put("ZipCodeDelivery", 1); //ZipCode Sample to delivery
            cart.put("PayamentId", 1); //1 - Money, 2 - check or 3 - credit card
            cart.put("ValueChange", 0); //Change Value;
            cart.put("ClientTable", ClientTable); //Number of ClientTable
            cart.put("Checkout", Boolean.toString(checkout)); //Verify if the count is closed or not
            cart.put("PrimaryKey", Controller.primaryKey);

            Log.i("Checkout", Boolean.toString(checkout));

            JSONArray products = new JSONArray();
            for (Product prod:Controller.Carrinho.getProducts().keySet()){
                JSONObject p = new JSONObject();

                boolean ProductDelivered = false;
                if (!prod.product_purchased) { //if the product hasn´t yet been purchased
                    p.put("quantity", Controller.Carrinho.getProducts().get(prod));
                    p.put("product_id", prod.getIdProduct());
                    p.put("ProductDelivered: ", ProductDelivered); //Used to staff know if the product was delivered to the table of client
                    products.put(p);
                }


                prod.setProduct_purchased(true); //set that the product already was ordered
            }

            cart.put("products", products);



            return cart.toString();
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }





/*
    private String generate_json(){
        JSONObject cart = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try{
            cart.put("id", Controller.Carrinho.getId());
            cart.put("value", Controller.Carrinho.getValue());
            cart.put("quantity", Controller.Carrinho.getQuantity());
            cart.put("finalized", Controller.Carrinho.isStatusFinalized());
            JSONArray products = new JSONArray();
            for (Product prod:Controller.Carrinho.getProducts().keySet()){
                JSONObject p = new JSONObject();
                p.put("quantity", Controller.Carrinho.getProducts().get(prod));
                p.put("product_id", prod.getIdProduct());
                products.put(p);
            }
            cart.put("products", products);
            jsonArray.put(cart);
            return jsonArray.toString();
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
    */









}
