package com.example.latihan201_tugas;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

public class ContactDetailTab extends Activity{
	
	
	ActionBar.Tab tabInbox, tabOutbox, tabEditContact;
	Fragment tab_inbox = new FragmentTabInbox();
	Fragment tab_outbox = new FragmentTabOutbox();
	Fragment tab_edit = new FragmentTabEditContact();
	String contact_id, contact_name;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_tab);
        
        Intent intent = getIntent();
        contact_id = intent.getStringExtra("id");
        contact_name = intent.getStringExtra("name");
        Bundle bundle = new Bundle();
        bundle.putString("id", contact_id);
        tab_inbox.setArguments(bundle);
        tab_outbox.setArguments(bundle);
        tab_edit.setArguments(bundle);
        
        ActionBar actionBar = getActionBar();
        getActionBar().setTitle(contact_name);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        tabInbox = actionBar.newTab().setText("Inbox");
        tabOutbox = actionBar.newTab().setText("Outbox");
        tabEditContact = actionBar.newTab().setText("Edit");
        
        tabInbox.setTabListener(new TabListener(tab_inbox));
        tabOutbox.setTabListener(new TabListener(tab_outbox));
        tabEditContact.setTabListener(new TabListener(tab_edit));
        
        actionBar.addTab(tabInbox);
        actionBar.addTab(tabOutbox);
        actionBar.addTab(tabEditContact);
	}
	
}
