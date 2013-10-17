package com.example.laser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class FindActivity extends ListActivity {
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
 
    ArrayList<HashMap<String, String>> productsList;
 
    // url to get all products list
    private static String url_all_products = "http://192.168.1.15/laserDatabase/android_connect/get_all_products.php";
 //192.168.1.15
    //udel 128.4.222.193
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_GAMES = "games";
    private static final String TAG_PID = "pid";
    private static final String TAG_SERVER_NAME = "server_name";
    private static final String TAG_MAX_PLAYERS = "max_players";
    private static final String TAG_CURRENT_PLAYERS = "current_players";
    private static final String TAG_GAME_MODE = "game_mode";
    private static final String TAG_GAME_INFO = "game_info";
 
    // products JSONArray
    JSONArray products = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_game);
 
        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();
 
        // Loading products in Background Thread
        new LoadAllProducts().execute();
 
        // Get listview
        ListView lv = getListView();
 
        // on seleting single product
        // launching Edit Product Screen
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText()
                        .toString();
 
                // Starting new intent
            //    Intent in = new Intent(getApplicationContext(),
              //          EditProductActivity.class);
                // sending pid to next activity
               // in.putExtra(TAG_PID, pid);
 
                // starting new activity and expecting some response back
            //    startActivityForResult(in, 100);
            }
        });
 
    }
 
    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
 
    }
 
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadAllProducts extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FindActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_products, "GET", params);
 
            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());
 
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    products = json.getJSONArray(TAG_GAMES);
 
                    // looping through All Products
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject c = products.getJSONObject(i);
 
                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String name = c.getString(TAG_SERVER_NAME);
                        String maxPlayers = c.getString(TAG_MAX_PLAYERS);
                        String currentPlayers = c.getString(TAG_CURRENT_PLAYERS);
                        String gameMode = c.getString(TAG_GAME_MODE);
 
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();
                        String gameInfo = (name+" "+currentPlayers+"/"+maxPlayers+" "+gameMode);
                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                       map.put(TAG_GAME_INFO, gameInfo);
                        // map.put(TAG_SERVER_NAME, name);
                        map.put(TAG_CURRENT_PLAYERS, currentPlayers);
                        map.put(TAG_MAX_PLAYERS, maxPlayers);
                        map.put(TAG_GAME_MODE, gameMode);
 
                        // adding HashList to ArrayList
                        productsList.add(map);
                    }
                } else {
                    // no products found
                    // Launch Add New product Activity
                  //  Intent i = new Intent(getApplicationContext(),
                //            NewProductActivity.class);
                    // Closing all previous activities
                 //   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            FindActivity.this, productsList,
                            R.layout.list_item, new String[] { TAG_PID, TAG_GAME_INFO,
                            		TAG_SERVER_NAME, TAG_CURRENT_PLAYERS, TAG_MAX_PLAYERS, TAG_GAME_MODE},
                            new int[] { R.id.pid, R.id.gameinfo});
                    // updating listview
                    setListAdapter(adapter);
                }
            });
 
        }
 
    }
}
