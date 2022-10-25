package com.example.ergasia3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class
ViewRes extends AppCompatActivity {

    TextView name, centerName, centerAddress, firstDate, secondDate, firstStatus, secondStatus;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String UID = user.getUid();
    Boolean flag = false;
    DateModel appointment;
    CenterModel center;
    CitizenModel citizen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_res);

        name = findViewById(R.id.textView42);
        centerName = findViewById(R.id.textView35);
        centerAddress = findViewById(R.id.textView36);
        firstDate = findViewById(R.id.textView37);
        firstStatus = findViewById(R.id.textView38);
        secondDate = findViewById(R.id.textView39);
        secondStatus = findViewById(R.id.textView40);

        searchRes();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showReservation();
            }
        }, 1000);


    }


    void showReservation(){
        if(flag){
            getCenter();
            getUser();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fillText();
                }
            }, 1000);

        }else{
            showMessage("Error", "User does not have an Appointment");
            return;
        }
    }

    void fillText(){
        try{
            String fullName = citizen.getFirstName() + " " + citizen.getLastName();
            name.setText(fullName);
            centerName.setText(center.getName());
            centerAddress.setText(center.getAddress());
            firstDate.setText(appointment.getDate1());
            secondDate.setText(appointment.getDate2());
            firstStatus.setText(appointment.getFirstDose());
            secondStatus.setText(appointment.getSecondDose());
        }catch (Exception e){
            showMessage("Error", "Something went wrong");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }, 2000);
        }

    }

    void getCenter(){
        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("vaccination centers").child(appointment.getCenterId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    center = snapshot.getValue(CenterModel.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void getUser(){
        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                citizen = snapshot.getValue(CitizenModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void searchRes(){
        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("reservations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    DateModel dateModel = childSnapshot.getValue(DateModel.class);
                    String uidDB = dateModel.getCitizenUid();
                    try{
                        if(uidDB.equals(UID)){
                            flag = true;
                            appointment = dateModel;
                            return;
                        }
                    }catch (Exception e){
                        showMessage("Error", "User not found. Please log out and try again.");
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}