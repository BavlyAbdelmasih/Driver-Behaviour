package com.example.uttam.driver_behaviour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Yashwanth on 11/14/17.
 */

public class UserLoginPage extends AppCompatActivity {

    private EditText mEmail,mPassword;
    private Button btnLogin;
    FirebaseDatabase database;
    DatabaseReference LoginRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        database = FirebaseDatabase.getInstance();
        LoginRef = database.getReference("Users");
        mEmail = (EditText) findViewById(R.id.usernamelogin);
        mPassword = (EditText) findViewById(R.id.passwordlogin);
        btnLogin = (Button)findViewById(R.id.logIn);

    }



    private void mainMenu() {

    }

    public void Login() {
        //Toast.makeText(getApplicationContext(),"yup",Toast.LENGTH_SHORT).show();
        final String userid = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        if (userid.isEmpty()) {
            mEmail.setError("Username cannot be blank");
        } else if (password.isEmpty()) {
            mPassword.setError("Password cannot be blank");
        } else {
            LoginRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(userid)) {
                        String pass = dataSnapshot.child(userid).child("Password").getValue().toString();
                        String UserN = dataSnapshot.child(userid).child("UserName").getValue().toString();

                        if (password.equals(pass)) {

                            mPassword.setError(null);
                            Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(UserLoginPage.this,MainMenu.class);
                            i.putExtra("userid",UserN);
                            startActivity(i);
                            LoginRef.removeEventListener(this);
                        } else {
                            mPassword.setError("Enter Correct Password");
                        }
                    } else {
                        //
                        mEmail.setError("Enter Correct Email");
                    }
                }



                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),databaseError.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void signini(View view) {

        Login();
    }
}

