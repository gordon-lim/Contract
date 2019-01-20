package com.example.soymilk.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mRootRef.child("condition");
    DatabaseReference mPassRef = mRootRef.child("GameObject").child("passcode");
    TextView view;
    Button joinButton;
    Button createButton;
    EditText passcodeView;
    static String globalPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (TextView)findViewById(R.id.view);
        joinButton = (Button)findViewById(R.id.joinButton);
        createButton = (Button)findViewById(R.id.createButton);
        passcodeView = (EditText)findViewById(R.id.editText);
    }

    public void newGame(){
        final String passcode = passcodeView.getText().toString();
        globalPass = passcodeView.getText().toString();

        final GameObject game1 = new GameObject(passcode);

        mRootRef.child(passcode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isGameTaken = dataSnapshot.exists();
                if(!isGameTaken){
                    mRootRef.child(passcode).setValue(game1);
                    joinGame(mRootRef.child(passcode));

                }
                else{
                    Toast.makeText(getApplicationContext(), "Sorry passcode is taken", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //mRootRef.child(passcode).setValue(game1);

        /* mRootRef.child(passcode).child("userA").child("isTaken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isTaken =dataSnapshot.getValue(Boolean.class);

                if (isTaken == false){

                    mRootRef.child(passcode).child("userA").child("isTaken").setValue(true);

                }

                else {
                    Toast.makeText(getApplicationContext(), "Sorry, passcode taken", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */



        //get the User A boolean for is taken



        //mRootRef.child(passcode).child("userA").child("isTaken").getValue(); //cannot work

    }

    // Enter a game
    private void joinGame(final DatabaseReference game){

        // First check the first userName: if it is not taken, join room
        game.child("userA").child("isTaken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) == false){
                    game.child("userA").child("isTaken").setValue(true);
                    Intent openMyList = new Intent(MainActivity.this, aList.class);
                    openMyList.putExtra("username", "userA");
                    startActivity(openMyList);
                } else {
                    // If userA taken, check if userB is taken
                    // If userB is not taken, join room
                    game.child("userB").child("isTaken").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue(Boolean.class) == false){
                                game.child("userB").child("isTaken").setValue(true);
                                Intent openMyList = new Intent(MainActivity.this, aList.class);
                                openMyList.putExtra("username", "userB");
                                startActivity(openMyList);
                            }
                            else{
                                // If you reach here, both users are taken
                                Toast.makeText(getApplicationContext(), "Game room is full", Toast.LENGTH_LONG).show();

                            }
                        };

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            };

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void testMethodforTestButton(View view){
        globalPass = "chicken";
        GameObject game1 = new GameObject("chicken");
        mRootRef.child("chicken").setValue(game1);
        Intent openShared = new Intent(MainActivity.this, sharedScreen.class);
        startActivity(openShared);
    }

    @Override
    protected void onStart() {
        super.onStart();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();

            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Check if the game is created
                final String passcode = passcodeView.getText().toString();
                globalPass = passcodeView.getText().toString();
                mRootRef.child(passcode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isGameTaken = dataSnapshot.exists();
                        if(!isGameTaken){
                            Toast.makeText(getApplicationContext(), "Game does not exist", Toast.LENGTH_LONG).show();
                        }
                        else {
                            joinGame(mRootRef.child(passcode));

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //Check if the game has a player


                //Check if the game has 2 players

                mConditionRef.setValue("Button");
                //String passcode = passcodeView.getText().toString();
                //mPassRef.setValue(passcode);
                //GameObject artist = new GameObject(id);
                //mRootRef.child(id).setValue(artist);



            }
        });

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text= dataSnapshot.getValue(String.class);
                view.setText(text);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
