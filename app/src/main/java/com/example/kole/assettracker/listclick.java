package com.example.kole.assettracker;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class listclick extends ListActivity {
    //Add variables, allowing them to be changed anywhere in the java file
    android.widget.ListView list;
    boolean delete = false;
    FloatingActionButton fabadd;
    FloatingActionButton fabdelete;

    JsonArray arrayofjson = new JsonArray();
    List<String> assets = new ArrayList<String>();
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intialize
        setContentView(R.layout.activity_listclick);
        list = (ListView) findViewById(android.R.id.list);
        file = new File(getFilesDir().toString() + "/" + "super");
        //read the file and file the assets list
        parsedaJson();
        //set the adapter to use assets and set it to the list
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, assets);
        list.setAdapter(adapter);
        //initialize the floating action buttons,(FABs)
        fabadd = (FloatingActionButton) findViewById(R.id.fabadd);
        fabdelete = (FloatingActionButton) findViewById(R.id.fabdelete);
        //listen for item clicks
        //used example https://stackoverflow.com/questions/5716599/how-to-set-onlistitemclick-for-listview-in-android
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        // if delete is enabled delete the selected object, if it isn't then do nothing
                        if(delete) {
                            //get selected item and make itemtouched the string of it
                            Object o = list.getItemAtPosition(position);
                            String itemtouched = o.toString();
                            //add the touched object to obj through a method
                            JsonObject obj = checkforObject(itemtouched);
                            //make sure the object returned isn't null, if it is then we do nothing
                            if(obj != null) {
                                //initialize object properties into strings
                                String name = obj.get("Name").getAsString();
                                String price = obj.get("Price").getAsString();
                                String location = obj.get("Location").getAsString();
                                String main = "d";
                                //checks if the object propety "Main" is null or not
                                if(obj.get("Main") != null){
                                    //checks if the object propety "Main" contains d, note "Main" can only contain numbers, unless it wasn't inputted
                                    //when the user made the asset, as it is optional
                                    if(!obj.get("Main").getAsString().contains("d")) {
                                        //give string main the string property of "Main" from the JsonObject obj
                                        main = obj.get("Main").getAsString();
                                    }
                                }
                                //start the object deleter, write to json and read the JSON file after it's all done so the listview will update
                                deleter(name, price, location, main);
                                writetojson(arrayofjson);
                                reader();
                            }

                        }

                    }
                }
        );

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    //hide the buttons
                    fabadd.hide(true);
                    //don't hide the delete button if delete is true
                    if(!delete)
                    fabdelete.hide(true);


                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    //show the buttons if delete is not true
                    if(fabadd.isHidden() && !delete) {
                        fabadd.show(true);
                        fabdelete.show(true);
                    }

                }
                mLastFirstVisibleItem=firstVisibleItem;

            }
        });
    }

    //the whole method reads items from the Json array, and then puts items from it into the assets list
    public void parsedaJson() {
        try {
            //initialize the parser, and make arrayofjson all the objects in the JSON file
            JsonParser parser = new JsonParser();
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));
            //for as long as there are objects in arrayofjson
            for (Object o : arrayofjson) {
                //set the current object in arrayofjson to asset and initialize properties string
                JsonObject asset = (JsonObject) o;
                String properties = "";
                //Make sure we're dealing with assets and not asset lists that you would add on the first screen
                //We do this by checking if name is not null, if it is we would skip the object and move to the next
                //We also make sure we're only getting assets in the current asset list by comparing the "Asset" value between them
                if (asset.get("Name") != null && asset.get("Asset").getAsString().contentEquals(getIntent().getStringExtra("asset"))) {
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
                }//end of initial if

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    //start the addsubasset activity
    public void addSub(View view){
        Intent startNewActivity = new Intent(this, addsubasset.class);
        int requestCode = 0;
        startActivityForResult(startNewActivity, requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //check result
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //add data from the edittext fields to variables
                String namem = data.getStringExtra("Assetname");
                String pricebefore = data.getStringExtra("Assetprice");
                float price = Integer.parseInt(pricebefore);
                String Location = data.getStringExtra("Assetlocation");
                String main = data.getStringExtra("Assetmain");
                //checks if the Maintenance field got filled or not, if it didn't get filled then pass main as null in addJson1
                if(data.getStringExtra("Assetmain").equals("d")) {
                    addJson1(namem, Location, null, price);
                }else{
                    addJson1(namem, Location, main, price);
                }
                //write and read to update the list
                writetojson(arrayofjson);
                reader();
            }
        }
    }
    //adds object to arrayofjson
    public void addJson1(String name, String location, String main, float price){
        //initalize obj and give it the properties from the edittext fields
        JsonObject obj = new JsonObject();
            obj.addProperty("Asset", getIntent().getStringExtra("asset"));
            obj.addProperty("Name", name);
            obj.addProperty("Price", price);
            obj.addProperty("Location", location);
        //checks if main isn't null, if it isn't null then add the Main property
        if(main != null){
                obj.addProperty("Main", main);
        }
        //add the object to the arrayofjson
            arrayofjson.add(obj);

    }
    //writes a JSONArray to file
    public void writetojson(JsonArray array){

        try {
            JsonWriter writer2 = new JsonWriter(new FileWriter(file));
            Gson gson = new Gson();
            gson.toJson(array,writer2);
            writer2.flush();
            writer2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Same thing as parsedaJson, except it alerts the adapter it has changed
    public void reader() {

        Gson gson = new Gson();
        assets.removeAll(assets);
        try {
            JsonParser parser = new JsonParser();
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));

            for (Object o : arrayofjson) {
                JsonObject person = (JsonObject) o;
                String properties = "";
                String name = person.get("Asset").getAsString();
                if (person.get("Name") != null && person.get("Asset").getAsString().contentEquals(getIntent().getStringExtra("asset"))) {
                    if(person.get("Main") != null){
                        if(!person.get("Main").getAsString().equals("d")){
                            properties =
                                    "Asset: "+person.get("Name").getAsString() + System.lineSeparator() +
                                            "Price: "+person.get("Price").getAsString() + System.lineSeparator() +
                                            "Location: "+ person.get("Location").getAsString()+System.lineSeparator() +
                                            "Maintenance: "+ person.get("Main").getAsString();
                        }else{
                            properties =
                                    "Asset: "+person.get("Name").getAsString() + System.lineSeparator() +
                                            "Price: "+person.get("Price").getAsString() + System.lineSeparator() +
                                            "Location: "+ person.get("Location").getAsString();
                        }
                    }else{
                        properties =
                                "Asset: "+person.get("Name").getAsString() + System.lineSeparator() +
                                        "Price: "+person.get("Price").getAsString() + System.lineSeparator() +
                                        "Location: "+ person.get("Location").getAsString();
                    }
                    assets.add(properties);
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = (ArrayAdapter<String>) list.getAdapter();
        adapter.notifyDataSetChanged();


    }
    //checks to make sure the object is right
    public JsonObject checkforObject(String obj){

        try {
            //Initialize the parser and make arrayofjson equal to the JSON file's objects
            JsonParser parser = new JsonParser();
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));
            //for as long as how many objects are in the arrayofjson
            for (Object o : arrayofjson) {
                //set the current object in arrayofjson to asset and initialize properties string
                JsonObject asset = (JsonObject) o;
                String properties = "";
                //Make sure we're dealing with assets and not asset lists that you would add on the first screen
                //We do this by checking if name is not null, if it is we would skip the object and move to the next
                //We also make sure we're only getting assets in the current asset list by comparing the "Asset" value between them
                if (asset.get("Name") != null && asset.get("Asset").getAsString().contentEquals(getIntent().getStringExtra("asset"))) {
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
                    //checks if the string matches the one provided so it can return the correct object
                    //if it isn't the one provided then move on to the next object in the for loop
                    if(properties.matches(obj)){
                        return asset;
                    }


                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //will only make it here if it couldn't find the correct object
        Toast.makeText(this,"Couldn't find object", Toast.LENGTH_LONG);
        return null;
    }
    //change state of delete variable on delete fab click and animate other fabs
    public void changeDelete(View view){

        if(!delete){
            delete = true;

            fabadd.hide(true);

            Toast.makeText(getApplicationContext(), "Select an asset to delete it or select the trash icon again to cancel", Toast.LENGTH_LONG).show();
        }else{
            fabadd.show(true);
            delete = false;
        }

    }
    //deletes the file you touched in listview if delete is active
    public void deleter(String Assetname, String Assetprice, String Assetlocation, String Assetmain){

        try {
            //Initialize the parser and make arrayofjson equal to the JSON file's objects
            JsonParser parser = new JsonParser();
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));
            //make new jsonobject to find the object to remove
            JsonObject toRemove = new JsonObject();
            //for as long as how many objects are in the arrayofjson
            for (JsonElement o : arrayofjson) {
                //set the current object in arrayofjson to asset
                JsonObject asset = (JsonObject) o;
                //Make sure we're dealing with assets and not asset lists that you would add on the first screen by check if it has a name
                if (asset.get("Name") != null) {
                    //Initialize the asset strings
                    String name = asset.get("Name").getAsString();
                    String price = asset.get("Price").getAsString();
                    String location = asset.get("Location").getAsString();
                    //check if all the properties are equal to the touched listitems properties
                    if (Assetname.equals(name) && Assetprice.equals(price) && Assetlocation.equals(location)) {
                        //check if the objects main property is null or not, if it isn't then make toRemove the current asset
                        if(asset.get("Main")!=null){
                            //checks if main property is the same as the one clicked by the user, if it isn't then move to the next object
                            if(Assetmain.equals(asset.get("Main").getAsString())){
                                toRemove = asset;
                            }
                        }else{
                            toRemove = asset;
                        }
                    }
                }
            }
            //remove the object from array of json and remove the asset from assets, so it no longer shows in the list
                arrayofjson.remove(toRemove);
            assets.remove(Assetname);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //show buttons after done deleting
        fabadd.show(true);
        delete = false;

    }


}
