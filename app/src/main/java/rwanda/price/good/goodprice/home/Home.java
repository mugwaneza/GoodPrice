package rwanda.price.good.goodprice.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

import rwanda.price.good.goodprice.MainActivity;
import rwanda.price.good.goodprice.R;
import rwanda.price.good.goodprice.helper.PrefManager;
import rwanda.price.good.goodprice.phone.VerifyPhoneFragment;

/**
 * Created by Alexis on 8/25/2016.
 */
public class Home extends AppCompatActivity {

    private Toolbar toolbar;
    private PrefManager pref;
    private TextView  phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        phone = (TextView) findViewById(R.id.phone);

        // enabling toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        pref = new PrefManager(getApplicationContext());


        // Checking if user session
        // if not logged in, take user to sms screen
        if (!pref.isLoggedIn()) {

            logout();
        }

        // Displaying user information from shared preferences
        HashMap<String, String> profile = pref.getUserDetails();

        phone.setText("Mobile: " + profile.get("phone"));
    }

    /**
     * Logging out user
     * will clear all user shared preferences and navigate to
     * sms activation screen
     */
    private void logout() {
        pref.clearSession();

        Intent intent = new Intent(Home.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
