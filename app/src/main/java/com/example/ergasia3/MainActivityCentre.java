package com.example.ergasia3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivityCentre extends AppCompatActivity {

    Button certBtn, viewBtn, logOffBtn;
    TextView logo;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String UID = user.getUid();

    ArrayList<DateModel> modelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_centre);

        modelArrayList.clear();

        logo = findViewById(R.id.welcomeText);
        certBtn = findViewById(R.id.certBtn);
        viewBtn = findViewById(R.id.certBtn);
        logOffBtn = findViewById(R.id.loBtn);

        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("vaccination centers").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    CenterModel center = dataSnapshot.getValue(CenterModel.class);
                    String name = center.getName();
                    logo.setText(R.string.welcom + name);
                }catch (Exception e){
                    showMessage("Error", "Log Out And Try Again");
                    viewBtn.setEnabled(false);
                    certBtn.setEnabled(false);
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showMessage("Error", "User Not Found");
                return;
            }
        });

    }


    public void viewAppCenter(View view){
        modelArrayList.clear();

        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("reservations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    DateModel model = childSnapshot.getValue(DateModel.class);
                    if(model.getCenterId().equals(UID)){
                        modelArrayList.add(model);
                    }
                }
               save();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // clears the save preferences whenever a new user logs in
    private void clearPref(){
        SharedPreferences mPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.clear();
        preferencesEditor.apply();
    }

    void save(){
        Collections.sort(modelArrayList);

        clearPref();

        Gson gson = new Gson();
        String json = gson.toJson(modelArrayList);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("Appointments", json);
        editor.apply();

        startActivity(new Intent(getApplicationContext(), ViewResCenter.class));
    }

    public void scanVacc(View view){
        startActivity(new Intent(getApplicationContext(), CertScan.class));
    }

    public void logOff(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), login.class));
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}