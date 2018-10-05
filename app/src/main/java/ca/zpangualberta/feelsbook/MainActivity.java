package ca.zpangualberta.feelsbook;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ADD = 1;  //This value will help handling the case where the user wants to edit his/her mood.   The idea was taken From Jasonnngao on github.
    final private int REQUEST_CODE_EDIT = 2; // Same as above
    private static final String FILENAME = "file.sav";  // File_name for storing data
    private ArrayList<Mood> moods; // This will store moods(for feelings) created in AddMoodActivity.
    private ListView moodListView; // This is the ListView used to show all the data.
    private ArrayAdapter<Mood> moodArrayAdapter; // This is Adapter of the ListView to help showing and updating the date changes.
    private Gson gson;
    private int indexEdited; // This helps the program knowing which emotion to edit.  The idea was taken from Jasonnngao on github.

  // ***************************************************************************************************************************************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Seting up everything
        gson = new Gson();
        loadFromFile();  // Load Saved file (if file exists)
        moodListView = findViewById(R.id.moodList);
        moodArrayAdapter = new ArrayAdapter<Mood>(this,R.layout.list_item,moods);
        //End of setting up everything


        //Handling the clicking of the list view.(The main thing that displays all the feelings)
        final Activity that = this; // Intent intent = new Intent(this,AddMoodActivity.class); was used but an error showed up, This line of code was the sulotion to it.  I don't know why and how it works.
        moodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // If a feeling of the Feeling list is clicked:
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(that,AddMoodActivity.class);  //go to AddMoodActivity
                intent.putExtra("purpose","edit");  //  tell the Add Mood Activity that the user wants to edit.
                indexEdited = i; // tell the app which feeling(item) the user wants to edit
                intent.putExtra("content",gson.toJson(moods.get(i))); //Go to Json and grab the item the user wants to edit
                startActivityForResult(intent,REQUEST_CODE_EDIT);  // gat back when finished.


            }
        });
        //End of handling the clicking of the list view.



        goAddMood(); // allow the user to go Add Feeling through clicking the ADD MOOD button
        seeDetail(); // Toast the counter information when the counter button is clicked

    }



    //******************************************************************************************************

    protected void onStart() {
        super.onStart();
        if ((moods.size()) > 1) {
            sortDatArray(); // // Sort That Array, this will make sure the feelings will show in the order of time.  The arrayList moods will be sorted in this method.
        }
        moodListView.setAdapter(moodArrayAdapter); // Let the user see the change of the data
    }




   // *****************************************************************************************************************



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {   // When the REQUEST_CODE_EDIT or REQUEST_CODE_ADD is passed, get thins ready for going to the AddMoodActivity.  The idea was taken from Jsonnnngao on github
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){ // and if there is nothing wrong during the AddMoodActivity
            if (requestCode == REQUEST_CODE_ADD){ // and the user wanted to add, do the flowing
                Mood mood = gson.fromJson(data.getStringExtra("result"),Mood.class);
                moods.add(mood);
            }

            else if(requestCode == REQUEST_CODE_EDIT){ // or the user wanted to edit, do the flowing
                if (data.getBooleanExtra("is_deleted",false)){
                    moods.remove(indexEdited);
                } else {
                    moods.set(indexEdited, gson.fromJson(data.getStringExtra("result"),Mood.class));
                }
            }

            saveInFile(); // save the changes to the data file
        }
    }

    private void goAddMood(){ // allow the user to goto the AddMoodActivity
        Button addMoodButton =  findViewById(R.id.addMoodButton);
        addMoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // blow is the code for moving to another activity
                Intent intent= new Intent(MainActivity.this,AddMoodActivity.class);
                intent.putExtra("purpose","add");
                startActivityForResult(intent,REQUEST_CODE_ADD);
            }
        });
    }

    private void seeDetail(){// Give the counter data when DETAIL button is clicked.
        Button detailButton = findViewById(R.id.detailButton);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> counterList = new ArrayList<Integer>(6){{  // set up an integer list for counting the number of feelings. , and initialize the list to a bunch of zeros.
                    add(0);
                    add(0);
                    add(0);
                    add(0);
                    add(0);
                    add(0);
                }};
                for (int i=0; i<moods.size(); i++) { // go to the ArrayList of feelings
                    Mood singleMood = moods.get(i);  // and check every item it has,
                    String feel_str = singleMood.getFeel();  // and for each of it's items, get the Feel it stored.
                    switch (feel_str) { // For every Feeling it finds, add 1 on the coresponding indes of the integer list conterList.
                        case "LOVE":
                            counterList.set(0, (counterList.get(0) + 1));
                            break;
                        case "JOY":
                            counterList.set(1, (counterList.get(1) + 1));
                            break;
                        case "SURPRISE":
                            counterList.set(2, (counterList.get(2) + 1));
                            break;
                        case "ANGER":
                            counterList.set(3, (counterList.get(3) + 1));
                            break;
                        case "SADNESS":
                            counterList.set(4, (counterList.get(4) + 1));
                            break;
                        case "FEAR":
                            counterList.set(5, (counterList.get(5) + 1));
                            break;
                    }

                }
                String str=stringCounter(counterList); // str is the name of what will be displayed to the user.
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
            }
        });
    }



    private String stringCounter(List<Integer> counterList) {  // This function will make the final string for displaying readable for human.
        String str = "LOVE: "+ counterList.get(0) + "\n\n"+
                "JOY: " + counterList.get(1) + "\n\n"+
                "SURPRISE: " + counterList.get(2) + "\n\n"+
                "ANGER: " + counterList.get(3) + "\n\n"+
                "SADNESS: " + counterList.get(4) + "\n\n"+
                "FEAR: " + counterList.get(5) + "\n\n";
        return str;
    }


    // Learned From stackoverflow.com;  Author Pranav
    private void sortDatArray (){  // simple method of sorting an Array List that contains objects.
            Collections.sort(moods, new Comparator<Mood>() {
                @Override
                public int compare(Mood o1, Mood o2) {

                    return Integer.valueOf((o1.getDate()).compareTo(o2.getDate()));
                }
            });
    }

 //***************************************************************************************************************************************************************************************************************





    public void saveInFile() {  // save file method for gson
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
                    0);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(moods, writer);
            writer.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void loadFromFile() {  // load file method for gson
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            // Following line based on https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/Gson.html retrieved 2015-09-21
            Type listType = new TypeToken<ArrayList<Mood>>() {
            }.getType();
            moods = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            moods = new ArrayList<>();
        }
    }


}
