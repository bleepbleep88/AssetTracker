package com.example.kole.assettracker;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class addsubasset extends AppCompatActivity {


    //Add lots of variables to edit throughout the script
    EditText name;
    EditText location;
    EditText price;
    EditText main;
    String locationtext;
    String nametext;
    String pricetext;
    String maintext;

    //Would use these but I couldn't get permissions to work. spent almost a day on it before I moved on to finish other things
   // LocationManager mLocationManager;
    //private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    //CheckBox check;
//    Location locationGPS;
//    String longitude;
//    String latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsubasset);
        //initialize the edittexts
        name = (EditText) findViewById(R.id.editText);
        location = (EditText) findViewById(R.id.editLongitude);
        price = (EditText) findViewById(R.id.editPrice);
        main = (EditText) findViewById(R.id.editMain);
    }

    public void addTheSubasset(View view) throws FileNotFoundException {
            //get all the text from the edittext fields
            nametext = name.getText().toString();
            locationtext = location.getText().toString();
            pricetext = price.getText().toString();
            maintext = main.getText().toString();
            //make sure the text fields aren't empty
        if(!nametext.isEmpty() && !pricetext.isEmpty() && !locationtext.isEmpty()){
            //checks to make sure maintext isn't empty
            if(!maintext.isEmpty()){
                //add the edittext fields text to the intent and pass it back to listclick
                Intent intent = new Intent();
                intent.putExtra("Assetname",nametext);
                intent.putExtra("Assetprice",pricetext);
                intent.putExtra("Assetlocation",locationtext);
                intent.putExtra("Assetmain",maintext);
                setResult(RESULT_OK, intent);
                finish();

            }
            //add the edittext fields text, except for the maintext, to the intent and pass it back to listclick
            Intent intent = new Intent();
            intent.putExtra("Assetname",nametext);
            intent.putExtra("Assetprice",pricetext);
            intent.putExtra("Assetlocation",locationtext);
            //put placeholder d since the textfield is optional
            intent.putExtra("Assetmain","d");
            setResult(RESULT_OK, intent);
            finish();

        }else{
            //tell user to enter text if there isn't any in one or more of the 3 required fields
            Toast.makeText(getApplicationContext(), "Please enter text into the fields", Toast.LENGTH_LONG).show();
        }

    }
}
