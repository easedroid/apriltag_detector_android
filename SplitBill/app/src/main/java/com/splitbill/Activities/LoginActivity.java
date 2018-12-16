package com.splitbill.Activities;

import android.arch.core.executor.TaskExecutor;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.splitbill.Helper.CommonHelper;
import com.splitbill.MainActivity;
import com.splitbill.R;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etPhone, etCode;
    Button btnLogin;
    String verificationId;
    FirebaseAuth mAuth;
    String phoneNumber;
    private boolean triggered = false, exit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etPhone = findViewById(R.id.etPhoneNumber);
        btnLogin = findViewById(R.id.btnSignIn);
        etCode = findViewById(R.id.etCode);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.btnSignIn:
                phoneNumber = "+" + "91" + etPhone.getText().toString().trim();
                if (!triggered) {
                    triggered = true;
                    if (phoneNumber.isEmpty()) {
                        etPhone.setError("Phone number is required");
                        etPhone.requestFocus();
                        return;
                    } else {
                        sendVerificationCode(phoneNumber);
                    }
                }else {
                    triggered = false;
                    String code = etCode.getText().toString().trim();
                    if (code.isEmpty()) {
                        etPhone.setError("Invalid code");
                        etPhone.requestFocus();
                        return;
                    } else {
                        verifyCode(code);
                    }
                }

        }
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallback
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            btnLogin.setText("Verify");
            etCode.setVisibility(View.VISIBLE);
            etCode.requestFocus();
            etPhone.setEnabled(false);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            etCode.setText(code);
            if (code!=null){
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }
    };

    private void verifyCode(String code){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(phoneAuthCredential);
    }

    private void signInWithCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }else {
                            CommonHelper.getInstance().showMessageShort(LoginActivity.this, task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (triggered){
            triggered = false;
            etCode.setVisibility(View.GONE);
            etPhone.requestFocus();
            etPhone.setText("");
            btnLogin.setText("SignIn");
        }
        if (exit){
            finish();
        }else {
            exit =true;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                    handler.removeCallbacks(null);
                }
            },1600);
        }
        //super.onBackPressed();
    }
}
