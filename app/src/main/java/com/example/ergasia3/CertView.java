package com.example.ergasia3;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


public class CertView extends AppCompatActivity {

    Button btn;
    ImageView qrImg;
    TextView nameTxt, lastTxt, statusTxt;
    String name, lastName, status;
    String qrTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_view);

        btn = findViewById(R.id.button);
        qrImg = findViewById(R.id.imageView);
        nameTxt = findViewById(R.id.textView7);
        lastTxt = findViewById(R.id.textView8);
        statusTxt = findViewById(R.id.textView10);

    }

    public void generate(View view){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String UID = user.getUid();
        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    CitizenModel citizen = dataSnapshot.getValue(CitizenModel.class);

                    name = citizen.getFirstName().toUpperCase();
                    lastName = citizen.getLastName().toUpperCase();
                    status = citizen.getStatus().toUpperCase();

                    lastTxt.setText(lastName);
                    nameTxt.setText(name);
                    statusTxt.setText(status);
                    qrTxt = name + " " + lastName + "\nStatus: " + status;

                    try{
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        BitMatrix bitMatrix = multiFormatWriter.encode(qrTxt, BarcodeFormat.QR_CODE,500,500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        qrImg.setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    showMessage(getString(R.string.error), getString(R.string.log_out_try));
                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showMessage(getString(R.string.error), getString(R.string.user_not_found));
                return;
            }
        });

    }



    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}