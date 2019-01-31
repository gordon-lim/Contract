package com.example.soymilk.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.solver.widgets.Helper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.soymilk.myapplication.models.GameObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    /*

    Notes:

    21/1/19: Firebase require your Pojo to have public variables or getter/setter. When I previously did not define the
    access modifiers, my object could not be added!
    Refer to link: https://www.learnhowtoprogram.com/android/data-persistence/firebase-writing-pojos

     */


    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
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

    }

    // Enter a game
    private void joinGame(final DatabaseReference game){

        // First check the first userName: if it is not taken, join room
        game.child("userA").child("isTaken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) == false){
                    game.child("userA").child("isTaken").setValue(true);
                    Intent openMyList = new Intent(MainActivity.this, GameActivity.class);
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
                                Intent openMyList = new Intent(MainActivity.this, GameActivity.class);
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

            }
        });


    }



}
