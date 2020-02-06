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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentTabInbox extends Fragment {
	
	String TAG = FragmentTabInbox.class.getSimpleName();
	String url = "http://apilearning.totopeto.com/messages/inbox";
	ProgressDialog pDialog;
	ListView linbox;
	TextView tcaption;
	Button bnewoutbox;
	String contact_id;
	String inbox_count;
	ArrayList<HashMap<String, String>> inboxList;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_inbox, container, false);
        
        inboxList = new ArrayList<HashMap<String, String>>();
        linbox = (ListView)rootView.findViewById(R.id.lvinbox);
        tcaption = (TextView)rootView.findViewById(R.id.tvcaptioninbox);
        bnewoutbox = (Button)rootView.findViewById(R.id.btnnewoutbox);
        
        contact_id = getArguments().getString("id");
        Log.e(TAG, "Tangkap ID for INBOX: " + contact_id);
        
        bnewoutbox.setOnClickListener(newOutboxClick);
        linbox.setOnItemClickListener(listClick);
        
        return rootView;
    }
	
	private class GetInboxes extends AsyncTask<Void, Void, Void>{
		
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
    		
    		tcaption.setText("Jumlah inbox: " + inbox_count);
    		ListAdapter adapter = new SimpleAdapter(getActivity(), inboxList, R.layout.list_item_inbox,
    				new String[]{"from", "content", "created_at"}, new int[]{R.id.inbox_tvfrom, R.id.inbox_tvcontent, R.id.inbox_tvcreatedat});
    		linbox.setAdapter(adapter);
    	}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			inboxList.clear();
			String url_new = url + "?id=" + contact_id;
			HttpHandler data = new HttpHandler();
			String jsonStr = data.makeServiceCall(url_new);
			Log.e(TAG, "Response from URL: " + jsonStr);
			
			if (jsonStr != null){
				try{
					JSONObject jsonObj = new JSONObject(jsonStr);
					inbox_count = jsonObj.getString("total");
					
					JSONArray messages = jsonObj.getJSONArray("data");
					for(int i = 0; i < messages.length(); i++){
						JSONObject message = messages.getJSONObject(i);
						String from = message.getString("from");
						String from_id = message.getString("from_id");
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
						message_temp.put("from", from);
						message_temp.put("from_id", from_id);
						message_temp.put("content", content);
						message_temp.put("created_at", calculate_diff);
						inboxList.add(message_temp);
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
    	new GetInboxes().execute();
    }
	
	private View.OnClickListener newOutboxClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//Toast.makeText(getActivity(), "Test", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(getActivity(), OutboxAdd.class);
			intent.putExtra("id", contact_id);
			startActivity(intent);
		}
	};
	
	private AdapterView.OnItemClickListener listClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			HashMap<String, String> hm = inboxList.get(arg2);
			Intent intent = new Intent(getActivity(), InboxReply.class);
			intent.putExtra("reply_to", hm.get("from"));
			intent.putExtra("reply_to_id", hm.get("from_id"));
			intent.putExtra("message", hm.get("content"));
			intent.putExtra("contact_id", contact_id);
			startActivity(intent);
		}
	};
	
}
