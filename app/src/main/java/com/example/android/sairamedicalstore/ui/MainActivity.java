package com.example.android.sairamedicalstore.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
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
import com.example.android.sairamedicalstore.models.DefaultKeyValuePair;
import com.example.android.sairamedicalstore.models.DisplayProduct;
import com.example.android.sairamedicalstore.models.Poster;
import com.example.android.sairamedicalstore.models.User;
import com.example.android.sairamedicalstore.ui.cart.CartActivity;
import com.example.android.sairamedicalstore.ui.customerSupport.CustomerSupportActivity;
import com.example.android.sairamedicalstore.ui.login.LoginActivity;
import com.example.android.sairamedicalstore.ui.poster.PosterDetailsActivity;
import com.example.android.sairamedicalstore.ui.prescription.MyPrescriptionsActivity;
import com.example.android.sairamedicalstore.ui.profile.MyProfileActivity;
import com.example.android.sairamedicalstore.ui.search.SearchActivity;
import com.example.android.sairamedicalstore.utils.Constants;
import com.example.android.sairamedicalstore.utils.Utils;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.auth.AuthUI;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,BaseSliderView.OnSliderClickListener {

    DrawerLayout mDrawer;
    NavigationView navigationView;
    Toolbar toolbar;

    ImageView mImageviewProfilePic,mImageViewCart;
    TextView mTextViewUserNickName,mTextViewOptionalMessage;
    LinearLayout mLinearLayoutForRecyclerView;
    LinearLayout mLinearLayoutManageUsers,mLinearLayoutManageOffers,mLinearLayoutManagePosters,mLinearLayoutDeliverableAddresses,
            mLinearLayoutAllOrders,mLinearLayoutEvaluatePrescriptions,mLinearLayoutDefaultMedicinePics;

    EditText mEditTextSearchMedicine;
    private SliderLayout mDemoSlider;
    MenuItem menuItemMyOrders,menuItemMyPrescriptions;

    User mCurrentUser;

    Firebase mFirebaseAllPostersRef,mFirebaseCurrentUserRef;
    ArrayList<DefaultKeyValuePair> mArrayListCommonDefaultValues;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCurrentUser = ((SairaMedicalStoreApplication) this.getApplication()).getCurrentUser();

        initializeScreen();

        mArrayListCommonDefaultValues = ((SairaMedicalStoreApplication) this.getApplication()).getArrayListCommonDefaultValues();


        mFirebaseCurrentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mCurrentUser = dataSnapshot.getValue(User.class);
                    if(mCurrentUser == null)
                        finish();

                    setProfilePic();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        setPosterSliders();

        mEditTextSearchMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE);
                startActivity(searchActivity);
            }
        });

        mImageviewProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToMyProfileActivity = new Intent(MainActivity.this, MyProfileActivity.class);
                startActivity(intentToMyProfileActivity);
            }
        });

        displayAccordingToUserType();

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

        if (id == R.id.nav_my_orders) {
            Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
            searchActivity.putExtra("whatToSearch",Constants.SEARCH_ORDER);
            startActivity(searchActivity);
        }
        else if (id == R.id.nav_my_profile) {
            Intent intentToMyProfileActivity = new Intent(MainActivity.this, MyProfileActivity.class);
            startActivity(intentToMyProfileActivity);
        }
        else if (id == R.id.nav_customer_support) {
            Intent intentToCustomerSupportActivity = new Intent(MainActivity.this, CustomerSupportActivity.class);
            startActivity(intentToCustomerSupportActivity);
        }
        else if (id == R.id.nav_sign_out) {
            AuthUI.getInstance().signOut(this);
            Intent intentToLoginActivity = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentToLoginActivity);
        }
        else if (id == R.id.nav_prescription_medicine) {
            Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
            searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE);
            searchActivity.putExtra("searchOrderBy",Constants.FIREBASE_PROPERTY_MEDICINE_CATEGORY);
            searchActivity.putExtra("specialSearch",Constants.MEDICINE_CATEGORY_PRESCRIPTION);
            startActivity(searchActivity);
        }
        else if (id == R.id.nav_over_the_counter_medicine) {
            Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
            searchActivity.putExtra("whatToSearch",Constants.SEARCH_MEDICINE);
            searchActivity.putExtra("searchOrderBy",Constants.FIREBASE_PROPERTY_MEDICINE_CATEGORY);
            searchActivity.putExtra("specialSearch",Constants.MEDICINE_CATEGORY_OTC);
            startActivity(searchActivity);
        }
        else if (id == R.id.nav_my_prescriptions) {
            Intent myPrescriptionsActivityIntent = new Intent(MainActivity.this,MyPrescriptionsActivity.class);
            myPrescriptionsActivityIntent.putExtra("activityVisitPurpose",Constants.ACTIVITY_VISIT_PURPOSE_VISIT);
            startActivity(myPrescriptionsActivityIntent);
        }
        else if (id == R.id.nav_share) {

            if (mArrayListCommonDefaultValues != null && mArrayListCommonDefaultValues.size() > 0) {

                for (DefaultKeyValuePair keyValuePair :mArrayListCommonDefaultValues) {
                    if (keyValuePair.getKey().equals("Logo"))
                    {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, R.drawable.logo2);
                        shareIntent.setType("image/jpeg");
                        startActivity(Intent.createChooser(shareIntent, "Share images to.."));
                        break;
                    }
                }

            }
        }
        /*else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }  else if (id == R.id.nav_send) {

        }*/


        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeScreen()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditTextSearchMedicine = (EditText) findViewById(R.id.edit_text_search);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView =  navigationView.getHeaderView(0);
        mImageviewProfilePic = (ImageView) headerView.findViewById(R.id.image_view_profile_pic);

        mTextViewUserNickName = (TextView)headerView.findViewById(R.id.text_view_user_nickname);
        mTextViewUserNickName.setText("Hi, "+Utils.getFirstName(mCurrentUser.getName())+"!");

        mImageViewCart = (ImageView) findViewById(R.id.image_view_cart);

        mTextViewOptionalMessage = (TextView)headerView.findViewById(R.id.text_view_optional_message);

        mDemoSlider = (SliderLayout)findViewById(R.id.slider);

        mLinearLayoutForRecyclerView = (LinearLayout) findViewById(R.id.linear_layout_for_recycler_view);

        mFirebaseAllPostersRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_POSTERS);
        mFirebaseCurrentUserRef = new Firebase(Constants.FIREBASE_URL_SAIRA_ALL_USERS).child(Utils.encodeEmail(mCurrentUser.getEmail()));

        mLinearLayoutManageUsers = (LinearLayout) findViewById(R.id.linear_layout_manage_users);
        mLinearLayoutManageOffers = (LinearLayout) findViewById(R.id.linear_layout_manage_offers);
        mLinearLayoutManagePosters = (LinearLayout) findViewById(R.id.linear_layout_manage_posters);
        mLinearLayoutDeliverableAddresses = (LinearLayout) findViewById(R.id.linear_layout_manage_deliverable_addresses);
        mLinearLayoutAllOrders = (LinearLayout) findViewById(R.id.linear_layout_all_orders);
        mLinearLayoutEvaluatePrescriptions = (LinearLayout) findViewById(R.id.linear_layout_evaluate_prescriptions);
        mLinearLayoutDefaultMedicinePics = (LinearLayout) findViewById(R.id.linear_layout_manage_default_medicine_pics);

        mArrayListCommonDefaultValues = new ArrayList<>();

        Menu menu = navigationView.getMenu();
        MenuItem Item= menu.getItem(1);

        SubMenu subMenuPersonalInfo = Item.getSubMenu();
        for (int menuItemIndex = 0; menuItemIndex < subMenuPersonalInfo.size(); menuItemIndex++) {
            MenuItem menuItem = subMenuPersonalInfo.getItem(menuItemIndex);


            if(menuItem.getItemId() == R.id.nav_my_orders)
                menuItemMyOrders = menuItem;
            else if(menuItem.getItemId() == R.id.nav_my_prescriptions)
                menuItemMyPrescriptions = menuItem;
        }


    }

    private void setProfilePic()
    {
        Glide.with(mImageviewProfilePic.getContext())
                .load(mCurrentUser.getPhotoURL())
                .into(mImageviewProfilePic);
    }


    public void displayAccordingToUserType()
    {
        if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_END_USER)) {

            //---------------------menu items---------------------
            menuItemMyOrders.setVisible(true);
            menuItemMyPrescriptions.setVisible(true);
            mTextViewOptionalMessage.setVisibility(View.GONE);

            //---------------------main activity items------------------
            mImageViewCart.setVisibility(View.VISIBLE);
            setDisplayCategories();


            mImageViewCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cartIntent = new Intent(MainActivity.this,CartActivity.class);
                    startActivity(cartIntent);
                }
            });
        }
        else if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_OWNER)) {

            //---------------------menu items---------------------
            menuItemMyOrders.setVisible(false);
            menuItemMyPrescriptions.setVisible(false);

            //---------------------main activity items------------------
            mTextViewOptionalMessage.setVisibility(View.VISIBLE);
            mTextViewOptionalMessage.setText(mCurrentUser.getUserType());

            mImageViewCart.setVisibility(View.GONE);

            setManageUsersListener();
            setManageOffersListener();
            setManagePostersListener();
            setAllOrdersListeners();
            setManageEvaluatePrescriptionsListener();
            setManageDeliverableAddressesListener();
            setManageDefaultMedicinePicListener();

        }
        else if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_EMPLOYEE)) {

            //---------------------menu items---------------------
            menuItemMyOrders.setVisible(false);
            menuItemMyPrescriptions.setVisible(false);

            //---------------------main activity items------------------
            mTextViewOptionalMessage.setVisibility(View.VISIBLE);
            mTextViewOptionalMessage.setText(mCurrentUser.getUserType());

            mImageViewCart.setVisibility(View.GONE);

            setManageOffersListener();
            setManagePostersListener();
            setAllOrdersListeners();
            setManageEvaluatePrescriptionsListener();
            setManageDeliverableAddressesListener();
            setManageDefaultMedicinePicListener();


        }
        else if (mCurrentUser.getUserType().equals(Constants.USER_TYPE_DEVELOPER)) {

            //---------------------menu items---------------------
            menuItemMyOrders.setVisible(false);
            menuItemMyPrescriptions.setVisible(false);

            //---------------------main activity items------------------
            mTextViewOptionalMessage.setVisibility(View.VISIBLE);
            mTextViewOptionalMessage.setText(mCurrentUser.getUserType());

            mImageViewCart.setVisibility(View.GONE);
        }
    }


    public void setDisplayCategories()
    {
        Firebase firebaseAllDisplayRef = new Firebase(Constants.FIREBASE_URL_SAIRA_All_DISPLAY);
        firebaseAllDisplayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    mLinearLayoutForRecyclerView.removeAllViews();

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

                        //---------------Linear layout------------------------------
                        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));

                        ViewGroup.MarginLayoutParams marginLayoutParamsForLinearLayout =
                                (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
                        marginLayoutParamsForLinearLayout.setMargins(10,20,10,10);
                        //linearLayout.setBackgroundColor(getResources().getColor(R.color.tw__composer_white));
                        linearLayout.setLayoutParams(marginLayoutParamsForLinearLayout);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);


                        //------------Textview for Display category name-----------------------------
                        TextView mTextViewDisplayCategoryName = new TextView(MainActivity.this);
                        mTextViewDisplayCategoryName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        ViewGroup.MarginLayoutParams marginLayoutParams =
                                (ViewGroup.MarginLayoutParams) mTextViewDisplayCategoryName.getLayoutParams();
                        marginLayoutParams.setMargins(5, 5, 5, 5);
                        mTextViewDisplayCategoryName.setLayoutParams(marginLayoutParams);
                        mTextViewDisplayCategoryName.setTextSize(20);
                        mTextViewDisplayCategoryName.setText(Utils.toLowerCaseExceptFirstLetter(snapshot.getKey()));


                        //------------Recycler view ----------------------------
                        RecyclerView.LayoutManager layoutManager  = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(layoutManager);
                        RecyclerView.Adapter adapter = new DisplayProductsAdapter(arrayListDisplayProducts,MainActivity.this);
                        recyclerView.setAdapter(adapter);

                        linearLayout.addView(mTextViewDisplayCategoryName);
                        linearLayout.addView(recyclerView);



                        mLinearLayoutForRecyclerView.addView(linearLayout);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void setManageUsersListener()
    {
        mLinearLayoutManageUsers.setVisibility(View.VISIBLE);
        mLinearLayoutManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_USERS);
                startActivity(searchActivity);
            }
        });

    }

    public void setManageOffersListener()
    {
        mLinearLayoutManageOffers.setVisibility(View.VISIBLE);
        mLinearLayoutManageOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void setManagePostersListener()
    {
        mLinearLayoutManagePosters.setVisibility(View.VISIBLE);
        mLinearLayoutManagePosters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_POSTER);
                startActivity(searchActivity);
            }
        });

    }

    public void setAllOrdersListeners()
    {
        mLinearLayoutAllOrders.setVisibility(View.VISIBLE);
        mLinearLayoutAllOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_ALL_ORDERS);
                startActivity(searchActivity);
            }
        });

    }

    public void setManageEvaluatePrescriptionsListener()
    {
        mLinearLayoutEvaluatePrescriptions.setVisibility(View.VISIBLE);
        mLinearLayoutEvaluatePrescriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_PRESCRIPTION_FOR_EVALUATION);
                startActivity(searchActivity);
            }
        });

    }

    public void setManageDeliverableAddressesListener()
    {
        mLinearLayoutDeliverableAddresses.setVisibility(View.VISIBLE);
        mLinearLayoutDeliverableAddresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_DELIVERABLE_ADDRESS);
                startActivity(searchActivity);
            }
        });

    }

    public void setManageDefaultMedicinePicListener()
    {
        mLinearLayoutDefaultMedicinePics.setVisibility(View.VISIBLE);
        mLinearLayoutDefaultMedicinePics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchActivity = new Intent(MainActivity.this, SearchActivity.class);
                searchActivity.putExtra("whatToSearch",Constants.SEARCH_DEFAULT_MEDICINE_PICS);
                startActivity(searchActivity);
            }
        });

    }

    public void setPosterSliders() {
        mFirebaseAllPostersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final Poster eachPoster = snapshot.getValue(Poster.class);
                        if (eachPoster != null) {
                            count++;
                            TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                            // initialize a SliderLayout
                            textSliderView
                                    .description(eachPoster.getPosterName())
                                    .image(eachPoster.getPosterImageURI())
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(MainActivity.this);

                            textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent searchActivity = new Intent(MainActivity.this, PosterDetailsActivity.class);
                                    searchActivity.putExtra("posterId", eachPoster.getPosterId());
                                    startActivity(searchActivity);
                                }
                            });
                            //add your extra information
                            /*textSliderView.bundle(new Bundle());
                            textSliderView.getBundle()
                                    .putString("extra", name);*/

                            mDemoSlider.addSlider(textSliderView);
                        }
                    }
                    if (count <= 0)
                        mDemoSlider.setVisibility(View.GONE);
                    else
                        mDemoSlider.setVisibility(View.VISIBLE);
                } else
                    mDemoSlider.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
