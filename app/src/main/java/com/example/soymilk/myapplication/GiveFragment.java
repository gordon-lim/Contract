package com.example.soymilk.myapplication;

import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import me.xdrop.fuzzywuzzy.FuzzySearch;


public class GiveFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    // the fragment initialization parameters
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    Button mAddButton;
    EditText editText;
    TextView displayName;
    CheckBox aCheckbox;
    CheckBox bCheckbox;

    // Listeners

    private ValueEventListener mListener2;
    private ValueEventListener mListener3;
    private ValueEventListener mListener4;

    String mUsername;
    String mOthername;
    String mPassword;
    boolean otherEnded = false;
    boolean userEnded = false;

    ArrayList<String> giveArrayList;

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
                        giveArrayList = (ArrayList<String>) dataSnapshot.getValue();
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
        mOthername = (mUsername.equals("userA")) ? "userB" : "userA";
        mPassword = args.getString(PASSWORD);
        mGiveRef = mRootRef.child(mPassword).child(mUsername).child("myGive");

        ArrayList<String> str = new ArrayList<String>();
        initializeUI(str);

        // When I end, update local variable

        mListener2 = mRootRef.child(mPassword).child(mUsername).child("Ended").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userEnded = dataSnapshot.getValue(Boolean.class);
                //if both ended, open result
                if(otherEnded && userEnded){
                    // Intent to open results Activity
                    Intent openResults = new Intent(getActivity(), ResultsActivity.class);
                    openResults.putExtra(USERNAME, mUsername);
                    startActivity(openResults);

                    // else if only other ended
                }else if(userEnded){
                    // if true, setText waiting for other
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // When other person end, update local variable
        mListener3 = mRootRef.child(mPassword).child(mOthername).child("Ended").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                otherEnded = dataSnapshot.getValue(Boolean.class);
                //if both ended, open result
                if(otherEnded && userEnded){
                    // Intent to open results Activity
                    checkOther();
                    Intent openResults = new Intent(getActivity(), ResultsActivity.class);
                    openResults.putExtra(USERNAME, mUsername);
                    startActivity(openResults);

                // else if only other ended
                }else if(otherEnded){
                    // if true, setText other player waiting for you
                    // also, set other checked
                    checkOther();

                }
                // if false, revert
                else{
                    uncheckOther();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // When there's change to Firebase, update UI.
        mListener4 = mGiveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                giveArrayList = (ArrayList<String>) dataSnapshot.getValue();
                if (giveArrayList != null)
                    updateUI(giveArrayList);
                else{
                    updateUI(new ArrayList<String>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        displayName = (TextView) rootView.findViewById(R.id.user);
        displayName.setText(mUsername);
        aCheckbox = (CheckBox) rootView.findViewById(R.id.Aready);
        bCheckbox = (CheckBox) rootView.findViewById(R.id.Bready);


        // Disable other player's CheckBox from the get go
        // And set listener to own
        if(mUsername.equals("userA")){
            bCheckbox.setEnabled(false);
            aCheckbox.setOnCheckedChangeListener(this);
        }else{
            aCheckbox.setEnabled(false);
            bCheckbox.setOnCheckedChangeListener(this);
        }


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

        mGiveRef = mRootRef.child(mPassword).child(mUsername).child("myGive");


        mGiveRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean GiveListExist = dataSnapshot.exists();

                // If mGiveRef exists, append to existing List
                if(GiveListExist){

                    // Get the current list on the database
                    giveArrayList = (ArrayList<String>) dataSnapshot.getValue();

                }
                // Else, add to new List
                else{
                    giveArrayList = new ArrayList<String>();
                }
                // Add the new item
                giveArrayList.add(itemAdded);
                // Send it back up
                mGiveRef.setValue(giveArrayList);
                // My ValueEventListener will trigger...Sending the list backdown and updating the UI
            } // End of OnDataChange

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); // End of Listener

        // Hides keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        // Clears editText

        editText.setText("");

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            mRootRef.child(mPassword).child(mUsername).child("Ended").setValue(true);
        }else{
            mRootRef.child(mPassword).child(mUsername).child("Ended").setValue(false);
        }
    }

    // Set other player checkbox true after he checked
    public void checkOther(){
        if(mOthername.equals("userA")){
            aCheckbox.setChecked(true);
        }else{
            bCheckbox.setChecked(true);
        }
    }

    // Set other player checkbox false after he unchecked
    public void uncheckOther(){
        if(mOthername.equals("userA")){
            aCheckbox.setChecked(false);
        }else{
            bCheckbox.setChecked(false);
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        mGiveRef.removeEventListener(mListener4);
        mRootRef.child(mPassword).child(mOthername).child("Ended").removeEventListener(mListener3);
        mRootRef.child(mPassword).child(mUsername).child("Ended").removeEventListener(mListener2);

    }
}
