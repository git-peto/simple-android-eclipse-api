package com.example.latihan201_tugas;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ContactDetail extends Activity{
	
	private String TAG = ContactDetail.class.getSimpleName();
	private ProgressDialog pDialog;
	private static String url = "http://apilearning.totopeto.com/contacts/";
	
	TextView tname, taddress, temail, tphone;
	Button bkembali;
	String contact_id, name, address, email, phone;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail);
        
        tname = (TextView)findViewById(R.id.tvname);
        taddress = (TextView)findViewById(R.id.tvaddress);
        temail = (TextView)findViewById(R.id.tvemail);
        tphone = (TextView)findViewById(R.id.tvphone);
        bkembali = (Button)findViewById(R.id.btnback);
        
        new GetContact().execute();
        
        bkembali.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ContactDetail.this.finish();
			}
		});
	}
	
	private class GetContact extends AsyncTask<Void, Void, Void>{

		protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(ContactDetail.this);
    		pDialog.setMessage("Please wait...");
    		pDialog.setCancelable(false);
    		pDialog.show();
    	}
		
		protected void onPostExecute(Void result){
    		super.onPostExecute(result);
    		if(pDialog.isShowing()){ pDialog.dismiss(); }
    		
    		tname.setText(name);
    		taddress.setText(address);
    		temail.setText(email);
    		tphone.setText(phone);
    	}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
	        Intent intent = getIntent();
	        contact_id = intent.getStringExtra("id");
	        String url_post = url + contact_id;
	        
	        HttpHandler data = new HttpHandler();
			String jsonStr = data.makeServiceCall(url_post);
			Log.e(TAG, "Response from URL: " + jsonStr);
	        
			if (jsonStr != null){
				try{
					JSONObject jsonObj = new JSONObject(jsonStr);
					JSONObject contact = jsonObj.getJSONObject("contact");
					name = contact.getString("name");
					address = contact.getString("address");
					email = contact.getString("email");
					phone = contact.getString("phone");
				}catch (final JSONException e){
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
	
}
