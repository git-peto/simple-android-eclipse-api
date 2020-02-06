package com.example.latihan201_tugas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONArray;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentTabOutbox extends Fragment {
	
	String TAG = FragmentTabOutbox.class.getSimpleName();
	String url = "http://apilearning.totopeto.com/messages/outbox";
	ProgressDialog pDialog;
	ListView loutbox;
	TextView tcaption;
	String contact_id;
	String outbox_count;
	ArrayList<HashMap<String, String>> outboxList;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_outbox, container, false);
        
        outboxList = new ArrayList<HashMap<String, String>>();
        loutbox = (ListView)rootView.findViewById(R.id.lvoutbox);
        tcaption = (TextView)rootView.findViewById(R.id.tvcaptionoutbox);
        
        contact_id = getArguments().getString("id");
        Log.e(TAG, "Tangkap ID for OUTBOX: " + contact_id);
        
        return rootView;
    }
	
	private class GetOutboxes extends AsyncTask<Void, Void, Void>{
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
    		
    		tcaption.setText("Jumlah outbox: " + outbox_count);
    		ListAdapter adapter = new SimpleAdapter(getActivity(), outboxList, R.layout.list_item_outbox,
    				new String[]{"to", "content", "created_at"}, new int[]{R.id.outbox_tvto, R.id.outbox_tvcontent, R.id.outbox_tvcreatedat});
    		loutbox.setAdapter(adapter);
    	}
    	
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			outboxList.clear();
			String url_new = url + "?id=" + contact_id;
			HttpHandler data = new HttpHandler();
			String jsonStr = data.makeServiceCall(url_new);
			Log.e(TAG, "Response from URL: " + jsonStr);
			
			if (jsonStr != null){
				try{
					JSONObject jsonObj = new JSONObject(jsonStr);
					outbox_count = jsonObj.getString("total");
					
					JSONArray messages = jsonObj.getJSONArray("data");
					for(int i = 0; i < messages.length(); i++){
						JSONObject message = messages.getJSONObject(i);
						String to = message.getString("to");
						String content = message.getString("content");
						String created_at = message.getString("created_at");
						
						Log.e(TAG, "FORMAT FROM API:" + created_at);
						
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
						//sdf.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
						Log.e(TAG, "FINAL FORMATTED: " + sdf.parse(created_at));
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(sdf.parse(created_at));
						calendar.add(Calendar.HOUR_OF_DAY, 7);
						Log.e(TAG, "TEST FINAL: " + calendar.getTime());
						
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
						String formattedDate = formatter.format(calendar.getTime());
						Log.e(TAG, "Date Date Date: " + formattedDate);
						
						Date current_date = new Date();
						String cur_date = formatter.format(current_date);
						Date d1 = null, d2 = null;
						d1 = formatter.parse(formattedDate);
						d2 = formatter.parse(cur_date);
						long diff = d2.getTime() - d1.getTime();
						long diffDays = diff / (24 * 60 * 60 * 1000);
						String calculate_diff;
						if (diffDays == 0){
							calculate_diff = "hari ini";
						}else{
							calculate_diff = (diffDays + 1) + " hari lalu";
						}
						
						HashMap<String, String> message_temp = new HashMap<String, String>();
						message_temp.put("to", to);
						message_temp.put("content", content);
						message_temp.put("created_at", calculate_diff);
						outboxList.add(message_temp);
					}
				} catch (final JSONException e){
					Log.e(TAG, "Json parsing error: " + e.getMessage());
					Toast.makeText(getActivity(), "json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
    	new GetOutboxes().execute();
    }
	
}
