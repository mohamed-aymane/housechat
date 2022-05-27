package com.application.chatprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class ActivitySaveNumber extends AppCompatActivity {

    private EditText getcode;
    private TextView changenumber;
    private android.widget.Button btnSaveCode;
    private ProgressBar progressBarNumber;

    private String entercode;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_number);

        getcode=findViewById(R.id.getcode);
        changenumber=findViewById(R.id.ChangeNumber);
        btnSaveCode=findViewById(R.id.btnSaveCode);
        progressBarNumber=findViewById(R.id.ProgressBarNumber);

        firebaseAuth=FirebaseAuth.getInstance();

        changenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ActivitySaveNumber.this,Authentification.class);
                startActivity(intent);
            }
        });

        btnSaveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entercode=getcode.getText().toString();
                if(entercode.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Entrez le code que vous avez recu", Toast.LENGTH_SHORT).show();
                    progressBarNumber.setVisibility(View.INVISIBLE);
                }
                else{
                    progressBarNumber.setVisibility(View.VISIBLE);
                    String coderecived=getIntent().getStringExtra("code");//intent.putExtra("code",codesent);

                    //For verification the code that you recieved
                    PhoneAuthCredential credential= PhoneAuthProvider.getCredential(coderecived,entercode);
                    signInWithPhoneAuthCredential(credential);

                }
            }
        });
    }
    //this function is used for verifey login is sucessed or failed
    private  void signInWithPhoneAuthCredential(PhoneAuthCredential credential){

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Connexion reussie",Toast.LENGTH_SHORT).show();
                    progressBarNumber.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(ActivitySaveNumber.this,SetProfil.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(getApplicationContext(),"Echec de la connexion",Toast.LENGTH_SHORT).show();
                        progressBarNumber.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });
    }
}