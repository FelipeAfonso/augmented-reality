package codswork.ifspra.activities;

import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.RestInterface;
import codswork.ifspra.dao.DaoClient;
import codswork.ifspra.database.Database;
import codswork.ifspra.pojo.Client;
import codswork.ifspra.pojo.MessageBox;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Felipe on 12/07/2016.
 */
public class FillFragment extends Fragment{

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
        myView  = inflater.inflate(R.layout.fragment_fill, container, false);
        // setHasOptionsMenu(true);
        return myView;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");

        Controller.active_id = -2;



        //Hide session finalized and update data button
        ((Button) getActivity().findViewById(R.id.BtnUpdateUser)).setVisibility(View.INVISIBLE);
        ((Button) getActivity().findViewById(R.id.BtnSessionFinish)).setVisibility(View.INVISIBLE);









        //------------------Query if already there is a register of client
        Log.d("DatabaseCreated: ", String.valueOf(Controller.DataBaseCreate));

        if(Controller.DataBaseCreate) {//if the database client as created, get the client data
            try {


                SQLiteDatabase conn;
                Database dataBase;

                dataBase = Database.getInstance(getActivity().getApplicationContext()); // Passando o objeto context para manipulação de dados
                conn = dataBase.getWritableDatabase(); //Criar e abrir a base de dados


                DaoClient objDClient = new DaoClient(conn);
                final Client client = objDClient.GetClient(getActivity().getApplicationContext());


                if (!(client.getName().equals(""))) {

                    //Case the user is authenticated, get the data from database and assign to global parameter in controller
                    Controller.Name = client.getName();
                    Controller.Email = client.getEmail();
                    Controller.loggedUser_in = true; //set true case it is loggin

                    //...

                    ((TextView) getActivity().findViewById(R.id.textViewEmail)).setText("Bem vindo: " + client.getName() + " \n");
                    ((TextView) getActivity().findViewById(R.id.textViewEmail)).setTextSize(20);


                    //Manager View elements. Hide the field Text
                    ((EditText) getActivity().findViewById(R.id.TxtEmail)).setVisibility(View.INVISIBLE);
                    ((EditText) getActivity().findViewById(R.id.TxtSenha)).setVisibility(View.INVISIBLE);

                    ((TextView) getActivity().findViewById(R.id.textViewSenha)).setVisibility(View.INVISIBLE);

                    ((Button) getActivity().findViewById(R.id.BtnAutentica)).setVisibility(View.INVISIBLE);
                    ((Button) getActivity().findViewById(R.id.BtnRegister)).setVisibility(View.INVISIBLE);


                    //Show session finalized and update data button
                    ((Button) getActivity().findViewById(R.id.BtnUpdateUser)).setVisibility(View.VISIBLE);
                    ((Button) getActivity().findViewById(R.id.BtnSessionFinish)).setVisibility(View.VISIBLE);

                    //Rotina para excluir os dados


                }
            } catch (Exception e) {

                Log.d("FillFragment Error: ", e.getMessage() + e.getStackTrace());
            }
        }





