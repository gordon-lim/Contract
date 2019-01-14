package com.example.soymilk.myapplication;

import java.util.Arrays;
import java.util.List;

public class GameObject {

    User userA = new User();
    User userB = new User();
    String passcode = "";

    public GameObject(String passcode) {
        this.passcode = passcode;
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getPasscode() {
        return passcode;
    }

    /* public boolean isEndedA() {
        return EndedA;
    }

    public boolean isEndedB() {
        return EndedB;
    }

    public String[] getA2bList() {
        return a2bList;
    }

    public String[] getaList() {
        return aList;
    }

    public String[] getB2aList() {
        return b2aList;
    }

    public String[] getbList() {
        return bList;
    } */

}
