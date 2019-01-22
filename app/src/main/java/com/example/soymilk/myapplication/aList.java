package com.example.soymilk.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class aList extends AppCompatActivity {

    String passcode = MainActivity.globalPass;
    ArrayAdapter<String> adapter;
    String username;



    //KIV: Make a random list





    //FOR NOW: Make a fixed list
    //List<String> myList = Arrays.asList("hand","leg","arm");

    //Put list onto realtime database (need to get passcode from previous activity

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    //if come here from create button
    //if come here from join button

    //onClose clear passcode table from Fire

    private void initializeUI (final ArrayList<String> listOfItems) {

        // Find a reference to the {@link ListView} in the layout
        ListView itemsListView = (ListView) findViewById(R.id.list_item);
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfItems);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        itemsListView.setAdapter(adapter);


    }

    private void updateUI (final ArrayList<String> listOfItems) {

        adapter.clear();
        adapter.addAll(listOfItems);
    }

    private void leaveGame (String username) {

        //First leave yourself
        mRootRef.child(passcode).child(username).child("isTaken").setValue(false);
        mRootRef.child(passcode).child(username).child("isEnded").setValue(false);
        mRootRef.child(passcode).child(username).child("myList").setValue(new ArrayList<String>());
        mRootRef.child(passcode).child(username).child("myGive").setValue(new ArrayList<String>());

        //Then check other user

        String othername = username=="userA" ? "userB" : "userA";

        mRootRef.child(passcode).child(othername).child("isTaken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class) == false){
                    mRootRef.child(passcode).removeValue();
                }
            };

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Reflect changes on realtime database to UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_list);
        //Toast.makeText(getApplicationContext(), passcode, Toast.LENGTH_LONG).show();

        username = getIntent().getStringExtra("username");

        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText(username);
        ArrayList<String> str = new ArrayList<String>();
        initializeUI(str);


    }

    @Override
    protected void onStart() {
        super.onStart();

        List<String> list = Arrays.asList(
                "shawl",
                "shovel",
                "balloon",
                "pencil",
                "charger",
                "fridge",
                "keys",
                "controller",
                "CD",
                "stockings",
                "key chain",
                "button",
                "radio",
                "drill press",
                "pants",
                "conditioner",
                "phone",
                "hair brush",
                "bowl",
                "shoe lace",
                "soap",
                "lamp shade",
                "eye liner",
                "coasters",
                "bow",
                "clamp",
                "washing machine",
                "vase",
                "ring",
                "purse",
                "magnet",
                "newspaper",
                "street lights",
                "sailboat",
                "rubber band",
                "sand paper",
                "book",
                "water bottle",
                "perfume",
                "paint brush",
                "sticky note",
                "toe ring",
                "air freshener",
                "thermostat",
                "glass",
                "toilet",
                "flag",
                "knife",
                "scotch tape");

        Collections.shuffle(list);

        final List myList = list.subList(0,3);

        //convert to ArrayList

        //final ArrayList<String> myArrayList = new ArrayList<String>(myList);


        mRootRef.child(passcode).child(username).child("myList").setValue(myList);

        mRootRef.child(passcode).child(username).child("myList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //List aListFromFire = dataSnapshot.getValue(List.class); //problem with this
                //ArrayList<String> myArrayList = new ArrayList<String>(aListFromFire);
                //updateUI(myArrayList);

                ArrayList<String> myArrayList = (ArrayList<String>) dataSnapshot.getValue();
                if (myArrayList != null)
                    updateUI(myArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



            }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        leaveGame(username);
    }
}

