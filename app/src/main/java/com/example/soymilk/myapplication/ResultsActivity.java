package com.example.soymilk.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    // Goal:
    // If userA, compute A list and B give
    // If userB, compute B list and A give

    TextView scoreView;
    TextView resultView;
    Button endGame;

    String username; // Need to get from GiveFragment or GameActivity
    String othername;
    String password = MainActivity.globalPass;

    String scoreString;
    int myScore;
    int otherScore;

    ArrayList<String> myList;
    ArrayList<String> otherGiveList;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    DatabaseReference mMyListRef;
    DatabaseReference mOtherGiveRef;

    public void leaveGame() {

        //First leave yourself
        mRootRef.child(password).child(username).child("isTaken").setValue(false);
        mRootRef.child(password).child(username).child("isEnded").setValue(false);

        // The following will delete the List objects because Firebase cannot have an empty List
        // mRootRef.child(mPassword).child(username).child("myList").setValue(new ArrayList<String>());
        // mRootRef.child(mPassword).child(username).child("myGive").setValue(new ArrayList<String>());

        //Then check other user

        othername = (username.equals("userA")) ? "userB" : "userA";

        mRootRef.child(password).child(othername).child("isTaken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) == false) {
                    mRootRef.child(password).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        scoreView = (TextView) findViewById(R.id.score);
        resultView = (TextView) findViewById(R.id.win_or_lose);
        endGame = (Button) findViewById(R.id.finish);

        username = getIntent().getStringExtra("username");
        othername = (username.equals("userA")) ? "userB" : "userA";
        mMyListRef = mRootRef.child(password).child(username).child("myList");
        mOtherGiveRef = mRootRef.child(password).child(othername).child("myGive");

        endGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // leaveGame();
                startActivity(new Intent(ResultsActivity.this, MainActivity.class));
            }
        });


        mMyListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myList = (ArrayList<String>) dataSnapshot.getValue();

                mOtherGiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        otherGiveList = (ArrayList<String>) dataSnapshot.getValue();
                        myScore = computeResult(myList, otherGiveList);
                        mRootRef.child(password).child(username).child("score").setValue(myScore);


                        // Get other player's score

                        mRootRef.child(password).child(othername).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                otherScore = dataSnapshot.getValue(Integer.class);
                                compareScores(myScore, otherScore);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        }); // END OF LISTENER BLOCK


                        scoreString = Integer.toString(myScore);
                        scoreView.setText(scoreString);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }); // End of listener block

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); // End of listener block

    }

    // Method to compare both lists
    public int computeResult(ArrayList<String> list1, ArrayList<String> list2) {
        int score = 0;
        for (String s : list1) {
            for (String s2 : list2) {
                if (s.equals(s2)) {
                    score++;
                }
            }
        }
        return score;
    }

    public void compareScores(int myscore, int otherscore) {
        if (myscore > otherscore) {
            resultView.setText("WIN");
        } else if (myscore < otherscore) {
            resultView.setText("LOSE");
        } else {
            resultView.setText("DRAW");
        }
    }


}
