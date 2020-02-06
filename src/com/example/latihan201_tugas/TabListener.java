package com.example.latihan201_tugas;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.app.ActionBar;

public class TabListener implements ActionBar.TabListener {
	
Fragment fragment;
	
	public TabListener(Fragment fragment){
		this.fragment = fragment;
	}
	
	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		arg1.replace(R.id.contact_detail_tab, fragment);
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		arg1.remove(fragment);
	}

}
