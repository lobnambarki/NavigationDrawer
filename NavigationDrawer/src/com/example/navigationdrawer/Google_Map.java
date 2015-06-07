package com.example.navigationdrawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

	@SuppressLint("NewApi") 
	public class Google_Map extends ActionBarActivity implements
	NavDrawerGoogle.NavigationDrawerCallbacks {

		private NavDrawerGoogle mNavigationDrawerFragment;

		private CharSequence mTitle;
//	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//	                             Bundle savedInstanceState) {
//	    	
//
//	        View rootView = inflater.inflate(R.layout.google_map, container, false);
//
//	        return rootView;
//	    }
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.google_map);

			mNavigationDrawerFragment = (NavDrawerGoogle) getSupportFragmentManager()
					.findFragmentById(R.id.navigation_drawer);
			mTitle = getTitle();
			
			DrawerLayout d = (DrawerLayout) findViewById(R.id.drawer_layout) ;

			// Set up the drawer.
			mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
					d);
	    }
	    @Override
		public void onNavigationDrawerItemSelected(int position) {
			// TODO Auto-generated method stub
				// update the main content by replacing fragments
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager
						.beginTransaction()
						.replace(R.id.content_frame,
								PlaceholderFragment.newInstance(position + 1)).commit();
			
		}
	    public void onSectionAttached(int number) {
			switch (number) {
			case 1:
				Intent profile = new Intent(this, Accueil.class);
				startActivity(profile);
				break;
			case 2:
				mTitle = getString(R.string.title_activity_google);
				break;
			case 3:
				Intent home = new Intent(this, Rapports.class);
				startActivity(home);
				break;
				
			
			}
		}

		public void restoreActionBar() {
			ActionBar actionBar = getSupportActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(mTitle);
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			if (!mNavigationDrawerFragment.isDrawerOpen()) {
				// Only show items in the action bar relevant to this screen
				// if the drawer is not showing. Otherwise, let the drawer
				// decide what to show in the action bar.
				getMenuInflater().inflate(R.menu.menu_google, menu);
				restoreActionBar();
				return true;
			}
			return super.onCreateOptionsMenu(menu);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// Handle action bar item clicks here. The action bar will
			// automatically handle clicks on the Home/Up button, so long
			// as you specify a parent activity in AndroidManifest.xml.
			int id = item.getItemId();
			if (id == R.id.action_settings) {
				Context context = getApplicationContext();
				CharSequence text = "CPG";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}

		/**
		 * A placeholder fragment containing a simple view.
		 */
		public static class PlaceholderFragment extends Fragment {
			/**
			 * The fragment argument representing the section number for this
			 * fragment.
			 */
			private static final String ARG_SECTION_NUMBER = "section_number";

			/**
			 * Returns a new instance of this fragment for the given section number.
			 */
			public static PlaceholderFragment newInstance(int sectionNumber) {
				PlaceholderFragment fragment = new PlaceholderFragment();
				Bundle args = new Bundle();
				args.putInt(ARG_SECTION_NUMBER, sectionNumber);
				fragment.setArguments(args);
				return fragment;
			}

			public PlaceholderFragment() {
			}

			
//			@Override
//			public void onAttach(Activity activity) {
//				super.onAttach(activity);
//				((Google_Map) activity).onSectionAttached(getArguments().getInt(
//						ARG_SECTION_NUMBER));
//			}
		}

		

	}




