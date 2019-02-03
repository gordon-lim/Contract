package com.example.soymilk.myapplication;

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
import java.util.zip.Inflater;

public class GetFragment extends Fragment {

    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    String mUsername;
    String mOthername;
    String mPassword;
    ValueEventListener mListener;

    ArrayAdapter<String> adapter;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    ArrayList<String> getArrayList;

    View rootView;


    public GetFragment(){

    }
    public static Fragment newInstance(String username, String password){
        GetFragment fragment = new GetFragment();
        // Supply input params as arguments
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        Bundle args = getArguments();
        mUsername = args.getString(USERNAME);
        mOthername = (mUsername.equals("userA")) ? "userB" : "userA";
        mPassword = args.getString(PASSWORD);

        TextView textView = (TextView) rootView.findViewById(R.id.title);
        textView.setText(mOthername + " agrees to give you:");

        ArrayList<String> str = new ArrayList<String>();
        initializeUI(str);




        mListener = mRootRef.child(mPassword).child(mOthername).child("myGive").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getArrayList = (ArrayList<String>)dataSnapshot.getValue();
                getArrayList = (ArrayList<String>) dataSnapshot.getValue();
                if (getArrayList != null)
                    updateUI(getArrayList);
                else{
                    updateUI(new ArrayList<String>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    private void initializeUI (final ArrayList<String> listOfItems) {

        // Find a reference to the {@link ListView} in the layout
        ListView itemsListView = rootView.findViewById(R.id.list_item);
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_layout, listOfItems);
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
        mRootRef.child(mPassword).child(mOthername).child("myGive").removeEventListener(mListener);
    }
}
