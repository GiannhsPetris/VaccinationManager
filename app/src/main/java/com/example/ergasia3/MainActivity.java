package com.example.ergasia3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button appBtn, certBtn, viewBtn, logBtn;
    TextView userText;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String UID = user.getUid();

    String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appBtn = findViewById(R.id.appointBtn);
        certBtn = findViewById(R.id.certBtn);
        viewBtn = findViewById(R.id.viewBtn);
        logBtn = findViewById(R.id.loBtn);
        userText = findViewById(R.id.welcomeText);



        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    CitizenModel citizen = dataSnapshot.getValue(CitizenModel.class);
                    String name = citizen.getFirstName() + " " + citizen.getLastName();
                    userText.setText(getString(R.string.welcom) + name);
                }catch (Exception e){
                    showMessage("Error", "Log Out And Try Again");
                    appBtn.setEnabled(false);
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

    void getAge(){
        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").child(UID).child("dateOfBirth").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    showMessage("Error getting data", task.getException().toString());
                }
                else {

                    String dob = String.valueOf(task.getResult().getValue());

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate date = LocalDate.parse(dob, formatter);

                    LocalDate today = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    Period p = Period.between(date, today);
                    Integer age = p.getYears();
                    if (age < 45) {
                        showMessage("Error", "Wrong Date of Birth. Enter a Valid Date.\n You must be older than 65 to get a vaccine.");
                        return;
                    }else{
                        startActivity(new Intent(getApplicationContext(), FindVacc.class));
                    }
                }
            }
        });
    }

    public void goApp(View view){
        getAge();
    }

    public void viewApp(View view){
        startActivity(new Intent(getApplicationContext(), ViewRes.class));
    }

   public void viewCert(View view){
        startActivity(new Intent(getApplicationContext(), CertView.class));
    }

   public void log(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), login.class));
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}