        ((Button) view.findViewById(R.id.BtnAutentica)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = ((EditText)getActivity().findViewById(R.id.TxtEmail)).getText().toString();
                String password = ((EditText)getActivity().findViewById(R.id.TxtSenha)).getText().toString();

                String PassMD5 = MD5(password); //cryptography md5 password

                Log.d("pass", password);
                Log.d("pass MD5", PassMD5);

                send_json(email, PassMD5);

            }
        });






        ((Button) view.findViewById(R.id.BtnRegister)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("http://julianoblanco-001-site3.ctempurl.com/Home/ClientRegister1");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);


            }
        });




        ((Button) view.findViewById(R.id.BtnUpdateUser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Delete all data in database //Clear the global in controller with the client data

                SQLiteDatabase conn;
                Database dataBase;

                dataBase = Database.getInstance(getActivity().getApplicationContext()); // Passando o objeto context para manipulação de dados
                conn = dataBase.getWritableDatabase(); //Criar e abrir a base de dados

                DaoClient objDClient = new DaoClient(conn);
                final Client client = objDClient.GetClient(getActivity().getApplicationContext());



                Log.d("id Delete ", client.getName() + " " + String.valueOf(client.getIdClient()));
                int idClient = client.getIdClient();
                String Email = client.getEmail();
                String Password = client.getPassword();

                objDClient.DeleteClientData(client.getIdClient()); //Delete data in database and global variable in controller


                Controller.loggedUser_in = false; //Verify if user is authenticated


                String PassMD5 = MD5(Password); //cryptography md5 password


                //Send the user to web system direct in http://localhost:53407/Home/Checkout to login mode
                Uri uri = Uri.parse("http://julianoblanco-001-site3.ctempurl.com/Homne/Checkout?Email="+Email+"&Pass="+PassMD5);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);




                //Close the app
                System.exit(0);






            }
        });




        ((Button) view.findViewById(R.id.BtnSessionFinish)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                SQLiteDatabase conn;
                Database dataBase;

                dataBase = Database.getInstance(getActivity().getApplicationContext()); // Passando o objeto context para manipulação de dados
                conn = dataBase.getWritableDatabase(); //Criar e abrir a base de dados

                DaoClient objDClient = new DaoClient(conn);
                final Client client = objDClient.GetClient(getActivity().getApplicationContext());


                Log.d("id Delete ", client.getName() + " " + String.valueOf(client.getIdClient()));
                objDClient.DeleteClientData(client.getIdClient()); //Delete data in database and global variable in controller

                Controller.loggedUser_in = false; //Verify if user is authenticated


                //Manager View elements. Hide the field Text
                ((EditText)getActivity().findViewById(R.id.TxtEmail)).setVisibility(View.VISIBLE);
                ((EditText)getActivity().findViewById(R.id.TxtSenha)).setVisibility(View.VISIBLE);

                ((TextView)getActivity().findViewById(R.id.textViewEmail)).setVisibility(View.VISIBLE);
                ((TextView)getActivity().findViewById(R.id.textViewEmail)).setText("Email");

                ((TextView)getActivity().findViewById(R.id.textViewSenha)).setVisibility(View.VISIBLE);

                ((Button)getActivity().findViewById(R.id.BtnAutentica)).setVisibility(View.VISIBLE);
                ((Button)getActivity().findViewById(R.id.BtnRegister)).setVisibility(View.VISIBLE);


                //Hide session finalized and update data button
                ((Button) getActivity().findViewById(R.id.BtnUpdateUser)).setVisibility(View.INVISIBLE);
                ((Button) getActivity().findViewById(R.id.BtnSessionFinish)).setVisibility(View.INVISIBLE);




            }
        });








    }








    private String send_json(String Email, String password){ //ClientTable: Get client table
        String output = "";
        //RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://www.pldlivros.com.br/").build();
        RestAdapter adapter = new RestAdapter.Builder().setEndpoint("http://julianoblanco-001-site3.ctempurl.com/").build();
        RestInterface api = adapter.create(RestInterface.class);

        api.insertUserAuthentication(generate_json(Email, password),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        BufferedReader reader = null;
                        String output = "";
                        Log.d("JSON send_json output", output);
                        try{
                            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                            output = reader.readLine();
                            Log.d("JSON TryOut: ", output);

                            if (!(output.equals("false"))) {
                                ParseJson(output); //Parse json from the server and salve in global variable
                            }else{
                                MessageBox.show(myView.getContext(), "Login inválido", "Seu usuário e senha estão inválidos. \n Por favor, tente novamente! ");
                            }


                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Log.d("JSON TryOut: ", output);
                        //return output;
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("JSON TryOut error: ", error.getMessage());
                    }

                }

        );
        Log.d("Method send_json ret: ", output);
        return output;
    }

    private String generate_json(String Email, String password){

        try{
            JSONObject cart = new JSONObject();

            cart.put("Email", Email);
            cart.put("Password", password);


            Log.d("JSON generate", cart.toString());

            return cart.toString();
        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }





    private void ParseJson(String JsonAuthentClient){


        try {
            JSONObject jsonObj = new JSONObject(JsonAuthentClient);
            String StreetName = jsonObj.getString("StreetName");
            String Name = jsonObj.getString("Name");
            String Email = jsonObj.getString("Email");
            String Password = jsonObj.getString("Password");
            String Number = jsonObj.getString("Number");
            String ZipCode = jsonObj.getString("ZipCode");
            String NameNeighborhood = jsonObj.getString("NameNeighborhood");
            String NameCity = jsonObj.getString("NameCity");
            String Complement = jsonObj.getString("Complement");
            int idClient = Integer.valueOf(jsonObj.getString("idClient"));

            Controller.AuthenticationJsonData = true; //Get data of client
            Controller.loggedUser_in = true; //Verify if user is authenticated
            Controller.Name = Name;
            Controller.Number = Number;
            Controller.ZipCode = ZipCode;
            Controller.NameNeighborhood = NameNeighborhood;
            Controller.NameCity = NameCity;
            Controller.Complement = Complement;
            Controller.StreetName = StreetName;
            Controller.idClient = String.valueOf(idClient);


//--------------------------------------------------------------------------Stored data in SQLIte
            //Create the client object
            Client objCli = new Client();
            objCli.setName(Name);
            objCli.setEmail(Email);
            objCli.setPassword(Password);
            objCli.setStreetName(StreetName);
            objCli.setNumber(Number);
            objCli.setComplement(Complement);
            objCli.setZipCode(ZipCode);
            objCli.setNameNeighborhood(NameNeighborhood);
            objCli.setNameCity(NameCity);
            objCli.setStreetName("SP");
            objCli.setIdClient(idClient);


            SQLiteDatabase conn;
            Database dataBase;

            dataBase = Database.getInstance(getActivity().getApplicationContext()); // Passando o objeto context para manipulação de dados
            conn = dataBase.getWritableDatabase(); //Criar e abrir a base de dados

            DaoClient objDCli = new DaoClient(conn);
            objDCli.InserirObjCli(objCli);
//--------------------------------------------------------------------------Stored data in SQLIte

            //Log.d(" IdClient: ", id);
            Log.d(" StreetName: ", StreetName);
            Log.d(" Name: ", Name);


            //Manager View elements. Hide the field Text
            ((EditText)getActivity().findViewById(R.id.TxtEmail)).setVisibility(View.INVISIBLE);
            ((EditText)getActivity().findViewById(R.id.TxtSenha)).setVisibility(View.INVISIBLE);

            ((TextView)getActivity().findViewById(R.id.textViewEmail)).setText("Bem vindo: " + Name);
            ((TextView)getActivity().findViewById(R.id.textViewSenha)).setVisibility(View.INVISIBLE);

            ((Button)getActivity().findViewById(R.id.BtnAutentica)).setVisibility(View.INVISIBLE);
            ((Button)getActivity().findViewById(R.id.BtnRegister)).setVisibility(View.INVISIBLE);

            //Show session finalized and update data button
            ((Button) getActivity().findViewById(R.id.BtnUpdateUser)).setVisibility(View.VISIBLE);
            ((Button) getActivity().findViewById(R.id.BtnSessionFinish)).setVisibility(View.VISIBLE);


            MessageBox.show(myView.getContext(), "Informações de autenticação", "Você está autenticado");







        } catch (JSONException e) {
            e.printStackTrace();
        }


    }





    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }









}
