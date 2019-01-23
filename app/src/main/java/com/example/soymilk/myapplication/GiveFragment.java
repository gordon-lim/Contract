package com.example.soymilk.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GiveFragment extends Fragment {

    // the fragment initialization parameters
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    Button mAddButton;
    EditText editText;

    String mUsername;
    String mPassword;

    ArrayAdapter<String> adapter;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mGiveRef;

    View rootView;

    private void initializeUI(final ArrayList<String> listOfItems) {

        // Find a reference to the {@link ListView} in the layout
        ListView itemsListView = (ListView) rootView.findViewById(R.id.giveList);
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listOfItems);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        itemsListView.setAdapter(adapter);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        itemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                mGiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<String> giveArrayList = (ArrayList<String>) dataSnapshot.getValue();
                        giveArrayList.remove(position);
                        mGiveRef.setValue(giveArrayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                return false;
            }
        });
    }

    private void updateUI(final ArrayList<String> listOfItems) {

        adapter.clear();
        adapter.addAll(listOfItems);
    }


    public GiveFragment() {
        // Required empty public constructor
    }


    public static GiveFragment newInstance(String username, String password) {
        GiveFragment fragment = new GiveFragment();
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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_shared, container, false);

        Bundle args = getArguments();
        mUsername = args.getString(USERNAME);
        mPassword = args.getString(PASSWORD);
        mGiveRef = mRootRef.child(mPassword).child(mUsername).child("myGive");

        ArrayList<String> str = new ArrayList<String>();
        initializeUI(str);

        // When there's change to Firebase, update UI.
        mGiveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> giveArrayList = (ArrayList<String>) dataSnapshot.getValue();
                if (giveArrayList != null)
                    updateUI(giveArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAddButton = (Button) rootView.findViewById(R.id.add);
        editText = (EditText) rootView.findViewById(R.id.enterItem);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                add(editText.getText().toString());

            }
        });

        return rootView;
    }

    // Method to add item to List in Firebase

    public void add(final String itemAdded) {

        mGiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Welp apparently I shouldn't be using Lists in Firebase db
                // Here's a less efficient work around

                // Get the current list on the database
                ArrayList<String> giveArrayList = (ArrayList<String>) dataSnapshot.getValue();
                // Add the new item
                giveArrayList.add(itemAdded);
                // Send it back up
                mGiveRef.setValue(giveArrayList);

                // My ValueEventListener will trigger...Sending the list backdown and updating the UI

                // Clear editText
                editText.setText("");

                // Hides keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
