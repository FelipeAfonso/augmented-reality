package codswork.ifspra.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.dao.DAOProduct;
import codswork.ifspra.database.Database;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int id = (int)this.getIntent().getExtras().get("id");

        //Database db = Database.getInstance(getApplicationContext());
        DAOProduct p =  DAOProduct.getProduct(DetailActivity.this, id);

        ImageView img = (ImageView)findViewById(R.id.img);
        img.setImageBitmap(Controller.ProductsBitmapList.get(p.getIdProduct()));
        //Picasso.with(getApplicationContext())
        //        .load(Controller.EndPointWsRest + "/Images/Products/" + p.getPicture1())
        //        .into(img);
        TextView name = (TextView)findViewById(R.id.tv_name);
        name.setText(p.getName());
        TextView price = (TextView)findViewById(R.id.tv_price);
        price.setText("R$ " + Double.toString(p.getPrice()));
        TextView weight = (TextView)findViewById(R.id.tv_peso);
        weight.setText(Float.toString(p.getWeight()));
        TextView descr = (TextView)findViewById(R.id.tv_descr);
        descr.setText(p.getDescription());
    }
}
