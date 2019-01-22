package com.example.soymilk.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class sharedScreen extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    String passcode = MainActivity.globalPass;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mA2BList = mRootRef.child(passcode).child("userA").child("myGive");

    private void initializeUI (final ArrayList<String> listOfItems) {

        // Find a reference to the {@link ListView} in the layout
        ListView itemsListView = (ListView) findViewById(R.id.a2b);
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfItems);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        itemsListView.setAdapter(adapter);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    private void updateUI (final ArrayList<String> listOfItems) {

        adapter.clear();
        adapter.addAll(listOfItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.shared_view);
        ArrayList<String> str = new ArrayList<String>();
        initializeUI(str);



    }

    @Override
    protected void onStart() {
        super.onStart();
        mA2BList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> a2bArrayList = (ArrayList<String>) dataSnapshot.getValue();
                if (a2bArrayList != null)
                    updateUI(a2bArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
