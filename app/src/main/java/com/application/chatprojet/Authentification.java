package com.application.chatprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Authentification extends AppCompatActivity {

    private EditText getphonenumber;
    private android.widget.Button btnsendnumber;
    private CountryCodePicker countryCodePicker;
    private ProgressBar progressBarAuth;

    //we declare  two variables to save the country code  and the phone number selected by user
    private String countrycode;
    private String phonenumber;

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks  Callbacks;
    private String codesent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);


        countryCodePicker = findViewById(R.id.countrycodepicker);
        getphonenumber = findViewById(R.id.getphonenumber);
        btnsendnumber = findViewById(R.id.btnSendNumber);
        progressBarAuth = findViewById(R.id.ProgressBarAuth);

        firebaseAuth=FirebaseAuth.getInstance();

        countrycode=countryCodePicker.getSelectedCountryCodeWithPlus();

        //if user want change the code country
        // the new code contry choose it it should be sored
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countrycode=countryCodePicker.getSelectedCountryCodeWithPlus();
            }
        });

        btnsendnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number;
                number=getphonenumber.getText().toString();
                if(number.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Entrez ton numero", Toast.LENGTH_SHORT).show();
                    progressBarAuth.setVisibility(View.INVISIBLE);
                }
                else if(number.length()<9){
                    Toast.makeText(getApplicationContext(),"Entrer le numero correcte ",Toast.LENGTH_SHORT).show();
                    progressBarAuth.setVisibility(View.INVISIBLE);

                }
                else {
                    progressBarAuth.setVisibility(View.VISIBLE);
                    phonenumber=countrycode+number;
                    PhoneAuthOptions options=PhoneAuthOptions.newBuilder(firebaseAuth)
                            .setPhoneNumber(phonenumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(Authentification.this)
                            .setCallbacks(Callbacks)
                            .build();
                    //it check phone number is correct or not
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
            //function for verify the code and verify send phonenumber and pass the current context
        Callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to automatically fetch code here like if you want to update my application in future  I type here
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            //this method il allow user enter the code
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(),"Code est envoye",Toast.LENGTH_SHORT).show();
                progressBarAuth.setVisibility(View.INVISIBLE);
                codesent=s;
                Intent intent=new Intent(Authentification.this,ActivitySaveNumber.class);
                intent.putExtra("code",codesent);
                startActivity(intent);

            }
        };

    }
    //if user open again open the app he should get
    //directly the activity chat not the the authenfication activity
    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent=new Intent(Authentification.this,ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}