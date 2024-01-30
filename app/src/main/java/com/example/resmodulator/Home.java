package com.example.resmodulator;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import Common.Common;
import Interface.ItemClickListener;
import Model.Category;
import Model.Token;
import ViewHolder.MenuViewHolder;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    FirebaseDatabase database;
    DatabaseReference category;

    TextView txtFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Menu");

        //view
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
                );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else {
                    Toast.makeText(getBaseContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }



            }
        });

        //by default load for first time.
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadMenu();
                else {
                    Toast.makeText(getBaseContext(), "Please Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        //initiate firebase
        database= FirebaseDatabase.getInstance();
        category= database.getReference("Category");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent cartIntent = new Intent(Home.this,Cart.class);
               startActivity(cartIntent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,0,0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView =(NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //set name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView)headerView.findViewById(R.id.txtFullName);
        txtFullName.setText(Common.currentTable.getName());

        //load menu
        recycler_menu=(RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);



        updateToken(FirebaseInstanceId.getInstance().getToken());







        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
       // mAppBarConfiguration = new AppBarConfiguration.Builder(
              //  R.id.nav_menu, R.id.nav_cart, R.id.nav_orders, R.id.nav_tables, R.id.nav_myProfile, R.id.nav_payments, R.id.nav_logout)
             //   .setDrawerLayout(drawer)
               // .build();
     // NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
    // NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
    // NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,false);// is false because the token is sent from  Client side application
        tokens.child(Common.currentTable.getTableNo()).setValue(data);
    }

    private void loadMenu() {
        adapter= new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class,R.layout.menu_item,MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder menuViewHolder, Category category, int i) {
                menuViewHolder.txtMenuName.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getImage())
                        .into(menuViewHolder.imageView);
                Category clickItem = category;
                menuViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       //get category id and send to new activity
                        Intent foodList = new Intent(Home.this, FoodList.class);
                        //since categoryId is they key , we just get the key of this item
                        foodList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foodList);
                    }
                });

            }
        };
        recycler_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh)
                loadMenu();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu){

        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(Home.this,Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_orders){
            Intent orderIntent = new Intent(Home.this,OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_tables){

        }else if (id == R.id.nav_myProfile){

        }else if (id == R.id.nav_payments){

        }else if (id == R.id.nav_logout){
            Intent signIn = new Intent(Home.this,SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);

        }
        return false;
    }


    // @Override
  // public boolean onSupportNavigateUp() {
      //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
      // return NavigationUI.navigateUp(navController, mAppBarConfiguration)
             //|| super.onSupportNavigateUp();
 // }
}