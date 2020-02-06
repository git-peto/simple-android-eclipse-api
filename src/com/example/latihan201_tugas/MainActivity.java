package com.example.latihan201_tugas;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String TAG = MainActivity.class.getSimpleName();
	private ProgressDialog pDialog;
	private ListView lv;
	private Button btambahcontact;
	private static String url = "http://apilearning.totopeto.com/contacts/";
	ArrayList<HashMap<String, String>> contactList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        contactList = new ArrayList<HashMap<String, String>>();
        lv = (ListView)findViewById(R.id.lvcontacts);
        btambahcontact = (Button)findViewById(R.id.btntambahcontact);
        //new GetContacts().execute();
        lv.setOnItemClickListener(listClick);
        btambahcontact.setOnClickListener(tambahContactClick);
    }

    private class GetContacts extends AsyncTask<Void, Void, Void>{
    	
    	protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(MainActivity.this);
    		pDialog.setMessage("Please wait...");
    		pDialog.setCancelable(false);
    		pDialog.show();
    	}
    	
    	protected void onPostExecute(Void result){
    		super.onPostExecute(result);
    		if(pDialog.isShowing()){ pDialog.dismiss(); }
    		
    		ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList, R.layout.list_item,
    				new String[]{"name", "email", "phone"}, new int[]{R.id.name, R.id.email, R.id.phone});
    		lv.setAdapter(adapter);
    	}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			contactList.clear();
			
			HttpHandler data = new HttpHandler();
			String jsonStr = data.makeServiceCall(url);
			Log.e(TAG, "Response from URL: " + jsonStr);
			if (jsonStr != null){
				try{
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONArray contacts = jsonObj.getJSONArray("contacts");
					for(int i = 0; i < contacts.length(); i++){
						JSONObject contact = contacts.getJSONObject(i);
						String id = contact.getString("id");
						String name = contact.getString("name");
						String address = contact.getString("address");
						String email = contact.getString("email");
						String phone = contact.getString("phone");
						String dob = contact.getString("dob");
						
						HashMap<String, String> contact_temp = new HashMap<String, String>();
						contact_temp.put("id", id);
						contact_temp.put("name", name);
						contact_temp.put("address", address);
						contact_temp.put("email", email);
						contact_temp.put("phone", phone);
						contact_temp.put("dob", dob);
						contactList.add(contact_temp);
					}
				} catch (final JSONException e){
					Log.e(TAG, "Json parsing error: " + e.getMessage());
					Toast.makeText(getApplicationContext(), "json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}else{
				Log.e(TAG, "Couldn't get json from server");
				Toast.makeText(getApplicationContext(), "Couldn't get json from server!", Toast.LENGTH_LONG).show();
			}
			
			return null;
		}
    	
    }

    private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			/*HashMap<String, String> hm = contactList.get(arg2);
			Intent intent = new Intent(MainActivity.this, ContactDetail.class);
			intent.putExtra("id", hm.get("id"));
			startActivity(intent);*/
			
			
			HashMap<String, String> hm = contactList.get(arg2);
			Intent intent = new Intent(MainActivity.this, ContactDetailTab.class);
			intent.putExtra("id", hm.get("id"));
			intent.putExtra("name", hm.get("name"));
			startActivity(intent);
		}
	};
	
	private View.OnClickListener tambahContactClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MainActivity.this, ContactAdd.class);
			startActivity(intent);
		}
	};
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	new GetContacts().execute();
    }
    
}
