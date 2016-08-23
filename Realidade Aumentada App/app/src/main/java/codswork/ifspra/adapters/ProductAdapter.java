package codswork.ifspra.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.activities.DetailActivity;
import codswork.ifspra.pojo.Product;

/**
 * Created by Juliano on 31/05/2016.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    String url="http://julianoblanco-001-site3.ctempurl.com/Images/Products/";
    private Context context;
    private List<Product> productList;


    public ProductAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.productList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.product_item_file,parent,false);
        Product prod = productList.get(position);

        TextView tv = (TextView) view.findViewById(R.id.name);
        tv.setText(prod.getName());

        TextView tv2 = (TextView) view.findViewById(R.id.price);
        tv2.setText("R$ " + Double.toString(prod.getPrice()));

        ImageView img = (ImageView) view.findViewById(R.id.img);
        img.setImageBitmap(prod.getImg());
        //Picasso.with(context)
        //        .load(url + prod.getPicture1())//.resize(100, 100)
        //        .into(img);

        final int id = prod.getIdProduct();
        final Product p = prod;
        ImageButton info = (ImageButton)view.findViewById(R.id.btn_detail);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra("id", id);
                getContext().startActivity(i);
            }
        });

        ImageButton add = (ImageButton)view.findViewById(R.id.btn_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Controller.isFastBuyChecked){ getDialog(p, v).show(); }
                else{
                    Controller.Carrinho.add(p, 1);//, v);
                    Controller.vibrateShort(getContext());
                    Toast.makeText(getContext(), "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }



    private AlertDialog getDialog(final Product p,final View v){
        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final NumberPicker npView = new NumberPicker(getContext());
        npView.setMinValue(1);
        npView.setMaxValue(20);
        return new AlertDialog.Builder(getContext())
                .setTitle("Selecione a quantidade")
                .setView(npView)
                .setPositiveButton("Adicionar ao Carrinho",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Controller.Carrinho.add(p, npView.getValue());//, v);
                                Controller.vibrateShort(getContext());
                                Toast.makeText(getContext(), "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
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
