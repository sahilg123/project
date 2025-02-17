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

import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    HashMap<String,String> user;


     RequestQueue requestQueue;
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

        user= (HashMap<String, String>) in.getSerializableExtra("user");
        final String phoneno= user.get("phone");

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

        new CountDownTimer(timeInMiliSeconds,1000) {
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
                        Toast.makeText(otp.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }


        );
    }
    private void Submit()
    {
        Call<JSONObject> call=RetrofitClient
                .getInstance()
                .getApi()
                .createuser(user.get("fullName"),user.get("email"),user.get("password"),user.get("dob"),user.get("country"),user.get("state"),user.get("city"),user.get("phone"));
         call.enqueue(new Callback<JSONObject>() {
             @Override
             public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                 String s;
                 if(response.body()!=null) {
                     s = response.body().toString();
                     Toast.makeText(getApplicationContext(), s , Toast.LENGTH_SHORT).show();
                 }
                 else
                 Toast.makeText(getApplicationContext(),"no response",Toast.LENGTH_SHORT).show();

             }


             @Override
             public void onFailure(Call<JSONObject> call, Throwable t) {
                 Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
             }
         });


    }



    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(otp.this, "Verified", Toast.LENGTH_SHORT).show();
                    Submit();
                    startActivity(new Intent(otp.this,compdetails.class));
                }else {

                    Toast.makeText(otp.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}