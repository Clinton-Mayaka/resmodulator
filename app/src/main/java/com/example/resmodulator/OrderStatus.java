package com.example.resmodulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Common.Common;
import Model.Request;
import ViewHolder.OrderViewHolder;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // load order by phone

        if(getIntent() == null)
            loadOrders(Common.currentTable.getTableNo());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String TableNo) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,R.layout.order_layout,OrderViewHolder.class,
                requests.orderByChild("TableNo")
                .equalTo(TableNo)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, Request request, int position) {
                orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(request.getStatus()));
                orderViewHolder.txtOrderPhone.setText(request.getPhone_number());
                orderViewHolder.txtOrderTableNo.setText(request.getTableNo());
                orderViewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));

            }
        };
        recyclerView.setAdapter(adapter);
    }

    // 0 default status 1 placed 2 ready 3 on the way


}