package com.example.latihan201_tugas;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentTabEditContact extends Fragment{
	String TAG = FragmentTabEditContact.class.getSimpleName();
	private ProgressDialog pDialog;
	private static String url = "http://apilearning.totopeto.com/contacts/";
	
	EditText ename, eaddress, eemail, ephone, edob;
	Button bsimpancontact;
	String contact_id, name, address, email, phone, dob;
	
	private String response_url;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.form_contact, container, false);
        
        ename = (EditText)rootView.findViewById(R.id.etname);
        eaddress = (EditText)rootView.findViewById(R.id.etaddress);
        eemail = (EditText)rootView.findViewById(R.id.etemail);
        ephone = (EditText)rootView.findViewById(R.id.etphone);
        edob = (EditText)rootView.findViewById(R.id.etdob);
        bsimpancontact = (Button)rootView.findViewById(R.id.btnsimpancontact);
        
        contact_id = getArguments().getString("id");
        
        bsimpancontact.setOnClickListener(simpanContactClick);
        
        return rootView;
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
    		pDialog = new ProgressDialog(getActivity());
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
					Toast.makeText(getActivity(), jsonObj.getString("message"), Toast.LENGTH_LONG).show();	
				}catch (final JSONException e){
					Log.e(TAG, "Json parsing error: " + e.getMessage());
					Toast.makeText(getActivity(), "json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}else{
				Log.e(TAG, "Couldn't get json from server");
				Toast.makeText(getActivity(), "Couldn't get json from server!", Toast.LENGTH_LONG).show();
			}
    	}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			String url_new = url + contact_id;
			
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
			String jsonStr = data.makePutRequest(url_new, post_params);
			Log.e(TAG, "Response from URL: " + jsonStr);
			response_url = jsonStr;
			return null;
		}
		
	}
	
	private class GetContact extends AsyncTask<Void, Void, Void>{

		protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(getActivity());
    		pDialog.setMessage("Please wait...");
    		pDialog.setCancelable(false);
    		pDialog.show();
    	}
		
		protected void onPostExecute(Void result){
    		super.onPostExecute(result);
    		if(pDialog.isShowing()){ pDialog.dismiss(); }
    		
    		ename.setText(name);
    		eaddress.setText(address);
    		eemail.setText(email);
    		ephone.setText(phone);
    		edob.setText(dob);
    	}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
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
					dob = contact.getString("dob");
				}catch (final JSONException e){
					Log.e(TAG, "Json parsing error: " + e.getMessage());
					Toast.makeText(getActivity(), "json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}else{
				Log.e(TAG, "Couldn't get json from server");
				Toast.makeText(getActivity(), "Couldn't get json from server!", Toast.LENGTH_LONG).show();
			}
			return null;
		}
	}
	
	@Override
    public void onResume(){
    	super.onResume();
    	new GetContact().execute();
    }
}
