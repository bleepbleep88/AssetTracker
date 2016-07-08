package com.example.kole.assettracker;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

public class MainActivity extends ListActivity {
    //Add variables, allowing them to be changed anywhere in the java file
    ListView list1;
    FloatingActionButton fabadd;
    FloatingActionButton fabdelete;
    FloatingActionButton fabsearch;
    boolean delete = false;
    File file;
    JsonArray arrayofjson = new JsonArray();
    List<String> assets = new ArrayList<String>();
    JsonWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initializations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list1 = (ListView) findViewById(android.R.id.list);
        Gson gson = new Gson();
        file = new File(getFilesDir().toString() + "/" + "super");
        JsonParser parser = new JsonParser();
        JsonObject obj = new JsonObject();
        JsonObject obj2 = new JsonObject();

        //Make a file and fill it with base objects if there's no file already made
        //used tutorial http://www.mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/
        //I used mkyong in general, he had lots of basic stuff on JSON
        if(!file.exists()) {
            try {
                file.createNewFile();
                writer = new JsonWriter(new FileWriter(file, true));
                //add asset data to object and add to arrayofjson
                obj.addProperty("Asset","Computers");
                obj2.addProperty("Asset","Furniture");
                arrayofjson.add(obj);
                arrayofjson.add(obj2);
                //write to file
                gson.toJson(arrayofjson, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Initialize arrayofjson by reading file contents to it
        try {
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //Read the json file into the adapter, parsedajson just reads the assets in the json file and adds to the assets list
        parsedaJson();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, assets);
        list1.setAdapter(adapter);
        //Listen for item clicks
        list1.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        // Go to the asset list if delete isn't enabled, if it is, delete the list of objects touched
                        if(!delete) {

                            Object o = list1.getItemAtPosition(position);
                            String pen = o.toString();
                            sendBack(pen);
                        }else{
                            Object o = list1.getItemAtPosition(position);
                            String pen = o.toString();
                            deleter(pen);
                            writetojson(arrayofjson);
                            reader();

                        }

                    }
                }
        );
        //initializing FABs
        fabadd = (FloatingActionButton) findViewById(R.id.fabadd);
        fabdelete = (FloatingActionButton) findViewById(R.id.fabdelete);
        fabsearch = (FloatingActionButton) findViewById(R.id.fabsearch);
        //listen for scrolls, enable animation to slide out when scrolling down
        list1.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if(mLastFirstVisibleItem<firstVisibleItem)
                {
                    //don't hide the delete button if it's currently activated
                    fabadd.hide(true);
                    fabsearch.hide(true);
                        if(!delete)
                        fabdelete.hide(true);


                }
                if(mLastFirstVisibleItem>firstVisibleItem)
                {
                    //don't show any other buttons if delete is activated
                    if(fabadd.isHidden()&&!delete) {
                        fabadd.show(true);
                        fabsearch.show(true);
                        fabdelete.show(true);
                    }

                }
                mLastFirstVisibleItem=firstVisibleItem;

            }
        });
    }
    //Start new activity to add Json objects
    //used tutorial https://www.youtube.com/watch?v=n21mXO1ASJM
    //used example https://stackoverflow.com/questions/14785806/android-how-to-make-an-activity-return-results-to-the-activity-which-calls-it
    //used example https://stackoverflow.com/questions/2497205/how-to-return-a-result-startactivityforresult-from-a-tabhost-activity
    public void addStuff(View view){
        Intent startNewActivity = new Intent(this, addasset.class);
        int requestCode = 0;
        startActivityForResult(startNewActivity, requestCode);
    }
    //Start the listclick activity to look at list of assets you touched from the listview
    public void sendBack(String item){
        Intent startNewActivity = new Intent(this, listclick.class).putExtra("asset",item);
        startActivity(startNewActivity);
    }
    //Start the search activity
    public void searcher(View view){
        Intent startNewActivity = new Intent(this, search.class);
        startActivity(startNewActivity);
    }
    //Adds the new asset list after addStuff finishes
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String namem = data.getStringExtra("Assetname");

                try {
                    addJson("Asset",namem);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                writetojson(arrayofjson);
                reader();
            }
        }
    }
    //initially reads the JSON file to the assets list for the adapter for listview
    public void parsedaJson() {
        //the whole try enclosure puts items into the assets list if they're not already added
        Gson gson = new Gson();
        try {
            JsonParser parser = new JsonParser();
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));

            //used example https://stackoverflow.com/questions/10926353/how-to-read-json-file-into-java-with-simple-json-library
            for (Object o : arrayofjson) {
                JsonObject asset = (JsonObject) o;
                //add the asset to assets list if it's not already added.
                String name = asset.get("Asset").getAsString();
                if(!assets.contains(name)) {
                    assets.add(name);
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
    //Adds the new object to the JSON array if the same name wasn't already added
    public void addJson(String name, String desc) throws FileNotFoundException {
       JsonObject obj = new JsonObject();
       //puts items into the Json array and later writes it to file when writetojson is called
            //make sure we don't already have the item.
           if(!assets.contains(desc)) {
               obj.addProperty(name, desc);
               arrayofjson.add(obj);

           }

    }
    //Same thing as parsedaJson, except it alerts the adapter it has changed
    public void reader() {

        try {
            JsonParser parser = new JsonParser();
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));


            for (Object o : arrayofjson) {
                JsonObject person = (JsonObject) o;

                String name = person.get("Asset").getAsString();
                if(!assets.contains(name)) {
                    assets.add(name);
                }


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) list1.getAdapter();
        adapter.notifyDataSetChanged();

    }
    //deletes the file you touched in listview if delete is active
    public void deleter(String deleteasset){

        try {
            //set arrayofjson to the json files contents
            JsonParser parser = new JsonParser();
            arrayofjson = (JsonArray) parser.parse(new FileReader(file));
            //add all assets that share the same name to toRemove
            JsonArray toRemove = new JsonArray();
            for (JsonElement o : arrayofjson) {
                JsonObject person = (JsonObject) o;
                String name = person.get("Asset").getAsString();
                if (name.equals(deleteasset)) {
                    toRemove.add(o);
                }
            }
            //Remove all the toRemove objects from arrayofjson, (there's no removeAll for arrayofjson unlike lists)
            for(int i = 0; i < toRemove.size();i++){
            arrayofjson.remove(toRemove.get(i));
            }
            //delete the asset from the assets list, so it no longer shows up in the ListView
            assets.remove(deleteasset);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //slide back in the other FABs, also returns delete to false.
        fabadd.show(true);
        fabsearch.show(true);
        delete = false;

    }
    //change state of delete variable on delete fab click and animate other fabs
    public void changeDelete(View view){

        if(!delete){
            delete = true;

            fabadd.hide(true);
            fabsearch.hide(true);

            Toast.makeText(getApplicationContext(), "Select an asset to delete it or select the trash icon again to cancel", Toast.LENGTH_LONG).show();
        }else{
            fabadd.show(true);
            fabsearch.show(true);
            delete = false;
        }

    }


}

