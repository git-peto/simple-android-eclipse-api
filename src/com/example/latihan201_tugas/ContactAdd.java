package com.example.latihan201_tugas;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactAdd extends Activity{
	
	private String TAG = ContactAdd.class.getSimpleName();
	private ProgressDialog pDialog;
	private static String url = "http://apilearning.totopeto.com/contacts/";
	
	EditText ename, eaddress, eemail, ephone, edob;
	Button bsimpancontact;
	
	private String response_url;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.form_contact);
        
        ename = (EditText)findViewById(R.id.etname);
        eaddress = (EditText)findViewById(R.id.etaddress);
        eemail = (EditText)findViewById(R.id.etemail);
        ephone = (EditText)findViewById(R.id.etphone);
        edob = (EditText)findViewById(R.id.etdob);
        bsimpancontact = (Button)findViewById(R.id.btnsimpancontact);
        bsimpancontact.setOnClickListener(simpanContactClick);
	}
	
	private View.OnClickListener simpanContactClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			new SaveContact().execute();
		}
	};
	
	private class SaveContact extends AsyncTask<Void, Void, Void>{

		protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(ContactAdd.this);
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
						ContactAdd.this.finish();
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
			String post_params = null;
			JSONObject params = new JSONObject();
			try {
				params.put("name", ename.getText().toString());
				params.put("address", eaddress.getText().toString());
				params.put("email", eemail.getText().toString());
				params.put("phone", ephone.getText().toString());
				params.put("dob", edob.getText().toString());
				post_params = params.toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpHandler data = new HttpHandler();
			String jsonStr = data.makePostRequest(url, post_params);
			Log.e(TAG, "Response from URL: " + jsonStr);
			response_url = jsonStr;
			return null;
		}
		
	}
}
