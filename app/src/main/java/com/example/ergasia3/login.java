package com.example.ergasia3;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class login extends AppCompatActivity {

    public static final int AUDIO_RECORD_REQUEST_CODE = 1;
    EditText mailT, passT;
    Button logButton;
    TextView signText, lang, speechMAil, speechPass;
    FirebaseAuth fireAuth;
    boolean email = false; // for checking if email edittext is empty or not
    boolean pass = false; // for checking if pass edittext is empty or not
    private Integer code; // id code to choose between password and mail for speech to text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mailT = findViewById(R.id.mailFieldLog);
        passT = findViewById(R.id.passFieldLog);
        logButton = findViewById(R.id.logButton);
        signText = findViewById(R.id.goSign);
        speechMAil = findViewById(R.id.speechMailL);
        speechPass = findViewById(R.id.speechPassL);
        fireAuth = FirebaseAuth.getInstance();
        logButton.setEnabled(false); // disable so it cannot be pressed before entering info on the text fields


        // takes you to sign in page
        signText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), register.class));
            }
        });







        // listeners to check if both text fields (mail and password) are empty or not. if at least one pf them is empty the log in button does not work
        mailT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    logButton.setEnabled(false);
                    email = false;
                }else{
                    email = true;
                    if (pass){
                        logButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    logButton.setEnabled(false);
                    pass = false;
                }else{
                    pass = true;
                    if (email){
                        logButton.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    //sign in method. takes the password and email the user wrote and checks if the user exists. if it does it opens the main page of the app. if it does not it dispalys the appropriate message
    public void signin(View view){
        fireAuth.signInWithEmailAndPassword(mailT.getText().toString(),passT.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String UID = user.getUid();



                            FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try{ String type = dataSnapshot.getValue().toString();}catch (Exception e){
                                        showMessage("Error", "User Not Found");
                                        return;
                                    }
                                    String type = dataSnapshot.getValue().toString();
                                    if(type.equals("citizen")){
                                        clearPref();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    }
                                    else if(type.equals("center")){
                                        clearPref();
                                        startActivity(new Intent(getApplicationContext(),MainActivityCentre.class));
                                    }else {
                                        showMessage("Error", "User Not Found");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    showMessage("Error", "User Not Found");
                                    return;
                                }
                            });

                        }else {
                            showMessage("Error",task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    // called when user presses speech recognition btn
    public void voice_rec(View view){
        code = Integer.valueOf(view.getTag().toString()); // takes id of button
        checkPerm(Manifest.permission.RECORD_AUDIO, AUDIO_RECORD_REQUEST_CODE);
    }

    //check the permission for audio record if is already given starts speech recognition if not asks for perm
    void checkPerm (String permission, int requestCode){
        if(ContextCompat.checkSelfPermission(login.this, permission) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(login.this, new String[]{permission}, requestCode);
        }else{
            voiceStart();
        }
    }

    // if permission given starts speech rec if not notifies the user that the perm must be given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AUDIO_RECORD_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               voiceStart();
            }else{
                showMessage("Permission Denied", "Please Give The Permission To Use This Feature");
            }
        }
    }

    // initiates speech recognition
    void voiceStart(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"For '@' say At Sign for '_' underscore and for '.' say dot");
        startActivityForResult(intent,code);
    }

    // takes the result and inputs it at the right textfield using the id var code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        clearPref();
        if (resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            // removes unwanted characters from result
            String txt = matches.toString().replace(" ", "").replace("[", "").replace("]", "").replace("atsign", "@");

            if (requestCode==456){
                mailT.setText(txt);
            }else{
                passT.setText(txt);
            }
        }else{
            showMessage("Error", "Something Went Wrong. Please Try Again");
        }

    }


    // method to show messages on screen
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }



    // clears the save preferences whenever a new user logs in
    private void clearPref(){
        SharedPreferences mPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }



}