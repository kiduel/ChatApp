package com.example.android.firechatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    List<Message> messages = new ArrayList<Message>();
    MessageAdapter messageAdapter;
    @BindView(R.id.rv_text)
    RecyclerView rv_text;
    @BindView(R.id.cameraButton)
    ImageView camera_button;
    @BindView(R.id.takePhotoButton)
    ImageView take_photo_butoon;
    @BindView(R.id.message_textEdit)
    EditText messages_editText;
    @BindView(R.id.send_button)
    Button sendButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                SingOut();
        }
        return true;
    }

    private void SingOut() {
        AuthUI.getInstance().signOut(this);
    }

    FirebaseDatabase database;   //Database
    DatabaseReference myRef;     // Part of the db we want to ref
    ChildEventListener childEventListener; //To listen when there are chanages in the ref

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener; //To listen when there is auth changes
    private static final int RC_SIGN_IN = 123;

    public static String username = "ANONYMOUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        messageAdapter = new MessageAdapter(this, messages);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();     //get instance for the auth
        myRef = database.getReference().child("Message");

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_text.setAdapter(messageAdapter);
        rv_text.setLayoutManager(linearLayoutManager);

        messages_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 1) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Message message = new Message(username, messages_editText.getText().toString().trim(), "null");
                // Clear input box
                messages_editText.setText("");
                myRef.push().setValue(message);
            }
        });


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null){
                    signInInitialize(firebaseUser.getDisplayName());
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


    }

    //This will happen as soon as users are signed in
    // - We want to get the username
    // - We want to perform read and attach the listner
    private void signInInitialize(String displayName) {
        username = displayName;
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        //This will create a listener and then attach it to our ref
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message received_message = dataSnapshot.getValue(Message.class);
                    messages.add(received_message);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            myRef.addChildEventListener(childEventListener);
        }

    }

    public void detachDatabaseListener() {
        if ( childEventListener != null ) {
            myRef.removeEventListener(childEventListener);
            childEventListener = null;
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Some one has signed in", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED ) {
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
     }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseListener();
    }

    public void onSignedOutCleanup(){
        detachDatabaseListener();
    }

}
