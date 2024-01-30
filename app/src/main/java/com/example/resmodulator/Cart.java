package com.example.resmodulator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Common.Common;
import Database.Database;
import Model.MyResponse;
import Model.Notification;
import Model.Order;
import Model.Request;
import Model.Sender;
import Model.Token;
import Remote.APIService;
import ViewHolder.CartAdapter;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    APIService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Init Service
        mService = Common.getFCMService();

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView= (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create a new request

                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your Cart is empty", Toast.LENGTH_SHORT).show();



            }
        });

        loadListFood();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Almost there!");
        alertDialog.setMessage("Enter your Phone Number: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_comment,null);
        MaterialEditText edtPhone = (MaterialEditText)order_address_comment.findViewById(R.id.edtPhone);
        MaterialEditText edtComment = (MaterialEditText)order_address_comment.findViewById(R.id.edtComment);

        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Request request = new Request(
                        Common.currentTable.getTableNo(),
                        Common.currentTable.getName(),
                        edtPhone.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        "0",
                        edtComment.getText().toString(),
                        cart
                );

                //Submit to firebase
                String order_number =String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);
                // clear cart after order placement
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);




                Toast.makeText(Cart.this, "Thank you, Your Order has been Placed!", Toast.LENGTH_SHORT).show();
              finish();


            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void sendNotificationOrder(String order_number) {
        DatabaseReference tokens  = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    //Create raw payload to send
                    Notification notification= new Notification("The ResModulator","You have  new Order"+order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() ==  200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Thank you, Your Order has been Placed!", Toast.LENGTH_SHORT).show();
                                            finish();

                                        } else {
                                            Toast.makeText(Cart.this, "Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                    Log.e("ERROR", t.getMessage());

                                }
                            });



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListFood() {
        cart =  new Database(this).getCarts();

        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        // calculating the total price for items in the cart

        int total =0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale = new Locale("en", "KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
                deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //removing item from order list by position
        cart.remove(position);
        // delete all old data from SQLlite
        new Database(this).cleanCart();
        //update new data
        for(Order item:cart)
                new Database(this).addToCart(item);

        loadListFood();

    }
}