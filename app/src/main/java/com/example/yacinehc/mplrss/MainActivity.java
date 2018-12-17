package com.example.yacinehc.mplrss;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yacinehc.mplrss.db.AccesDonnees;
import com.example.yacinehc.mplrss.itemDetails.ItemDetailsFragment;
import com.example.yacinehc.mplrss.itemDetails.WebViewFragment;
import com.example.yacinehc.mplrss.model.RSS;
import com.example.yacinehc.mplrss.model.RssItem;
import com.example.yacinehc.mplrss.rssItems.RssItemsAdapter;
import com.example.yacinehc.mplrss.rssItems.RssItemsFragment;
import com.example.yacinehc.mplrss.utils.MyParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity
        implements Observer, Serializable,
        CustomCursorRecyclerViewAdapter.OnItemClickListener,
        RssItemsAdapter.OnRssItemSelected,
        ItemDetailsFragment.OnFragmentInteractionListener{

    transient private MenuItem deleteAction;
    private MyObservable myObsarvable;
    private int checkedItemsCount;
    private RssListFragment rssListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myObsarvable = new MyObservable();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            rssListFragment = new RssListFragment();
            fragmentTransaction.add(R.id.feedListFrameLayout, rssListFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("myObsarvable", this.myObsarvable);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        deleteAction = menu.findItem(R.id.action_delete);
        deleteAction.setVisible((checkedItemsCount != 0));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_delete) {
            myObsarvable.setChanged();
            myObsarvable.notifyObservers(new String("deleteAll"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public MyObservable getMyObsarvable() {
        return myObsarvable;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof CustomCursorRecyclerViewAdapter.MyObsarvable) {
            checkedItemsCount = (Integer) arg;
            if (deleteAction != null) {
                if (checkedItemsCount == 0) {
                    deleteAction.setVisible(false);
                } else {
                    deleteAction.setVisible(true);
                }
            }
        }
    }

    @Override
    public void displayRssItems(RSS rss) {
        AccesDonnees accesDonnees = new AccesDonnees(this);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ArrayList<RssItem> rssItems = (ArrayList<RssItem>) accesDonnees.getRssItems(rss);
        RssItemsFragment rssItemsFragment = RssItemsFragment.newInstance(rssItems, rss);
        fragmentTransaction.replace(R.id.feedListFrameLayout, rssItemsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public class MyObservable extends Observable implements Serializable {
        public void setChanged() {
            super.setChanged();
        }
    }

    @Override
    public void selectItem(RssItem rssItem) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ItemDetailsFragment itemDetailsFragment = ItemDetailsFragment.newInstance(rssItem);
        fragmentTransaction.replace(R.id.feedListFrameLayout, itemDetailsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(RssItem rssItem) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        WebViewFragment webViewFragment = WebViewFragment.newInstance(rssItem.getLink());
        fragmentTransaction.replace(R.id.feedListFrameLayout, webViewFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
