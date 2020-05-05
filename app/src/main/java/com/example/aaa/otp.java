package com.example.aaa;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {
    FirebaseAuth fAuth;
    Button letsStart;
    String remainingTime="";

     TextView countDownText;
    long timeInMiliSeconds=60000;
    EditText otpCode;
    PhoneAuthCredential credential;
    String verificationId;
    String otp1="123456";
    private Handler mainHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        fAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        letsStart=(Button)findViewById(R.id.letsstart);
        otpCode=(EditText)findViewById(R.id.otp);
 Intent in=getIntent();
 final String phoneno=in.getStringExtra("phoneno");
 countDownText=(TextView)findViewById(R.id.timer);

requestPhoneAuth(phoneno);


letsStart.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        otp1 = otpCode.getText().toString();
        if(otp1.isEmpty()){
            otpCode.setError("Required");
            return;
        }

        credential = PhoneAuthProvider.getCredential(verificationId,otp1);
        verifyAuth(credential);
    }
});
    }
    private void requestPhoneAuth(String phoneNumber){

                CountDownTimer countDownTimer=new CountDownTimer(timeInMiliSeconds,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeInMiliSeconds=millisUntilFinished;
                        remainingTime=Long.toString(timeInMiliSeconds/1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countDownText.setText("VALID FOR "+remainingTime+" SECONDS");
                            }
                        });



                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60L, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId=s;

                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        Toast.makeText(otp.this, "OTP Timeout, Please Re-generate the OTP Again.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        verifyAuth(phoneAuthCredential);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                    }

                }


        );
    }
    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(otp.this, "Verified", Toast.LENGTH_SHORT).show();
                     startActivity(new Intent(otp.this,compdetails.class));
                }else {

                    Toast.makeText(otp.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
