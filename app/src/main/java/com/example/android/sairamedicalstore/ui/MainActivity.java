package com.example.android.sairamedicalstore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.android.sairamedicalstore.R;
import com.example.android.sairamedicalstore.SairaMedicalStoreApplication;
import com.example.android.sairamedicalstore.models.DisplayProduct;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.models.item;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BaseSliderView.OnSliderClickListener {

    FloatingActionButton fab;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar;

    ImageView mImageviewProfilePic;
    TextView mTextViewUserNickName;
    LinearLayout mLinearLayoutForRecyclerView;
    Button mButtonAddNewMedicine;
    EditText mEditTextSearchMedicine;
    private SliderLayout mDemoSlider;

    User mCurrentUser;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<item> mArrayListDataSet;
    private ArrayList<DisplayProduct> mArrayListDisplayProduct;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        /*HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal",R.drawable.hannibal);
        file_maps.put("Big Bang Theory",R.drawable.bigbang);
        file_maps.put("House of Cards",R.drawable.house);
        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);*/

        for(String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);
                    //.setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }

        setDisplayCategories();

        mEditTextSearchMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE);
                startActivity(searchActivity);
            }
        });

    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeScreen()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditTextSearchMedicine = (EditText) findViewById(R.id.edit_text_search);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView =  navigationView.getHeaderView(0);
        mImageviewProfilePic = (ImageView) headerView.findViewById(R.id.image_view_profile_pic);
        setProfilePic();

        mTextViewUserNickName = (TextView)headerView.findViewById(R.id.text_view_user_nickname);
        mTextViewUserNickName.setText("Hi, "+Utils.getFirstName(mCurrentUser.getName())+"!");

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        mButtonAddNewMedicine = (Button) headerView.findViewById(R.id.button_add_new_medicine);
        mLinearLayoutForRecyclerView = (LinearLayout) findViewById(R.id.linear_layout_for_recycler_view);
        mArrayListDisplayProduct = new ArrayList<DisplayProduct>();
    }

    private void setProfilePic()
    {
        Glide.with(mImageviewProfilePic.getContext())
                .load(mCurrentUser.getPhotoURL())
                .into(mImageviewProfilePic);
    }

    public void onClick(View v)
    {
        Intent intentAddNewMedicine = new Intent(MainActivity.this, AddNewMedicine.class);
        startActivity(intentAddNewMedicine);
    }

    public void onCreateNewOfferClick(View v)
    {
        Intent intentCreateNewOffer = new Intent(MainActivity.this, CreateOrUpdateOfferActivity.class);
        startActivity(intentCreateNewOffer);
    }

    public void onSearchPosterClick()
    {
        Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
        searchActivity.putExtra("whatToSearch",Constants.SEARCH_POSTER);
        startActivity(searchActivity);
    }


    public void setDisplayCategories()
    {
        Firebase firebaseAllDisplayRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY);
        firebaseAllDisplayRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        RecyclerView recyclerView = new RecyclerView(MainActivity.this);
                        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));


                        ArrayList<DisplayProduct> arrayListDisplayProducts = new ArrayList<DisplayProduct>();
                        for (DataSnapshot snapshot2: snapshot.getChildren()) {
                            DisplayProduct product = snapshot2.getValue(DisplayProduct.class);
                            arrayListDisplayProducts.add(product);
                        }

                        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(layoutManager);
                        RecyclerView.Adapter adapter = new DisplayProductsAdapter(arrayListDisplayProducts,MainActivity.this);
                        recyclerView.setAdapter(adapter);

                        mLinearLayoutForRecyclerView.addView(recyclerView);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void setPosterSliders()
    {

    }

}
