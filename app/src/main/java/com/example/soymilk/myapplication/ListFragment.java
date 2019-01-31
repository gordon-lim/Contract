package com.example.soymilk.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ListFragment extends Fragment {


    // the fragment initialization parameters
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    View rootView;
    String mUsername;
    String mPassword;
    ArrayAdapter<String> adapter;
    ListView itemsListView;
    private ValueEventListener mListener;

    // References
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();


    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(String username, String password) {
        ListFragment fragment = new ListFragment();
        // Supply input params as arguments
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle args = getArguments();
        mUsername = args.getString(USERNAME);
        mPassword = args.getString(PASSWORD);
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_list, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.title);
        textView.setText("Your List (" + mUsername + ")");
        ArrayList<String> str = new ArrayList<String>();
        initializeUI(str);

        // Create a List of 5 random items
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

        final List myList = list.subList(0,5);

        mRootRef.child(mPassword).child(mUsername).child("myList").setValue(myList);

        mListener = mRootRef.child(mPassword).child(mUsername).child("myList").addValueEventListener(new ValueEventListener() {
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

        return rootView;

    }

    private void initializeUI (final ArrayList<String> listOfItems) {

        // Find a reference to the {@link ListView} in the layout
        ListView itemsListView = (ListView) rootView.findViewById(R.id.list_item);
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter =
                new ArrayAdapter<String>(getActivity(), R.layout.list_item_layout, listOfItems);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        itemsListView.setAdapter(adapter);

    }

    private void updateUI (final ArrayList<String> listOfItems) {

        adapter.clear();
        adapter.addAll(listOfItems);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRootRef.child(mPassword).child(mUsername).child("myList").removeEventListener(mListener);
    }
}
