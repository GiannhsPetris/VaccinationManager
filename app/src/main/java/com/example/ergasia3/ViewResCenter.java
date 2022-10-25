package com.example.ergasia3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ViewResCenter extends AppCompatActivity {

    ArrayList<DateModel> model ;
    RecyclerView rec;
    DateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_res_center);

        rec = findViewById(R.id.rec);
        loadData();
        String str = model.get(0).getCenterId();

        // calling method to build
        // recycler view.
        buildRecyclerView();
    }


    // method to show messages on screen
    void showMessage(String title, String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("Appointments", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<DateModel>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        model = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (model == null) {
            // if the array list is empty
            // creating a new array list.
            model = new ArrayList<>();
        }
    }
    

    private void buildRecyclerView() {
        // initializing our adapter class.
        adapter = new DateAdapter(ViewResCenter.this, model, this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rec.setHasFixedSize(true);

        // setting layout manager to our recycler view.
        rec.setLayoutManager(manager);

        // setting adapter to our recycler view.
        rec.setAdapter(adapter);
    }
}