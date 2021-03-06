package com.gamecockmobile;

import java.util.ArrayList;

import com.gamecockmobile.buses.BusesFragment;
import com.gamecockmobile.events.EventsFragment;
import com.gamecockmobile.news.NewsFragment;
import com.gamecockmobile.service.CourseDatabaseService;
import com.gamecockmobile.social.SocialFragment;
import com.gamecockmobile.util.LPreviewUtils;
import com.gamecockmobile.util.UIUtils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
//import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This is the main class for the app that runs the dashboard for all of the resources.
 */
public class MainActivity extends ActionBarActivity {

    //private LPreviewUtils.ActionBarDrawerToggleWrapper mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    private Intent mServiceIntent;

    // slide menu items
    private String[] navMenuTitles;

    Toolbar mToolbar;

    protected static final int NAVDRAWER_ITEM_COURSE_LIST = 0;
    protected static final int NAVDRAWER_ITEM_EVENTS = 1;
    protected static final int NAVDRAWER_ITEM_BUSES = 2;
    protected static final int NAVDRAWER_ITEM_SOCIAL = 3;
    protected static final int NAVDRAWER_ITEM_NEWS = 4;

    // fade in and fade out durations for the main content when switching between different Fragmnets of the app throught the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 350;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // deleteDatabase("CoursesManager.db");
        super.onCreate(savedInstanceState);

        System.out.println("Starting...");
        setContentView(R.layout.activity_main);

//        mServiceIntent = new Intent(this, CourseDatabaseService.class);
//        this.startService(mServiceIntent);
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.schedule_action_list);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<NavDrawerItem>();

        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }


        // adding nav drawer items to array
        // Schedule
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAVDRAWER_ITEM_COURSE_LIST], R.drawable.ic_list_grey));
        // Events
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAVDRAWER_ITEM_EVENTS], R.drawable.ic_today_grey));
        // Buses
        navDrawerItems.add(new NavDrawerItem("selector", R.drawable.ic_drawer_social));
        // Social
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAVDRAWER_ITEM_SOCIAL], R.drawable.ic_people_grey));
        // News
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[NAVDRAWER_ITEM_NEWS], R.drawable.ic_trending_up_grey));

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        ///////getActionBar().setDisplayHomeAsUpEnabled(true);
        ///////getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, // nav menu
                // toggle
                // icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                /////getActionBar().setTitle(mTitle);
                mToolbar.setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                mToolbar.setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }
        getSupportActionBar().setTitle(navMenuTitles[0]);
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
//        // Handle action bar actions click
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                return true;
//            default:
                return super.onOptionsItemSelected(item);
//        }

    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void goToNavDrawerItem(int item) {
        // update the main content by replacing fragments
        Fragment fragment = null;

        switch (item) {
            case NAVDRAWER_ITEM_COURSE_LIST:
                fragment = new CourseListFragment();
                break;
            case NAVDRAWER_ITEM_EVENTS:
                fragment = new EventsFragment();
                break;
            case NAVDRAWER_ITEM_BUSES:
                fragment = new BusesFragment();
                break;
            case NAVDRAWER_ITEM_SOCIAL:
                fragment = new SocialFragment();
                break;
            case NAVDRAWER_ITEM_NEWS:
                fragment = new NewsFragment();
            default:
                break;
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        View mainContent = findViewById(R.id.frame_container);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }
    }

    private void setSelectedNavDrawerItem(int itemId) {
        mDrawerList.setItemChecked(itemId, true);
        mDrawerList.setSelection(itemId);

        setTitle(navMenuTitles[itemId]);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(final int position) {
        // Launch the target Activity after a short delay, to allow the close animation to play
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                goToNavDrawerItem(position);
            }
        }, NAVDRAWER_LAUNCH_DELAY);

        // change the active item on the list so the user can see the item changed
        setSelectedNavDrawerItem(position);

        // fade out the main content
        View mainContent = findViewById(R.id.frame_container);
        if (mainContent != null) {
            mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
        }

        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        mToolbar.setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during onPostCreate() and
     * onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}