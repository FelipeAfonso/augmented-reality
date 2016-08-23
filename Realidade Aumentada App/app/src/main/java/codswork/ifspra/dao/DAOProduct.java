package codswork.ifspra.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

import codswork.ifspra.database.Database;
import codswork.ifspra.pojo.Product;

/**
 * Created by Felipe on 12/07/2016.
 */
public class DAOProduct {
    private int idProduct;
    private String Name;
    private String Description;
    private float Price;
    private String ShortDescription;
    private int Stock;
    private Boolean Featured;
    private float Weight;
    private String Picture1;
    private String Picture2;
    private int SubCategory_idSubCategory;

    public DAOProduct( int idProduct, String name, String description, float price,
                       String shortDescription, int stock, Boolean featured, float weight,
                       String picture1, String picture2, int subCategory_idSubCategory) {

        SubCategory_idSubCategory = subCategory_idSubCategory;
        this.idProduct = idProduct;
        Name = name;
        Description = description;
        Price = price;
        ShortDescription = shortDescription;
        Stock = stock;
        Featured = featured;
        Weight = weight;
        Picture1 = picture1;
        Picture2 = picture2;
    }

    public DAOProduct( Product p ) {

        SubCategory_idSubCategory = p.getSubCategory_idSubCategory();
        this.idProduct = p.getIdProduct();
        Name = p.getName();
        Description = p.getDescription();
        Price = p.getPrice();
        ShortDescription = p.getShortDescription();
        Stock = p.getStock();
        Featured = p.getFeatured();
        Weight = p.getWeight();
        Picture1 = p.getPicture1();
        Picture2 = p.getPicture2();
    }

    public DAOProduct(Context c, int id){ this.idProduct = id; getProduct(c, id); }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public Boolean getFeatured() {
        return Featured;
    }

    public void setFeatured(Boolean featured) {
        Featured = featured;
    }

    public float getWeight() {
        return Weight;
    }

    public void setWeight(float weight) {
        Weight = weight;
    }

    public String getPicture1() {
        return Picture1;
    }

    public void setPicture1(String picture1) {
        Picture1 = picture1;
    }

    public String getPicture2() {
        return Picture2;
    }

    public void setPicture2(String picture2) {
        Picture2 = picture2;
    }

    public int getSubCategory_idSubCategory() {
        return SubCategory_idSubCategory;
    }

    public void setSubCategory_idSubCategory(int subCategory_idSubCategory) {
        SubCategory_idSubCategory = subCategory_idSubCategory;
    }

    /* ########################
       #   CRUD - Produtos    #
       ######################## */

    //CREATE
    public boolean createProduct(Context c){
        Database db = Database.getInstance(c.getApplicationContext());
        String query = "INSERT INTO product(id, name, description, " +
                "price, short_description, stock, " +
                "featured, weight, picture, " +
                "picture2, subcat_id) VALUES('" +
                + this.getIdProduct() + "',' "
                + this.getName()+ "',' "
                + this.getDescription() + "',' "
                + this.getPrice() + "',' "
                + this.getShortDescription() + "',' "
                + this.getStock() + "',' "
                + this.getFeatured() + "',' "
                + this.getWeight() + "',' "
                + this.getPicture1() + "',' "
                + this.getPicture2() + "',' "
                + this.getSubCategory_idSubCategory()
                + "')";
        //Log.d("DB-Get", query);
        try{db.getWritableDatabase().execSQL(query); db.close(); return true;}catch(Exception e){ return false;}
    }

    //READ
    public static DAOProduct getProduct(Context context, int id){
        Database db = Database.getInstance(context.getApplicationContext());
        String query = "SELECT * FROM product WHERE id=" + id;
        //Log.d("DB-Get", query);
        Cursor c = db.getWritableDatabase().rawQuery(query,null);
        DAOProduct p;
        if(c!=null && c.moveToFirst()){

            p = new DAOProduct(
                    Database.getDataInt("id", c),
                    Database.getDataString("name",c),
                    Database.getDataString("description",c),
                    Database.getDataFloat("price",c),
                    Database.getDataString("short_description",c),
                    Database.getDataInt("stock",c),
                    (Database.getDataInt("featured",c)==1),
                    Database.getDataFloat("weight",c),
                    Database.getDataString("picture",c),
                    Database.getDataString("picture2",c),
                    Database.getDataInt("subcat_id",c)
            );
            c.close();
            db.close();
        }else{
            p = null; }
        return p;
    }
    public ArrayList<DAOProduct> getProducts(Context context){
        ArrayList<DAOProduct> temp = new ArrayList<>();

        Database db = Database.getInstance(context.getApplicationContext());
        String query = "SELECT * FROM product";
        //Log.d("DB-Get", query);
        Cursor c = db.getWritableDatabase().rawQuery(query,null);
        if(c!=null && c.moveToFirst()){
            do {
                DAOProduct p = new DAOProduct(
                        Database.getDataInt("id", c),
                        Database.getDataString("name", c),
                        Database.getDataString("description", c),
                        Database.getDataFloat("price", c),
                        Database.getDataString("short_description", c),
                        Database.getDataInt("stock", c),
                        (Database.getDataInt("featured", c) == 1),
                        Database.getDataFloat("weight", c),
                        Database.getDataString("picture", c),
                        Database.getDataString("picture2", c),
                        Database.getDataInt("subcat_id", c)
                );
                temp.add(p);
            }while (c.moveToNext());
            c.close();
            db.close();
        }
        return temp;
    }

    //UPDATE
    public boolean updateProduct(Context c){
        Database db = Database.getInstance(c.getApplicationContext());
        String query = "UPDATE product SET "
                + "id=" + this.getIdProduct() + ", "
                + "name="+ this.getName() + ", "
                + "description="+ this.getDescription() + ", "
                + "price="+ this.getPrice() + ", "
                + "short_description="+ this.getShortDescription() + ", "
                + "stock="+ this.getStock() + ", "
                + "featured="+ this.getFeatured() + ", "
                + "weight="+ this.getWeight() + ", "
                + "picture="+ this.getPicture1() + ", "
                + "picture2="+ this.getPicture2() + ", "
                + "subcat_id="+ this.getSubCategory_idSubCategory() + ", "
                + ") WHERE id=" + this.getIdProduct();
        //Log.d("DB-Get", query);
        try{db.getWritableDatabase().execSQL(query); db.close(); return true;}catch(Exception e){ return false;}
    }

    //DELETE
    public boolean deleteProduct(Context c){
        Database db = Database.getInstance(c.getApplicationContext());
        String query = "DELETE FROM product WHERE id=" + this.getIdProduct();
        //Log.d("DB-Get", query);
        try{db.getWritableDatabase().execSQL(query); db.close(); return true;}catch(Exception e){ return false;}
    }
}
