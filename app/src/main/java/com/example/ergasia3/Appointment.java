package com.example.ergasia3;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class Appointment extends AppCompatActivity {

    Button finalBtn, firstdate, secondDate;
    TextView date1, date2, name, mail, address, telephone;
    CenterModel center;
    LocalDate localDate1, localDate2;
    int checkFinal;
    Boolean flag = true;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String UID = user.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        finalBtn = findViewById(R.id.button9);
        firstdate = findViewById(R.id.button5);
        secondDate = findViewById(R.id.button7);
        date1 = findViewById(R.id.textView28);
        date2 = findViewById(R.id.textView33);
        name = findViewById(R.id.vaccName);
        mail = findViewById(R.id.vacMail);
        address = findViewById(R.id.vacAd);
        telephone = findViewById(R.id.vactel);

        checkFinal = 0;
        finalBtn.setEnabled(false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String string = sharedPreferences.getString("Vaccination Center", getString(R.string.not_found));
        Gson gson = new Gson();
        center = gson.fromJson(string, CenterModel.class);

        searchDB();

        try {
            name.setText(center.getName());
            mail.setText(center.getEmail());
            address.setText(center.getAddress());
            telephone.setText(center.getTelephone());
        }catch (Exception e){
            showMessage(getString(R.string.error), getString(R.string.try_again));
            // adds a delay between the message and the redirection to the front page
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
            }, 5000);
        }

    }


    public void setVaccdate(View view1){

        Calendar calendar = Calendar.getInstance();
        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DAY = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year);
                calendar1.set(Calendar.MONTH, month);
                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                CharSequence dateChar = DateFormat.format("EEEE,\n dd MMM yyyy", calendar1);
                CharSequence dateChar2 = DateFormat.format("yyyy-MM-dd", calendar1);

                String date = String.valueOf(year)+String.valueOf(month)+String.valueOf(dayOfMonth);

                if (view1.getTag().toString().equals("1")){
                    localDate1 = LocalDate.parse(dateChar2);
                    date1.setText(dateChar);
                    checkFinal +=1;
                }else{
                    date2.setText(dateChar);
                    localDate2 = LocalDate.parse(dateChar2);
                    checkFinal +=1;
                }
                if(checkFinal >= 2){finalBtn.setEnabled(true);}
            }
        }, YEAR, MONTH, DAY);

        datePickerDialog.show();

    }


    public void makeAppointment(View view){

        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        if (localDate1.isBefore(today)) {
            showMessage(getString(R.string.error), getString(R.string.old_date));
            return;
        }

        if (localDate2.isBefore(localDate1.plusDays(27))) {
            showMessage(getString(R.string.error), getString(R.string.days_apart));
            return;
        }


        if(flag){
            showMessage("Vaccination Complete","                      ");
            saveDb();
        }else {
            showMessage(getString(R.string.error),getString(R.string.already_appointment));
        }

    }

    void searchDB(){
        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("reservations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()){
                    DateModel dateModel = childSnapshot.getValue(DateModel.class);
                    String uidDB = dateModel.getCitizenUid();

                    try{
                        if(uidDB.equals(UID)) {
                            flag= false;
                            return;
                        }
                    }catch (Exception e){
                        showMessage(getString(R.string.error), getString(R.string.user_not_found));
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void saveDb(){
        DateModel model = new DateModel();
        model.setCitizenUid(UID);
        model.setCenterId(center.getId());
        model.setDate1(localDate1.toString());
        model.setDate2(localDate2.toString());
        model.setFirstDose("Not Done");
        model.setSecondDose("Not Done");

        finalBtn.setEnabled(false);
        String str = UID + center.getId();

        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("reservations").child(str).setValue(model);
    }


    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }
}