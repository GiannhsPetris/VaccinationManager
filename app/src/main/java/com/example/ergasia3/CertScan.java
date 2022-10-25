 package com.example.ergasia3;

 import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.regex.Pattern;

 public class CertScan extends AppCompatActivity {

     Button btn, btn2;
     TextView nameTxt, lastTxt, statusTxt;
     String name, lastName, status;
     EditText mailTxt;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_cert_scan);

         btn = findViewById(R.id.button);
         nameTxt = findViewById(R.id.textView7);
         lastTxt = findViewById(R.id.textView8);
         statusTxt = findViewById(R.id.textView10);
         mailTxt = findViewById(R.id.editTextTextEmailAddress);
         btn2 = findViewById(R.id.button2);
     }


     public void scan(View view) {
         // we need to create the object
         // of IntentIntegrator class
         // which is the class of QR library
         IntentIntegrator intentIntegrator = new IntentIntegrator(this);
         intentIntegrator.setPrompt(getString(R.string.scan_qr));
         intentIntegrator.setBarcodeImageEnabled(true);
         intentIntegrator.setCameraId(0);
         intentIntegrator.setBeepEnabled(false);
         intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
         intentIntegrator.initiateScan();

     }


     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
         // if the intentResult is null then
         // toast a message as "cancelled"
         if (intentResult != null) {
             if (intentResult.getContents() == null) {
                 Toast.makeText(getBaseContext(), R.string.cancelled, Toast.LENGTH_SHORT).show();
             } else {
                 showMessage(getString(R.string.result), intentResult.getContents());
             }
         } else {
             super.onActivityResult(requestCode, resultCode, data);
         }
     }

     void showMessage(String title, String message) {
         new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
     }


     public void searchMail(View view) {

         lastTxt.setText("");
         nameTxt.setText("");
         statusTxt.setText("");

         if (!Pattern.matches("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", mailTxt.getText())) {
             showMessage(getString(R.string.error), getString(R.string.invalid_email));
             return;
         }


         FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").orderByChild("email").equalTo(mailTxt.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 if (snapshot.exists()){
                     for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                         CitizenModel citizen = childSnapshot.getValue(CitizenModel.class);

                         name = citizen.getFirstName().toUpperCase();
                         lastName = citizen.getLastName().toUpperCase();
                         status = citizen.getStatus().toUpperCase();

                         lastTxt.setText(lastName);
                         nameTxt.setText(name);
                         statusTxt.setText(status);
                     }
                 }else{
                     showMessage("Error", "Email not found");
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });


     }
 }