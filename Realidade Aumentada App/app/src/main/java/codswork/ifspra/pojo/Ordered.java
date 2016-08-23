package codswork.ifspra.pojo;

import android.util.Log;

import java.util.Calendar;
import java.util.LinkedHashMap;

import codswork.ifspra.Controller;

/**
 * Created by Felipe on 16/07/2016.
 */
public class Ordered {

    public static int id_count;
    private int id;

    private double value = 0;

    private int quantity = 0;

    private Calendar OrderedTime;

    private boolean StatusFinalized;

    //private HashMap<Product, Integer> Products = new HashMap<>();
    private LinkedHashMap<Product, Integer> Products = new LinkedHashMap<>();

    public Ordered(){
        id_count++;
        this.id = id_count;
        this.OrderedTime = Calendar.getInstance();
        this.StatusFinalized = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // public HashMap<Product, Integer> getProducts() {
    public LinkedHashMap<Product, Integer> getProducts() {
        return Products;
    }

    public double getValue() {
        return value;
    }

    public int getQuantity() {
        return quantity;
    }

    public Calendar getOrderedTime() {
        return OrderedTime;
    }
    public boolean isStatusFinalized() {
        return StatusFinalized;
    }

    public void setStatusFinalized(boolean statusFinalized) {
        StatusFinalized = statusFinalized;
    }

    public void add(Product p, int quantity){//, View v){
        if(!Products.containsKey(p)) { //If there isn´t a product in HashMap add
            Products.put(p, quantity);
            Log.d(" Produto novo no cart ", Integer.toString(p.idProduct) + Boolean.toString(p.product_purchased));
        }else{ //if there is a product in HashMap:
            int q = Products.get(p);

            /*----------Original Code
            Products.remove(p);
            Products.put(p, quantity + q);
            */

            //----------extended Code
            if (p.product_purchased) {//Verify if this product already was ordered. Case positive

                //Create new object to stored two register with same data. The difference is in purchased or not
                Product objP = new Product();
                objP.idProduct = p.idProduct;
                objP.Name = p.Name;
                objP.setImg(p.getImg());
                objP.product_purchased = false;
                objP.Price = p.Price;


                Products.put(objP, quantity); //Add new hashMap Tuple with product_puchased variable set false

                Log.d(" Produto comprado ", Integer.toString(p.idProduct) + Boolean.toString(p.product_purchased) + Controller.RandonGenerate());

            }else{ //Remove and count the total quantity of the product specific
                //Products.remove(p);
                Products.put(p, quantity + q);
                Log.d(" Produto n comp ejá Add", Integer.toString(p.idProduct) + Boolean.toString(p.product_purchased));
            }



            int r=0;
            for (Product prod:Controller.Carrinho.getProducts().keySet()) {
                Log.d(" HashMap id", Integer.toString(prod.idProduct) + Boolean.toString(prod.product_purchased) + Integer.toString(r) + prod.getImg());
                r++;
            }






        }
        value += p.getPrice() * quantity;
        this.quantity += quantity;
        //((MenuItem)v.findViewById(R.id.total_value)).setTitle("R$ " + value);
    }



/*
    public void add(Product p, int quantity){//, View v){
        if(!Products.containsKey(p)) {
            Products.put(p, quantity);
        }else{
            int q = Products.get(p);
                Products.remove(p);
                Products.put(p, quantity + q);
        }
        value += p.getPrice() * quantity;
        this.quantity += quantity;
        //((MenuItem)v.findViewById(R.id.total_value)).setTitle("R$ " + value);
    }
*/




    public void subtract(Product p, int quantity){//, View v){
        if(Products.containsKey(p)) {
            int q = Products.get(p);
            Products.remove(p);
            value -= p.getPrice() * quantity;
            this.quantity -= quantity;
            if(q-quantity<0) {
                this.quantity += q-quantity;
            }
            if(q-quantity>0) {
                Products.put(p, q - quantity);
            }
            //((MenuItem)v.findViewById(R.id.total_value)).setTitle("R$ " + value);
        }
    }

    public void remove(Product p){ Products.remove(p);}
    public void clear(){Products.clear(); value=0; quantity=0;}
}
