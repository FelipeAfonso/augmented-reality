package codswork.ifspra.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import codswork.ifspra.CartInterface;
import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.pojo.Product;

/**
 * Created by Juliano on 31/05/2016.
 */
public class OrderedProductAdapter extends BaseAdapter {

    private final ArrayList mData;

    private CartInterface listener;

    String url="http://julianoblanco-001-site3.ctempurl.com/Images/Products/";
    private Context context;
    private HashMap<Product, Integer> productList;


    public OrderedProductAdapter(Context context, int resource, HashMap<Product, Integer> objects, CartInterface listener) {
        this.context = context;
        this.productList = objects;
        this.mData = new ArrayList();
        this.mData.addAll(objects.entrySet());
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ordered_product_item_file,parent,false);
        HashMap.Entry<Product, Integer> entry = (HashMap.Entry)mData.get(position);
        Product prod = entry.getKey();

        TextView tv = (TextView) view.findViewById(R.id.name);
        tv.setText(prod.getName());

        TextView tv2 = (TextView) view.findViewById(R.id.price);
        tv2.setText("R$ " + Double.toString(prod.getPrice()));

        //Controll to client: bought or not
        String bought="NÃO";
        if (prod.product_purchased){
            bought = "SIM";
        }


        TextView tv3 = (TextView)view.findViewById(R.id.sub_price);
        tv3.setText("Sub: R$ " + Double.toString(entry.getValue() * prod.getPrice()) + "\nComprado: " + bought);

        TextView tv4 = (TextView)view.findViewById(R.id.qtd);
        tv4.setText("x" + Integer.toString(entry.getValue()));

        ImageView img = (ImageView) view.findViewById(R.id.img);
        img.setImageBitmap(prod.getImg());

        final Product p = prod;

        Button remove = (Button)view.findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Controller.isFastRemChecked){ getDialog(p,v).show(); }
                else{
                    Controller.Carrinho.subtract(p, 1);//, v);
                    listener.updateCart();
                    Controller.vibrateShort(v.getContext());
                    Toast.makeText(v.getContext(), 1 + " " + p.getName() + " removidos com sucesso", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }












    private AlertDialog getDialog(final Product p, final View v){
        LayoutInflater inflater = (LayoutInflater)
                v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NumberPicker npView = new NumberPicker(v.getContext());
        npView.setMinValue(1);
        npView.setMaxValue(20);
        return new AlertDialog.Builder(v.getContext())
                .setTitle("Selecione a quantidade de " + p.getName() + " a serem removidos")
                .setView(npView)
                .setPositiveButton("Remover",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Controller.Carrinho.subtract(p, npView.getValue());//, v);
                                listener.updateCart();
                                Controller.vibrateShort(v.getContext());
                                Toast.makeText(v.getContext(), npView.getValue() + " " + p.getName() + " removidos com sucesso", Toast.LENGTH_SHORT).show();
                            }
                        })
                .setNeutralButton("Remover todos",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Controller.Carrinho.remove(p);
                                listener.updateCart();
                                Controller.vibrateLong(v.getContext());
                                Toast.makeText(v.getContext(), "Todos os itens removidos com sucesso", Toast.LENGTH_SHORT).show();
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



