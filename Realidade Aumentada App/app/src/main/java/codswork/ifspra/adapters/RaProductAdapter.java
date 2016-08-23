package codswork.ifspra.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.activities.DetailActivity;
import codswork.ifspra.pojo.Product;

/**
 * Created by Felipe on 14/08/2016.
 */
public class RaProductAdapter extends ArrayAdapter<Product> {

    String url="http://julianoblanco-001-site3.ctempurl.com/Images/Products/";
    private Context context;
    private List<Product> productList;


    public RaProductAdapter(Context context, int resource, List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.productList = objects;
    }


    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ra_product_item_file,parent,false);
        Product prod = productList.get(position);

        TextView tv = (TextView) view.findViewById(R.id.name);
        tv.setText(prod.getName() + " " + position);

        TextView tv2 = (TextView) view.findViewById(R.id.price);
        tv2.setText("R$ " + Double.toString(prod.getPrice()));

        final ImageView img = (ImageView) view.findViewById(R.id.img);
        img.setImageBitmap(prod.getImg());

        final int id = prod.getIdProduct();
        final Product p = prod;
        final LinearLayout lv = (LinearLayout)view.findViewById(R.id.ll_ra_product);
        final ImageButton show = (ImageButton)view.findViewById(R.id.btn_show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Controller.ActiveRaItem==-1) {
                    lv.setBackgroundColor(Color.LTGRAY);
                    Controller.ActiveRaItem = position;
                    ((ImageButton)v.findViewById(R.id.btn_show))
                            .setImageBitmap(
                                    BitmapFactory.decodeResource(
                                            v.getResources(),
                                            R.drawable.cart_icon_white
                                    )
                            );
                }
                else {
                    ((ImageButton)
                            ((LinearLayout)
                                    ((LinearLayout)
                                            parent.getChildAt(Controller.ActiveRaItem)
                                    ).getChildAt(2)
                            ).getChildAt(0)
                    ).setImageBitmap(
                            BitmapFactory.decodeResource(
                                    v.getResources(),
                                    R.drawable.projector_icon
                            )
                    );
                    Log.d("RALIST", "Mudando item " + Controller.ActiveRaItem + " para Projector_icon");
                    ((LinearLayout)getView(Controller.ActiveRaItem, convertView, parent)).setBackgroundColor(Color.WHITE);
                    Controller.ActiveRaItem = position;
                    show.setImageBitmap(
                            BitmapFactory.decodeResource(
                                    v.getResources(),
                                    R.drawable.cart_icon_white
                            )
                    );
                    //Log.d("RALIST", "Mudando item " + Controller.ActiveRaItem + " para cart_icon");
                    lv.setBackgroundColor(Color.LTGRAY);
                }
            }
        });
        return view;
    }

    private AlertDialog getDialog(final Product p, final View v){
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
