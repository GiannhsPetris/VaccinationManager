package com.example.ergasia3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {


    private Context context;
    private ArrayList<DateModel> dateList;
    CitizenModel citizen;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String UID = user.getUid();


    public DateAdapter(Context context, ArrayList<DateModel> dateList, ViewResCenter viewResCenter) {
        this.context = context;
        this.dateList = dateList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView nameTxt, emailTxt, firstDateTxt, firstStatusTxt, secondDateTxt, secondStatusTxt;
        private Button firstBtn, secondBtn;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.textView57);
            emailTxt = itemView.findViewById(R.id.textView58);
            firstDateTxt = itemView.findViewById(R.id.textView59);
            firstStatusTxt = itemView.findViewById(R.id.textView60);
            secondDateTxt = itemView.findViewById(R.id.textView61);
            secondStatusTxt = itemView.findViewById(R.id.textView62);
            firstBtn = itemView.findViewById(R.id.button6);
            secondBtn = itemView.findViewById(R.id.button8);
        }
    }

    @Override
    public DateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_reservations, parent, false);
        return new DateAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateAdapter.MyViewHolder holder, int position) {
        // takes the object from the list of objects
        DateModel model = dateList.get(position);

        holder.firstDateTxt.setText(model.getDate1());
        holder.firstStatusTxt.setText(model.getFirstDose());
        holder.secondDateTxt.setText(model.getDate2());
        holder.secondStatusTxt.setText(model.getSecondDose());
        if (holder.firstStatusTxt.getText().equals("Not Done")){
            holder.secondBtn.setEnabled(false);
            holder.firstBtn.setEnabled(true);
        }else if(holder.secondStatusTxt.getText().equals("Done")){
            holder.secondBtn.setEnabled(false);
            holder.firstBtn.setEnabled(false);
        }else{
            holder.secondBtn.setEnabled(true);
            holder.firstBtn.setEnabled(false);
        }

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate date1 = LocalDate.parse(model.getDate1());
        LocalDate date2 = LocalDate.parse(model.getDate2());

        holder.firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (today.isBefore(date1)) {
                    showMessage("Error", "The timestamp of the dose is of a day in the future");
                    return;
                }else{
                    holder.firstStatusTxt.setText("Done");
                    holder.secondBtn.setEnabled(true);
                    holder.firstBtn.setEnabled(false);
                    updateAppointmentStatus(model, citizen, true);
                }
            }
        });

        holder.secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (today.isBefore(date2)) {
                    showMessage("Error", "The timestamp of the dose is of a day in the future");
                    return;
                }else{
                    holder.secondStatusTxt.setText("Done");
                    holder.secondBtn.setEnabled(false);
                    updateAppointmentStatus(model, citizen, false);
                }
            }
        });



        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").child(model.getCitizenUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                citizen = task.getResult().getValue(CitizenModel.class);

                holder.nameTxt.setText(citizen.getFirstName()+ " " + citizen.getLastName());
                holder.emailTxt.setText(citizen.getEmail());

            }
        });


    }
    void updateAppointmentStatus(DateModel model, CitizenModel citizen, Boolean mode){
        String id = model.getCitizenUid()+UID;
        DatabaseReference updateData =  FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("reservations").child(id);
        DatabaseReference updateData2 =  FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").child(model.getCitizenUid());
        if (mode){
            updateData.child("firstDose").setValue("Done");
        }else{
            updateData.child("secondDose").setValue("Done");
            updateData2.child("status").setValue("Vaccinated");
        }
    }

    void showMessage(String title, String message){
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }
}
