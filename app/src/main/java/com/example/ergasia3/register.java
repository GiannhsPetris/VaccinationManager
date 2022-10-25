package com.example.ergasia3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class register extends AppCompatActivity {

    Button signBtn;
    ImageView nameImg, lastImg, mailImg, passImg, phoneImg, dateImg;
    EditText nameText, lastText, mailText, phoneText, passText, dateText;
    TextView goLog;
    FirebaseAuth fireAuth;

    private Integer code;
    public static final int AUDIO_RECORD_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseAuth.getInstance().signOut();

        signBtn = findViewById(R.id.signButton);
        goLog = findViewById(R.id.goLog);

        nameImg = findViewById(R.id.imageViewN);
        lastImg = findViewById(R.id.imageViewL);
        mailImg = findViewById(R.id.imageViewE);
        dateImg = findViewById(R.id.imageViewD);
        passImg = findViewById(R.id.imageViewP);
        phoneImg = findViewById(R.id.imageViewPh);

        nameText = findViewById(R.id.editTextName);
        lastText = findViewById(R.id.editTextName2);
        mailText = findViewById(R.id.editTextEmail);
        dateText = findViewById(R.id.editTextDate);
        passText = findViewById(R.id.editTextPassword);
        phoneText = findViewById(R.id.editTextPhone);

        signBtn.setEnabled(false); // disable so it cannot be pressed before entering info on the text fields
        ArrayList<Boolean> checkList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            checkList.add(false);
        }

        fireAuth = FirebaseAuth.getInstance();


        //takes you to the log in page
        goLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });


        nameText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    checkList.set(0, false);
                    signBtn.setEnabled(false);
                } else {
                    checkList.set(0, true);
                    if (!checkList.contains(false)) {
                        signBtn.setEnabled(true);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        lastText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    checkList.set(1, false);
                    signBtn.setEnabled(false);
                } else {
                    checkList.set(1, true);
                    if (!checkList.contains(false)) {
                        signBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        mailText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    checkList.set(2, false);
                    signBtn.setEnabled(false);
                } else {
                    checkList.set(2, true);
                    if (!checkList.contains(false)) {
                        signBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        passText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    checkList.set(3, false);
                    signBtn.setEnabled(false);
                } else {
                    checkList.set(3, true);
                    if (!checkList.contains(false)) {
                        signBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        phoneText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    checkList.set(4, false);
                    signBtn.setEnabled(false);
                } else {
                    checkList.set(4, true);
                    if (!checkList.contains(false)) {
                        signBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        dateText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    checkList.set(5, false);
                    signBtn.setEnabled(false);
                } else {
                    checkList.set(5, true);
                    if (!checkList.contains(false)) {
                        signBtn.setEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
    }

    public void signup(View view) throws ParseException {

        if (!Pattern.matches("^(\\d{3}[- .]?){2}\\d{4}$", phoneText.getText())) {
            showMessage("Error", "Wrong Phone Format. Enter a Valid Phone Number.");
            return;
        }
        if (!Pattern.matches("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}$", dateText.getText())) {
            showMessage("Error", "Wrong Date Format. Enter a Valid Date.");
            return;
        } else {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String var = dateText.getText().toString();
            LocalDate dateObject = formatter.parse(var).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Period p = Period.between(dateObject, today);
            Integer age = p.getYears();
            if (age > 99 || age < 18) {
                showMessage("Error", "Wrong Date of Birth. Enter a Valid Date. \n You must be between 18 and 99 to enter");
                return;
            }
        }

        fireAuth.createUserWithEmailAndPassword(mailText.getText().toString(), passText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveToDb();
                            showMessage("Success", "User Created");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            showMessage("Error", task.getException().getLocalizedMessage());
                        }
                    }
                });


    }

    void showMessage(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(true).show();
    }

    void saveToDb() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String UID = user.getUid();

        CitizenModel citizen = new CitizenModel();
        citizen.setDateOfBirth(dateText.getText().toString());
        citizen.setEmail(mailText.getText().toString());
        citizen.setFirstName(nameText.getText().toString());
        citizen.setLastName(lastText.getText().toString());
        citizen.setTelephone(phoneText.getText().toString());
        citizen.setStatus("Unvaccinated");


        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("citizens").child(UID).setValue(citizen);

        FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(UID).setValue("citizen");
    }

    // called when user presses speech recognition btn
    public void voiceRecReg(View view) {
        code = Integer.valueOf(view.getTag().toString()); // takes id of button
        checkPerm(Manifest.permission.RECORD_AUDIO, AUDIO_RECORD_REQUEST_CODE);
    }

    //check the permission for audio record if is already given starts speech recognition if not asks for perm
    void checkPerm(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(register.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(register.this, new String[]{permission}, requestCode);
        } else {
            voiceStart();
        }
    }

    // if permission given starts speech rec if not notifies the user that the perm must be given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AUDIO_RECORD_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                voiceStart();
            } else {
                showMessage("Permission Denied", "Please Give The Permission To Use This Feature");
            }
        }
    }

    // initiates speech recognition
    void voiceStart() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "For '@' say At Sign for '_' underscore and for '.' say dot");
        startActivityForResult(intent, code);
    }

    // takes the result and inputs it at the right textfield using the id var code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            // removes unwanted characters from result
            String txt = matches.toString().replace(" ", "").replace("[", "").replace("]", "").replace("atsign", "@");

            if (requestCode == 1) {
                nameText.setText(txt);
            } else if (requestCode == 2) {
                lastText.setText(txt);
            } else if (requestCode == 3) {
                mailText.setText(txt);
            } else if (requestCode == 4) {
                passText.setText(txt);
            } else if (requestCode == 5) {
                phoneText.setText(txt);
            } else {
                dateText.setText(txt);
            }

        } else {
            showMessage("Error", "Something Went Wrong. Please Try Again");
        }
    }
}
