package codswork.ifspra.pojo;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by Juliano on 11/01/2016.
 */
public class MessageBox {


    public static void showInfo(Context ctx, String title, String msg, int idIcon){ //Método static pode ser chamada sem criar novas instancia, ex.: new Classe

        show(ctx, title, msg, android.R.drawable.ic_dialog_info);

    }


    public static void showAlert(Context ctx, String title, String msg, int idIcon){ //Método static pode ser chamada sem criar novas instancia, ex.: new Classe

        show(ctx, title, msg, android.R.drawable.ic_dialog_alert);

    }

    public static void show(Context ctx, String title, String msg){

        show(ctx, title, msg, 0);


    }


    public static void show(Context ctx, String title, String msg, int idIcon){ //Método static pode ser chamada sem criar novas instancia, ex.: new Classe

        AlertDialog.Builder dlg = new AlertDialog.Builder(ctx);
        dlg.setIcon(idIcon);
        dlg.setTitle(title);
        dlg.setMessage(msg);
        dlg.setNeutralButton("Ok", null);
        dlg.show();
    }


}
