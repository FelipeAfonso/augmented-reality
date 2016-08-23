package codswork.ifspra.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import codswork.ifspra.Controller;
import codswork.ifspra.R;
import codswork.ifspra.adapters.ProductAdapter;

/**
 * Created by Felipe on 12/07/2016.
 */
public class MenuFragment extends Fragment {

    View myView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //if(Controller.isFastBuyChecked)
        //    menu.findItem(R.id.check_buy).setChecked(true);
        //if(Controller.isFastRemChecked)
        //    menu.findItem(R.id.check_rem).setChecked(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView  = inflater.inflate(R.layout.menu_layout, container, false);
        //setHasOptionsMenu(true);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Cardápio");

        ProductAdapter adapter = new ProductAdapter(
                getActivity(),
                R.layout.product_item_file,
                Controller.ProductsList);

        ListView lv = (ListView)getView().findViewById(R.id.menu_list);
        lv.setAdapter(adapter);

    }
}
