package com.example.soymilk.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GiveFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GiveFragment extends Fragment {

    ArrayAdapter<String> adapter;
    String passcode = MainActivity.globalPass;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mA2BList = mRootRef.child(passcode).child("userA").child("myGive");

    View rootView;

    private void initializeUI (final ArrayList<String> listOfItems) {

        // Find a reference to the {@link ListView} in the layout
        ListView itemsListView = (ListView) rootView.findViewById(R.id.a2b);
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listOfItems);
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        itemsListView.setAdapter(adapter);
        itemsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void updateUI (final ArrayList<String> listOfItems) {

        adapter.clear();
        adapter.addAll(listOfItems);
    }




        private OnFragmentInteractionListener mListener;

    public GiveFragment() {
        // Required empty public constructor
    }



    public static GiveFragment newInstance() {
        GiveFragment fragment = new GiveFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_shared, container, false);


        ArrayList<String> str = new ArrayList<String>();
        initializeUI(str);

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

        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onSharedFragmentInteraction();
    }
}
