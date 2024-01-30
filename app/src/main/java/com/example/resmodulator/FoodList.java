package com.example.resmodulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Common.Common;
import Interface.ItemClickListener;
import Model.Food;
import ViewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    SwipeRefreshLayout swipeRefreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        // instantiate firebase
        database= FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);

        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
                );
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() &&  categoryId != null){

                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);
                    else
                    {
                        Toast.makeText(FoodList.this, "Please check your Internet Connection!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(getIntent() != null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() &&  categoryId != null){

                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);
                    else
                    {
                        Toast.makeText(FoodList.this, "Please check your Internet Connection!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        //Get Intent here





    }

    private void loadListFood(String categoryId) {

        adapter= new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item, FoodViewHolder.class,
                // this code below is like select* from foods where
                foodList.orderByChild("menuID").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {

                foodViewHolder.food_name.setText(food.getName());
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(foodViewHolder.food_image);
                final Food local = food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                       //Start new activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        // sending food id to a new activity
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });

            }
        };
        //set adapter
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}