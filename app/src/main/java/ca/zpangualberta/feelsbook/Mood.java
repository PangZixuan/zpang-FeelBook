package ca.zpangualberta.feelsbook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mood {
    private String feel;
    private String date;
    private String comment;

// Creates the class Mood to store a single mood, this will be stored in an ArrayList of objects in the Main Activity.

    public Mood(String feel, String date, String comment) {
        this.feel = feel;
        this.date = date;
        this.comment = comment;
    }

    public String getFeel(){ return feel; }

    public void setFeel(String feel){ this.feel = feel;}

    public String getDate() {
        return date;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment){ this.comment = comment;}


    public String toString() {
        return "Feel: " + feel + "\n"+
                "Date: " + date + "\n" +
                "Comment: " + comment;

    }



}
