package com.example.resmodulator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Common.Common;
import Model.Table;
import Model.User;
import info.hoang8f.widget.FButton;

public class SignIn extends AppCompatActivity {

    EditText edtTableNo,edtPassword;
    Button btnSignIn;
    TextView txtForgotPwd;
    FirebaseDatabase database;
    DatabaseReference table_table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword=(MaterialEditText)findViewById(R.id.edtPassword);
        edtTableNo=(MaterialEditText)findViewById(R.id.edtTableNo);
        btnSignIn=(FButton)findViewById(R.id.btnSignIn);
        txtForgotPwd = (TextView)findViewById(R.id.txtForgotPwd);

        //init firebase

         database = FirebaseDatabase.getInstance();
         table_table = database.getReference("Table");

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())){

               final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();
                table_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //check if user does not exist in database

                        if(dataSnapshot.child(edtTableNo.getText().toString()).exists()) {


                            //get user information
                            mDialog.dismiss();
                            Table table = dataSnapshot.child(edtTableNo.getText().toString()).getValue(Table.class);
                            table.setTableNo(edtTableNo.getText().toString());
                            if (table.getPassword().equals(edtPassword.getText().toString())){

                                //Toast.makeText(SignIn.this, "Signed in successfully!!", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(SignIn.this,Home.class);
                                Common.currentTable= table;
                                startActivity(homeIntent);
                                finish();

                            } else {
                                Toast.makeText(SignIn.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else{
                            mDialog.dismiss();
                            Toast.makeText(SignIn.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
                else
                {
                    Toast.makeText(SignIn.this, "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your Secure Code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view =inflater.inflate(R.layout.forgot_password_layout,null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_baseline_security_24);

        MaterialEditText edtTableNo= (MaterialEditText)forgot_view.findViewById(R.id.edtTableNo);
        MaterialEditText edtSecureCode= (MaterialEditText)forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                table_table.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Table table = dataSnapshot.child(edtTableNo.getText().toString())
                                .getValue(Table.class);
                        if(table.getSecureCode().equals(edtSecureCode.getText().toString()))
                            Toast.makeText(SignIn.this, "Your password: "+table.getPassword(), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(SignIn.this, "Wrong Secure Code!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

}