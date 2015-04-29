package com.example.navigationdrawer;

	import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

	import com.example.navigationdrawer.R;
import com.example.navigationdrawer.NavDrawerRapports;
import com.example.navigationdrawer.Accueil;
import com.example.navigationdrawer.Google_Map;


	import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


	public class Rapports extends  ActionBarActivity implements
	NavDrawerRapports.NavigationDrawerCallbacks
	{
		private NavDrawerRapports mNavigationDrawerFragment;

		/**
		 * Used to store the last screen title. For use in
		 * {@link #restoreActionBar()}.
		 */
	private CharSequence mTitle;
		
	
		// Lien vers votre page php sur votre serveur
		private static final String	UPDATE_URL	= "http://localhost/phpTutorials/script.php";

		public ProgressDialog				progressDialog;

		private EditText						UserEditText;

		private EditText						PassEditText;

		public void onCreate(Bundle savedInstanceState)
		{

			super.onCreate(savedInstanceState);
			setContentView(R.layout.login);
					mNavigationDrawerFragment = (NavDrawerRapports) getSupportFragmentManager()
					.findFragmentById(R.id.drawer_list);
			mTitle = getTitle();

			// Set up the drawer.
			mNavigationDrawerFragment.setUp(R.id.drawer_list,
					(DrawerLayout) findViewById(R.id.drawer_layout));

			// initialisation d'une progress bar
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Please wait...");
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			// Récupération des éléments de la vue définis dans le xml
			UserEditText = (EditText) findViewById(R.id.password);
			PassEditText = (EditText) findViewById(R.id.username);
			Button log = (Button) findViewById(R.id.okbutton);
		
			// Définition du listener du bouton
			log.setOnClickListener(new View.OnClickListener()
			{

				public void onClick(View v)
				{

					int usersize = UserEditText.getText().length();

					int passsize = PassEditText.getText().length();
					// si les deux champs sont remplis
					if (usersize > 0 && passsize > 0)
					{

						progressDialog.show();

						String user = UserEditText.getText().toString();

						String pass = PassEditText.getText().toString();
						// On appelle la fonction doLogin qui va communiquer avec le PHP
						doLogin(user, pass);

					}
					else
						createDialog("Error", getString(R.string.please_enter_username_and_password));
				}

			});

			Button ca = (Button) findViewById(R.id.cancelbutton);
			// Création du listener du button cancel (on sort de l'appli)
			ca.setOnClickListener(new View.OnClickListener()
			{

				public void onClick(View v)
				{
					quit(false, null);
				}

			});

		}

		private void quit(boolean success, Intent i)
		{
			// On envoie un résultat qui va permettre de quitter l'appli
			setResult((success) ? Activity.RESULT_OK : Activity.RESULT_CANCELED, i);
			finish();

		}

		private void createDialog(String title, String text)
		{
			// Création d'une popup affichant un message
			AlertDialog ad = new AlertDialog.Builder(this)
					.setPositiveButton("Ok", null).setTitle(title).setMessage(text)
					.create();
			ad.show();

		}

		private void doLogin(final String login, final String pass)
		{

			final String pw = md5(pass);
			// Création d'un thread
			Thread t = new Thread()
			{

				public void run()
				{

					Looper.prepare();
					// On se connecte au serveur afin de communiquer avec le PHP
					DefaultHttpClient client = new DefaultHttpClient();
					HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);

					HttpResponse response;
					HttpEntity entity;

					try
					{
						// On établit un lien avec le script PHP
						HttpPost post = new HttpPost(UPDATE_URL);

						List<NameValuePair> nvps = new ArrayList<NameValuePair>();

						nvps.add(new BasicNameValuePair("username", login));

						nvps.add(new BasicNameValuePair("password", pw));

						post.setHeader("Content-Type", "application/x-www-form-urlencoded");
						// On passe les paramètres login et password qui vont être récupérés
						// par le script PHP en post
						post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
						// On récupère le résultat du script
						response = client.execute(post);

						entity = response.getEntity();

						InputStream is = entity.getContent();
						// On appelle une fonction définie plus bas pour traduire la réponse
						read(is);
						is.close();

						if (entity != null)
							entity.consumeContent();

					}
					catch (Exception e)
					{

						progressDialog.dismiss();
						createDialog("Error", "Couldn't establish a connection");

					}

					Looper.loop();

				}

			};

			t.start();

		}

		private void read(InputStream in)
		{
			// On traduit le résultat d'un flux
			SAXParserFactory spf = SAXParserFactory.newInstance();

			SAXParser sp;

			try
			{

				sp = spf.newSAXParser();

				XMLReader xr = sp.getXMLReader();
				// Cette classe est définie plus bas
				LoginContentHandler uch = new LoginContentHandler();

				xr.setContentHandler(uch);

				xr.parse(new InputSource(in));

			}
			catch (ParserConfigurationException e)
			{

			}
			catch (SAXException e)
			{

			}
			catch (IOException e)
			{
			}

		}

		private String md5(String in)
		{

			MessageDigest digest;

			try
			{

				digest = MessageDigest.getInstance("MD5");

				digest.reset();

				digest.update(in.getBytes());

				byte[] a = digest.digest();

				int len = a.length;

				StringBuilder sb = new StringBuilder(len << 1);

				for (int i = 0; i < len; i++)
				{

					sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));

					sb.append(Character.forDigit(a[i] & 0x0f, 16));

				}

				return sb.toString();

			}
			catch (NoSuchAlgorithmException e)
			{
				e.printStackTrace();
			}

			return null;

		}

		private class LoginContentHandler extends DefaultHandler
		{
			// Classe traitant le message de retour du script PHP
			private boolean	in_loginTag		= false;
			private int			userID;
			private boolean	error_occured	= false;

			public void startElement(String n, String l, String q, Attributes a)

			throws SAXException

			{

				if (l == "login")
					in_loginTag = true;
				if (l == "error")
				{

					progressDialog.dismiss();

					switch (Integer.parseInt(a.getValue("value")))
					{
						case 1:
							createDialog("Error", "Couldn't connect to Database");
							break;
						case 2:
							createDialog("Error", "Error in Database: Table missing");
							break;
						case 3:
							createDialog("Error", "Invalid username and/or password");
							break;
					}
					error_occured = true;

				}

				if (l == "user" && in_loginTag && a.getValue("id") != "")
					// Dans le cas où tout se passe bien on récupère l'ID de l'utilisateur
					userID = Integer.parseInt(a.getValue("id"));

			}

			public void endElement(String n, String l, String q) throws SAXException
			{
				// on renvoie l'id si tout est ok
				if (l == "login")
				{
					in_loginTag = false;

					if (!error_occured)
					{
						progressDialog.dismiss();
						Intent i = new Intent();
						i.putExtra("userid", userID);
						quit(true, i);
					}
				}
			}

			public void characters(char ch[], int start, int length)
			{
			}

			public void startDocument() throws SAXException
			{
			}

			public void endDocument() throws SAXException
			{
			}

		}

		
		@Override
		public void onNavigationDrawerItemSelected(int position) {
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
				mTitle = getString(R.string.title_activity_rapports);
				break;
			case 2:
				Intent home = new Intent(this, Google_Map.class);
				startActivity(home);
				break;
			case 3:
				Intent profile = new Intent(this, Accueil.class);
				startActivity(profile);
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
				getMenuInflater().inflate(R.menu.menu_login, menu);
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

			
			@Override
			public void onAttach(Activity activity) {
				super.onAttach(activity);
				((Rapports) activity).onSectionAttached(getArguments().getInt(
						ARG_SECTION_NUMBER));
			}
		}

	}




