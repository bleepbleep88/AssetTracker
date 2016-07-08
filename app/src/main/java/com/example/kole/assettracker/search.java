package com.example.kole.assettracker;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


public class search extends ListActivity {
    //Add variables, allowing them to be changed anywhere in the java file
    android.widget.ListView list;
    EditText searchInput;
    JsonArray arrayofjson = new JsonArray();
    List<String> assets = new ArrayList<String>();
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchInput = (EditText) findViewById(R.id.editsearcher);
        list = (android.widget.ListView) findViewById(android.R.id.list);
        file = new File(getFilesDir().toString() + "/" + "super");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, assets);
        list.setAdapter(adapter);

        //activates when you press done on the edittext field
        //used example https://stackoverflow.com/questions/5099814/knowing-when-edit-text-is-done-being-edited
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //search the json after user is done editing text
                    search();

                    return true;
                }
                return false;
            }
        });
    }
    //SpaghettiCode/10
    //Reads your input from the EditText and corresponds to what you give it
    public void search(){
        //put the edittext text into a string
        String searched = searchInput.getText().toString();
        //remove all the assets currently in the assets list so we don't just keep adding assets every search
        assets.removeAll(assets);

        try {
            //Initialize the parser
            JsonParser parser = new JsonParser();
            //set the arrayofjson to the JSON file's contents
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));
            //make sure there is text in the edittext through the searched variable
            if(!searched.isEmpty()) {
                //For as long as how many objects are in the arrayofjson
                for (Object o : arrayofjson) {
                    //set the current object in arrayofjson to asset and initialize properties string
                    JsonObject asset = (JsonObject) o;
                    String properties = "";
                    //Make sure we're dealing with assets and not asset lists that you would add on the first screen
                    //We do this by checking if name is not null, if it is we would skip the object and move to the next
                    //We also make sure we're only getting assets searched for by comparing names and the edittext search with .contains
                    if (asset.get("Name") != null && asset.get("Name").getAsString().contains(searched)) {
                        //make sure Main is not null, if it is then we skip it and only add the three properties
                        if(asset.get("Main") != null){
                            //make sure Main is not the string that is added for when no one inputs the Maintenance price in the field,
                            //if it is then we skip it and only add the three properties
                            if(!asset.get("Main").getAsString().equals("d")){
                                //add all the object asset's properties to one string
                                properties =
                                        "Asset: "+asset.get("Name").getAsString() + System.lineSeparator() +
                                                "Price: "+asset.get("Price").getAsString() + System.lineSeparator() +
                                                "Location: "+ asset.get("Location").getAsString()+System.lineSeparator() +
                                                "Maintenance: "+ asset.get("Main").getAsString();
                            }else{
                                //add all the object asset's properties except Maintenance to one string
                                properties =
                                        "Asset: "+asset.get("Name").getAsString() + System.lineSeparator() +
                                                "Price: "+asset.get("Price").getAsString() + System.lineSeparator() +
                                                "Location: "+ asset.get("Location").getAsString();
                            }
                        }else{
                            //add all the object asset's properties except Maintenance to one string
                            properties =
                                    "Asset: "+asset.get("Name").getAsString() + System.lineSeparator() +
                                            "Price: "+asset.get("Price").getAsString() + System.lineSeparator() +
                                            "Location: "+ asset.get("Location").getAsString();
                        }
                        //add the string to the assets list for the ListView
                        assets.add(properties);
                    }

                }
            }else{
                //this activates if there was no text
                //this adds a toast to the top of the screen and notifies the adapter it has changed since we removed all assets from assets
                //used example https://stackoverflow.com/questions/13988596/show-toast-above-keyboard
                Toast toast= Toast.makeText(this,"Please enter text into the field",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) list.getAdapter();
                adapter.notifyDataSetChanged();
                return;

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Display toast if no assets were found
        if(assets.size() == 0){
            Toast toast= Toast.makeText(this,"No assets found",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

        }
        //notify the adapter it has changed
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) list.getAdapter();
        adapter.notifyDataSetChanged();

    }
}
