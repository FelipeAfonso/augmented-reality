package codswork.ifspra;

/**
 * Created by Juliano on 16/02/2016.
 */

import java.util.List;

import codswork.ifspra.pojo.Product;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;


public interface RestInterface {

        @GET("/WebService/ProductList")
        void getWSRestProduct(Callback<List<Product>> response);

        @FormUrlEncoded
        @POST("/WebServicePHP/InsertOrderFinalizedJson.php")
        void insertUserJson(@Field("json") String json, Callback<Response> callBack);


        @FormUrlEncoded
        @POST("/WebServicePHP/AuthenticationClient.php")
        void insertUserAuthentication(@Field("json") String json, Callback<Response> callBack);


}
