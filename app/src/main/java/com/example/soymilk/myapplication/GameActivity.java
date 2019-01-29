package com.example.soymilk.myapplication;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {

    ViewPager viewPager;
    GamePagerAdapter adapter;
    String username;
    String othername;
    String password = MainActivity.globalPass;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        username = getIntent().getStringExtra("username");

        viewPager = (ViewPager) findViewById(R.id.container);
        adapter = new GamePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

    }


    // Inner class for PagerAdapter

    class GamePagerAdapter extends FragmentStatePagerAdapter {

        public GamePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ListFragment().newInstance(username, password);
                case 1:
                    return new GiveFragment().newInstance(username, password);
                default:
                    return null;

            }

        }


        @Override
        public int getCount() {
            return 2;
        }

        // TODO: Understand this code
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

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
    protected void onDestroy() {
        super.onDestroy();
        leaveGame();
    }


}
