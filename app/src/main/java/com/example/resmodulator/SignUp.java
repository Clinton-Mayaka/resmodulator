package com.example.resmodulator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class SignUp extends AppCompatActivity {

    EditText edtName,edtTableNo,edtPassword,edtSecureCode;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtPassword= (MaterialEditText)findViewById(R.id.edtPassword);
        edtTableNo= (MaterialEditText)findViewById(R.id.edtTableNo);
        edtSecureCode= (MaterialEditText)findViewById(R.id.edtSecureCode);

        btnSignUp=(FButton)findViewById(R.id.btnSignUp);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_table = database.getReference("Table");


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();

                    table_table.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            //check if user staff no already exists
                            if (dataSnapshot.child(edtTableNo.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(SignUp.this, "Table Already Registered!!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else
                                mDialog.dismiss();
                            Table table = new Table(edtName.getText().toString(),
                                    edtPassword.getText().toString(),
                                    edtSecureCode.getText().toString());
                            table_table.child(edtTableNo.getText().toString()).setValue(table);
                            Toast.makeText(SignUp.this, "Sign Up Successful!!", Toast.LENGTH_LONG).show();
                            finish();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(SignUp.this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}