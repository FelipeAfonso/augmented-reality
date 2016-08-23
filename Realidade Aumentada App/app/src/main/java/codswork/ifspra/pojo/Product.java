package codswork.ifspra.pojo;

import android.graphics.Bitmap;

import java.io.Serializable;

import codswork.ifspra.dao.DAOProduct;

/**
 * Created by Juliano on 31/05/2016.
 */

public class Product implements Serializable {


    public int idProduct;
    public String Name;
    public String Description;
    public float Price;
    public String ShortDescription;
    public int Stock;
    public Boolean Featured;
    public float Weight;
    public String Picture1;
    public String Picture2;
    public int SubCategory_idSubCategory;
    private Bitmap img;
    public boolean product_purchased = false; //Set if the product already was ordered. Default: false


    public boolean isProduct_purchased() {
        return product_purchased;
    }

    public void setProduct_purchased(boolean product_purchased) {
        this.product_purchased = product_purchased;
    }


    //public Date DateTimeRegister;


    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }
    public int getIdProduct() {
        return idProduct;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public void setShortDescription(String shortDescription) {
        ShortDescription = shortDescription;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public void setFeatured(Boolean featured) {
        Featured = featured;
    }

    public void setWeight(float weight) {
        Weight = weight;
    }

    public void setPicture1(String picture1) {
        Picture1 = picture1;
    }

    public void setPicture2(String picture2) {
        Picture2 = picture2;
    }

    public void setSubCategory_idSubCategory(int subCategory_idSubCategory) {
        SubCategory_idSubCategory = subCategory_idSubCategory;
    }
    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }


    //public void setDateTimeRegister(Date dateTimeRegister) {     DateTimeRegister = dateTimeRegister;    }





    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public float getPrice() {
        return Price;
    }

    public String getShortDescription() {
        return ShortDescription;
    }

    public int getStock() {
        return Stock;
    }

    public Boolean getFeatured() {
        return Featured;
    }

    public float getWeight() {
        return Weight;
    }

    public String getPicture1() {
        return Picture1;
    }

    public String getPicture2() {
        return Picture2;
    }

    public int getSubCategory_idSubCategory() {
        return SubCategory_idSubCategory;
    }

    //public Date getDateTimeRegister() {        return DateTimeRegister;    }

    public DAOProduct getDAO(){
        return new DAOProduct(this);
    }


}
