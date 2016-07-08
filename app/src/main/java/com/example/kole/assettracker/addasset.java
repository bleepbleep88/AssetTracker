package com.example.kole.assettracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class addasset extends MainActivity {
    //add mEdit variable
    EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addasset);
        //initialize mEdit
        mEdit   = (EditText)findViewById(R.id.editText);
    }
    //called when user clicks the "add" button
    //used examples https://stackoverflow.com/questions/14785806/android-how-to-make-an-activity-return-results-to-the-activity-which-calls-it
    public void addTheAsset(View view) throws FileNotFoundException {
        //turn edittext text into string checker
        String checker = mEdit.getText().toString();
        //checks to make sure the string isnt empty
        if (!checker.matches("")) {
            //pass the checker string back to MainActivity and uses it in the mainactivitie's OnActivityResult
            Intent intent = new Intent();
            intent.putExtra("Assetname",checker);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            //tell user to enter text if there is none
            Toast.makeText(getApplicationContext(), "Please enter text into the field", Toast.LENGTH_SHORT).show();

        }
    }
}
