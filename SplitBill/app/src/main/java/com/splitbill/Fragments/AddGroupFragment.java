package com.splitbill.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.splitbill.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddGroupFragment extends Fragment {

    FirebaseApp firebaseApp;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    private boolean flag = true;
    FirebaseAuth.AuthStateListener authStateListener;

    public AddGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);
        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null && flag) {
                    onAuthSuccess(firebaseAuth.getCurrentUser());
                    flag = false;
                }
                firebaseAuth.getUid();
            }
        };



        return view;
    }

    private void onAuthSuccess(FirebaseUser currentUser) {
        if (currentUser!=null){
            Log.e("CurrentUser", currentUser.getPhoneNumber());
        }
    }
}


