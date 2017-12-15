package com.example.tyren.beachtrade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

public class NavigationBarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    AccessToken accessToken;
    ProfileMapperClass retrievedProfile;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessToken = AccessToken.getCurrentAccessToken();

        setContentView(R.layout.activity_navigation_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = this.getSharedPreferences(
                "com.example.tyren.beachtrade", Context.MODE_PRIVATE);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*new GraphRequest(
                accessToken.getCurrentAccessToken(),
                "/{user-id}",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result//
                    }
                }
        ).executeAsync();*/
        new getUsername().execute();

        getFragmentManager().beginTransaction().replace(R.id.content_frame,
                new FirstFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_bar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(NavigationBarActivity.this, LoginActivity.class);
            LoginManager.getInstance().logOut();
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fragment_profile) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame,
                    new ProfileFragment()).commit();
        } else if (id == R.id.nav_first_layout) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame,
                    new FirstFragment()).commit();

        } else if (id == R.id.nav_my_items) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame,
                    new MyItemsFragment()).commit();


        } else if (id == R.id.nav_third_layout) {
            Log.i("Send email", "");
            String[] TO = {""};
            String[] CC = {""};
            Intent emailIntent = new Intent(Intent.ACTION_SEND);

            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I am interested in an Item of yours!");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "I'd like to inquire about an item of yours.");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                //finish();

            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(NavigationBarActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class getUsername extends AsyncTask<Void, Integer, Integer> {
        @Override
        protected Integer doInBackground(Void... params){

            ManagerClass managerClass = new ManagerClass();
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "us-east-1:6ec4d10e-5eff-4422-8388-af344a4a3923", // Identity pool ID
                    Regions.US_EAST_1 // Region
            );


            ProfileMapperClass profileMapper = new ProfileMapperClass();


            if(credentialsProvider != null && profileMapper != null){
                DynamoDBMapper dynamoDBMapper = ManagerClass.intiDynamoClient(credentialsProvider);
                retrievedProfile = dynamoDBMapper.load(ProfileMapperClass.class, accessToken.getUserId());
            }


            return 1;
        }
        protected void onPostExecute(Integer integer){
            super.onPostExecute(integer);
            if(retrievedProfile != null){
                prefs.edit().putString("userName", retrievedProfile.getUserName()).apply();
                prefs.edit().putString("email", retrievedProfile.getEmailAddress()).apply();
                String username = prefs.getString("userName", null);
                Toast.makeText(NavigationBarActivity.this, "Welcome, "+username, Toast.LENGTH_SHORT).show();
            }
        }


    }


}
