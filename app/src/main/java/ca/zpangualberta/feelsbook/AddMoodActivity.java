package ca.zpangualberta.feelsbook;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.Date;

public class AddMoodActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private String purpose; // for this activity to now that the user wants to do
    private EditText commentText;
    private Spinner moodChooser; // a spinner for user to choose a mood from 6 moods.
    private String datetime;  // a string for storing time in form of string
    private String spinnerText; // a string for storing text selected in the spinner(mood chooser)
    private Gson gson;
    private Calendar calendar = Calendar.getInstance(); // calendar was made for the need of choosing time or storing time in the Mood class.


    // ***************************************************************************************************************************************************************************************************************
    @SuppressLint("WrongViewCast")   // this line was added automatically
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_mood);

        // Pass the purpose which will be used to determine if the past comment will be in the box or not.
        Intent intent = getIntent();
        purpose = intent.getStringExtra("purpose");

        //  Create and Handle the spinner
        gson = new Gson();
        Button delete = findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("is_deleted",true);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        moodChooser = findViewById(R.id.moodChooser);
        ArrayAdapter<CharSequence> moodArrayAdapter = ArrayAdapter.createFromResource(this, R.array.moods, android.R.layout.simple_selectable_list_item);
        moodArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodChooser.setAdapter(moodArrayAdapter);
        moodChooser.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        //  End of spinner Handling





        // Save button & comment box & defult time setup.
        final Button saveButton = findViewById(R.id.saveButton);
        commentText = findViewById(R.id.commentText);
        SimpleDateFormat timeformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        datetime = timeformat.format(calendar.getTime());
        // End of setting up





        //Date button handling
        ImageButton dateButton = findViewById(R.id.dateChanger);
        final Activity that = this;
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  final Calendar c = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month  = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datepicker = new DatePickerDialog(that, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat timeformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        datetime = timeformat.format(calendar.getTime());
                    }
                },year,month,day);
                datepicker.show();
            }
        });
        // End of date button handling






        //time Button handling
        ImageButton timeButton = findViewById(R.id.timeChanger);
        final Activity another = this;
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timepicker = new TimePickerDialog(another, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        SimpleDateFormat timeformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        datetime = timeformat.format(calendar.getTime());
                    }
                },hour,minute,true);
                timepicker.show();
            }
        });
        // End of time button handling






        // save Button handling
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String com = commentText.getText().toString();
                String fel = spinnerText.toString();

                Mood newMood = new Mood(fel,datetime.toString(),com);
                String  mood_str = gson.toJson(newMood);
                Intent intent = new Intent();
                intent.putExtra("result",mood_str);
                setResult(RESULT_OK,intent);
                finish();
            }
        });





        // When editing, fetch the comment back into the box and get the spinner to the position the user selected previously
        if (purpose.equals("edit")){
            Mood mood = gson.fromJson(intent.getStringExtra("content"),Mood.class);
            String oldfeel = mood.getFeel();   // old feel is the feel the user chose previously
            int wantedPosition = spinnerPositionHandler(oldfeel); // spinnerPositionHandler will return the position on spinner for the coresponding feeling.
            moodChooser.setSelection(wantedPosition);
            commentText.setText(mood.getComment());
        }


        // New mood handling
        if (purpose.equals("add")){
            delete.setVisibility(View.GONE);
        }
    }  // End of On create


    // ***************************************************************************************************************************************************************************************************************
    public Integer spinnerPositionHandler (String feel){  // simple method for getting the spinner's coresponding position of the Feeling.
        int position=0;
        switch (feel) {
            case "LOVE":
                position = 0;
                break;
            case "JOY":
                position = 1;
                break;
            case "SURPRISE":
                position = 2;
                break;
            case "ANGER":
                position = 3;
                break;
            case "SADNESS":
                position = 4;
                break;
            case "FEAR":
                position = 5;
                break;
        }
        return position;
    }




    @Override
    // Handle the selecting of the spinner
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerText = parent.getItemAtPosition(position).toString();
       // Toast.makeText(parent.getContext(), spinnerText, Toast.LENGTH_SHORT).show();
    }

    @Override
    // NothingSelected case is not handled
    public void onNothingSelected(AdapterView<?> parent) {
    }

// end of the Activity !
}

