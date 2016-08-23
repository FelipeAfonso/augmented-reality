package codswork.ifspra.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.TCPClient;
import codswork.ifspra.adapters.RaProductAdapter;
import codswork.ifspra.pojo.Product;


public class RaFragment extends Fragment {

    private boolean connected = false;
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
        myView  = inflater.inflate(R.layout.fragment_ra, container, false);
        //setHasOptionsMenu(true);
        return myView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Realidade Aumentada");
        prepareLayout();

        new Thread(new Runnable(){
            @Override
            public void run() {
                if(TCPClient.send_recv("handshake").equals("hi")){
                    connected = true;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)getActivity().findViewById(R.id.tv_connection)).setText("Conectado");
                            ((TextView)getActivity().findViewById(R.id.tv_connection)).setTextColor(Color.WHITE);
                            ((TextView)getActivity().findViewById(R.id.tv_status)).setText("");
                        }
                    });
                }
                else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView) getActivity().findViewById(R.id.tv_status)).setText("Clique aqui para tentar novamente");
                            ((TextView) getActivity().findViewById(R.id.tv_status)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    run();
                                }
                            });
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Controller.ActiveRaItem = -1;
    }
    ///*
    @TargetApi(16)
    private void prepareLayout(){
        int i =0;
        for (Product p: Controller.ProductsList) {
            final LinearLayout base = new LinearLayout(getActivity());
            base.setOrientation(LinearLayout.HORIZONTAL);
            base.setGravity(Gravity.CENTER_VERTICAL);
            base.setPadding(5,5,5,5);
            base.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

            ImageView img = new ImageView(getActivity());
            img.setLayoutParams(new TableLayout.LayoutParams(200, 200, 1));
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img.setImageBitmap(p.getImg());

            LinearLayout center = new LinearLayout(getActivity());
            center.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            center.setPadding(5,0,5,0);
            center.setGravity(Gravity.CENTER_VERTICAL);
            center.setOrientation(LinearLayout.VERTICAL);

            TextView nome = new TextView(getActivity());
            nome.setTextSize(20f);
            nome.setText(p.getName());
            center.addView(nome);

            TextView price = new TextView(getActivity());
            price.setTextColor(Color.DKGRAY);
            price.setText("R$ " + Double.toString(p.getPrice()));
            center.addView(price);

            ImageButton btn = new ImageButton(getActivity());
            btn.setLayoutParams(new TableLayout.LayoutParams(150, 150, 1));
            btn.setMaxHeight(120);
            btn.setMaxWidth(120);
            btn.setImageBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.projector_icon));
            btn.setBackground(getResources().getDrawable(R.drawable.round_button));
            btn.setScaleType(ImageView.ScaleType.FIT_CENTER);
            final Product prod = p;
            final int x = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Controller.ActiveRaItem==-1) {
                        Controller.ActiveRaItem = x;
                        base.setBackgroundColor(Color.LTGRAY);
                        ((ImageButton)v).setImageBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.cart_icon_white));

                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                TCPClient.send("selectbyname_" + prod.getName());
                            }
                        }).start();
                    }
                    else if(Controller.ActiveRaItem!=x) {
                        LinearLayout ll = (LinearLayout)(
                                (LinearLayout)getView().findViewById(R.id.raLinearLayout)
                        ).getChildAt(Controller.ActiveRaItem);
                        ll.setBackgroundColor(Color.WHITE);
                        ((ImageButton)ll.getChildAt(2)).setImageBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.projector_icon));

                        Controller.ActiveRaItem = x;
                        base.setBackgroundColor(Color.LTGRAY);
                        ((ImageButton)v).setImageBitmap(BitmapFactory.decodeResource(v.getResources(), R.drawable.cart_icon_white));

                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                TCPClient.send("selectbyname_" + prod.getName());
                            }
                        }).start();

                    }else{
                        if(!Controller.isFastBuyChecked){ getDialog(prod, v).show(); }
                        else{
                            Controller.Carrinho.add(prod, 1);//, v);
                            Controller.vibrateShort(getActivity());
                            Toast.makeText(getActivity(), "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            base.addView(img);
            base.addView(center);
            base.addView(btn);
            ((LinearLayout)getView().findViewById(R.id.raLinearLayout)).addView(base);
            i++;
        }
    }
    //*/

    private AlertDialog getDialog(final Product p, final View v){
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NumberPicker npView = new NumberPicker(getActivity());
        npView.setMinValue(1);
        npView.setMaxValue(20);
        return new AlertDialog.Builder(getActivity())
                .setTitle("Selecione a quantidade")
                .setView(npView)
                .setPositiveButton("Adicionar ao Carrinho",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Controller.Carrinho.add(p, npView.getValue());//, v);
                                Controller.vibrateShort(getActivity());
                                Toast.makeText(getActivity(), "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                .create();
    }
}
