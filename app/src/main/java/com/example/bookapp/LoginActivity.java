package com.example.bookapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;


import androidx.annotation.NonNull;


import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;


import java.util.concurrent.TimeUnit;


public class LoginActivity extends Activity {

    private static final String TAG="LoginAcivity";
    private static final String PROGRESS="PROGRESS";
    private Button sendOtpButton;
    private Button verifyOtpButton;
    private TextView timerView;
    private EditText contactText;
    private EditText otp1,otp2,otp3,otp4,otp5,otp6;
    private Button editButton;

    private String contact;
    private int time=60;
    private CountDownTimer timer;
    private boolean progress;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private ViewGroup viewGroup;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        editButton=findViewById(R.id.edit_button);
        editButton.setVisibility(View.GONE);
        sendOtpButton=findViewById(R.id.send_button);
        verifyOtpButton=findViewById(R.id.verify_button);
        timerView=findViewById(R.id.timer);
        contactText=findViewById(R.id.contact);
        viewGroup=findViewById(R.id.verify_view);
        viewGroup.setVisibility(View.INVISIBLE);
        verifyOtpButton.setVisibility(View.GONE);
        timerView.setVisibility(View.GONE);
        contactText.requestFocus();
        otp1=findViewById(R.id.otp1);
        otp2=findViewById(R.id.otp2);
        otp3=findViewById(R.id.otp3);
        otp4=findViewById(R.id.otp4);
        otp5=findViewById(R.id.otp5);
        otp6=findViewById(R.id.otp6);
        mAuth = FirebaseAuth.getInstance();
        int timerTime=60000;

        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count==1)
                otp2.requestFocus();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==1)
                    otp3.requestFocus();
                if(count==0){
                    otp1.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==1)
                    otp4.requestFocus();
                if(count==0){
                    otp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==1)
                    otp5.requestFocus();
                if(count==0){
                    otp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==1)
                    otp6.requestFocus();
                if(count==0){
                    otp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(otp6.getText().toString().length()==1)

                if(count==1)
                    verifyOtpButton.requestFocus();
                if(count==0){
                    otp5.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp=TextUtils.concat(otp1.getText(),otp2.getText(),otp3.getText(),otp4.getText(),otp5.getText(),otp6.getText()).toString();
                if (!TextUtils.isEmpty(otp))
                    verifyPhoneNumberWithCode(mVerificationId, otp);
                else {
                    Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_LONG).show();
                }
            }
        });
        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(contactText.getText()))
                {
                    contactText.setError("Empty!");
                }
                else {
                    otp1.requestFocus();
                    editButton.setVisibility(View.VISIBLE);
                    //Starting timer
                    viewGroup.setVisibility(View.VISIBLE);
                    verifyOtpButton.setVisibility(View.VISIBLE);
                    timerView.setVisibility(View.VISIBLE);
                    sendOtpButton.setVisibility(View.GONE);
                    contactText.setEnabled(false);

                    timer = new CountDownTimer(timerTime, 1000) {

                        public void onTick(long millisUntilFinished) {
                            if (savedInstanceState != null)
                                savedInstanceState.putBoolean(PROGRESS, true);
                            progress = true;
                            timerView.setText(TextUtils.concat("" , checkDigit(time)));
                            time--;
                            sendOtpButton.setText("Resend");
                            sendOtpButton.setClickable(false);
                        }

                        public void onFinish() {
                            timerView.setText("00");
                            time = 60;
                            if (savedInstanceState != null)
                                savedInstanceState.putBoolean(PROGRESS, false);
                            progress = false;
                            sendOtpButton.setVisibility(View.VISIBLE);
                            sendOtpButton.setClickable(true);
                            verifyOtpButton.setVisibility(View.GONE);
                            contactText.setEnabled(true);

                        }

                    }.start();
                    //ending timer

                    contact = TextUtils.concat("+91",contactText.getText()).toString();
                    verifyPhoneNumber(contact);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);
                timer.cancel();
                timerView.setText("00");
                time = 60;

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(getApplicationContext(),"Please try again tomorrow!",Toast.LENGTH_LONG).show();
                }
                if(savedInstanceState!=null)
                    savedInstanceState.putBoolean(PROGRESS,false);
                progress=false;
                timer.cancel();
                timerView.setText("00");
                time = 60;
                Toast.makeText(getApplicationContext(),"Verification failed! Check Internet",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };




        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                viewGroup.setVisibility(View.INVISIBLE);
                verifyOtpButton.setVisibility(View.GONE);
                timerView.setVisibility(View.GONE);
                contactText.setEnabled(true);
                contactText.setText("");
                sendOtpButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.GONE);
                sendOtpButton.setClickable(true);
                sendOtpButton.setText("Send OTP");
            }
        });
        //onCreate ends here
    }

    private String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        progress=savedInstanceState.getBoolean(PROGRESS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (progress){
            verifyPhoneNumber(contact);
        }
    }

    private void verifyPhoneNumber(String phone)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);     // OnVerificationStateChangedCallbacks
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            Intent intent=new Intent(LoginActivity.this,NewProfileActivity.class);
                            intent.putExtra("USER_ID",user.getUid());
                            intent.putExtra("USER_CONTACT",user.getPhoneNumber());
                            startActivity(intent);

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(),"Invalid OTP",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }


}
