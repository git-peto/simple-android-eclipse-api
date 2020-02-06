package com.example.latihan201_tugas;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class OutboxAdd extends Activity {
	
	private String TAG = OutboxAdd.class.getSimpleName();
	private ProgressDialog pDialog;
	Spinner scontacts;
	EditText epesan;
	Button bkirim;
	List<String> list_contacts = new ArrayList<String>();
	List<String> list_contacts_id = new ArrayList<String>();
	private String contact_id, response_url;
	private static String url_get_contacts = "http://apilearning.totopeto.com/contacts/";
	private static String url_post_message = "http://apilearning.totopeto.com/messages/";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_outbox);
        
        scontacts = (Spinner)findViewById(R.id.spcontact);
        epesan = (EditText)findViewById(R.id.etpesan);
        bkirim = (Button)findViewById(R.id.btnkirim);
        
        getActionBar().setTitle("Pesan Baru");
        
        Intent intent = getIntent();
        contact_id = intent.getStringExtra("id");
        
        new GetContacts().execute();
        bkirim.setOnClickListener(kirimClick);
    }

	private class GetContacts extends AsyncTask<Void, Void, Void>{
    	
    	protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(OutboxAdd.this);
    		pDialog.setMessage("Please wait...");
    		pDialog.setCancelable(false);
    		pDialog.show();
    	}
    	
    	protected void onPostExecute(Void result){
    		super.onPostExecute(result);
    		if(pDialog.isShowing()){ pDialog.dismiss(); }
    		
    		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(OutboxAdd.this, android.R.layout.simple_spinner_item, list_contacts);
    		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		scontacts.setAdapter(dataAdapter);
    	}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			
			HttpHandler data = new HttpHandler();
			String jsonStr = data.makeServiceCall(url_get_contacts);
			Log.e(TAG, "Response from URL: " + jsonStr);
			if (jsonStr != null){
				try{
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONArray contacts = jsonObj.getJSONArray("contacts");
					for(int i = 0; i < contacts.length(); i++){
						JSONObject contact = contacts.getJSONObject(i);
						String id = contact.getString("id");
						String name = contact.getString("name");
						
						list_contacts.add(name);
						list_contacts_id.add(id);
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
	
	private View.OnClickListener kirimClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			new SaveOutbox().execute();
		}
	};
	
	private class SaveOutbox extends AsyncTask<Void, Void, Void>{

		protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(OutboxAdd.this);
    		pDialog.setMessage("Please wait...");
    		pDialog.setCancelable(false);
    		pDialog.show();
    	}
		
		protected void onPostExecute(Void result){
    		super.onPostExecute(result);
    		if(pDialog.isShowing()){ pDialog.dismiss(); }
    		
    		if (response_url != null){
				try{
					JSONObject jsonObj = new JSONObject(response_url);
					if (jsonObj.getString("status") == "400"){
						Toast.makeText(getApplicationContext(), jsonObj.getString("message"), Toast.LENGTH_LONG).show();	
					}else{
						OutboxAdd.this.finish();
					}
				}catch (final JSONException e){
					Log.e(TAG, "Json parsing error: " + e.getMessage());
					Toast.makeText(getApplicationContext(), "json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}else{
				Log.e(TAG, "Couldn't get json from server");
				Toast.makeText(getApplicationContext(), "Couldn't get json from server!", Toast.LENGTH_LONG).show();
			}
    	}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String selected_contact_id = list_contacts_id.get(scontacts.getSelectedItemPosition());
			
			String post_params = null;
			JSONObject params = new JSONObject();
			try {
				params.put("from_id", contact_id);
				params.put("to_id", selected_contact_id);
				params.put("content", epesan.getText().toString());
				post_params = params.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpHandler data = new HttpHandler();
			String jsonStr = data.makePostRequest(url_post_message, post_params);
			Log.e(TAG, "Response from URL: " + jsonStr);
			response_url = jsonStr;
			return null;
		}
		
	}

}
