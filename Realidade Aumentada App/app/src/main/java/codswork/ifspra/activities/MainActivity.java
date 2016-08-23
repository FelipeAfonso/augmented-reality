package codswork.ifspra.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

import codswork.ifspra.Controller;
import codswork.ifspra.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Util", "Other Activity: " + Calendar.getInstance().getTime().toString());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mTitle = "IFSP RA Restaurant";

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);






    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (Controller.active_id != -2) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FillFragment()).commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if(Controller.isFastBuyChecked)
            menu.findItem(R.id.check_buy).setChecked(true);
        if(Controller.isFastRemChecked)
            menu.findItem(R.id.check_rem).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        if (id == R.id.check_buy){
            if(item.isChecked()){

                item.setChecked(false);
            }else{
                Controller.isFastBuyChecked = true;
                item.setChecked(true);
            }
            Controller.saveCount(MainActivity.this);
        }else if(id==R.id.check_rem){
            if(item.isChecked()){
                Controller.isFastRemChecked = false;
                item.setChecked(false);
            }else{
                Controller.isFastRemChecked = true;
                item.setChecked(true);
            }
            Controller.saveCount(MainActivity.this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new FillFragment()).commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Controller.active_id = item.getItemId();

        android.app.FragmentManager fragmentManager = getFragmentManager();

        if (Controller.active_id == R.id.nav_menu_layout) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new MenuFragment())
                    .commit();
        } else if (Controller.active_id == R.id.nav_cart_layout) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new CartFragment())
                    .commit();
        }else if (Controller.active_id == R.id.nav_ra_layout) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new RaFragment())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //if(Controller.getCount(MainActivity.this) != Ordered.id_count) {
        Controller.saveCount(MainActivity.this);
    }
}

