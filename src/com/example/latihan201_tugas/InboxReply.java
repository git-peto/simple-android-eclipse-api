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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InboxReply extends Activity {
	
	private String TAG = InboxReply.class.getSimpleName();
	private ProgressDialog pDialog;
	String reply_to, reply_to_id, from_id, prev_message, response_url;
	TextView tinbox;
	EditText ereply;
	Button breply;
	private static String url_post_message = "http://apilearning.totopeto.com/messages/";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reply_inbox);
        
        tinbox = (TextView)findViewById(R.id.tv_content_inbox);
        ereply = (EditText)findViewById(R.id.etpesanreply);
        breply = (Button)findViewById(R.id.btnkirimreply);
        
        Intent intent = getIntent();
        reply_to = intent.getStringExtra("reply_to");
        reply_to_id = intent.getStringExtra("reply_to_id");
        prev_message = intent.getStringExtra("message");
        from_id = intent.getStringExtra("contact_id");
        
        getActionBar().setTitle("Balasan ke " + reply_to);
        
        tinbox.setText(prev_message);
        
        breply.setOnClickListener(replyClick);
	}
	
	private View.OnClickListener replyClick = new View.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			new SaveReply().execute();
		}
		
	};
	
	private class SaveReply extends AsyncTask<Void, Void, Void>{

		protected void onPreExecute(){
    		super.onPreExecute();
    		pDialog = new ProgressDialog(InboxReply.this);
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
						InboxReply.this.finish();
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
				params.put("from_id", from_id);
				params.put("to_id", reply_to_id);
				params.put("content", ereply.getText().toString());
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
