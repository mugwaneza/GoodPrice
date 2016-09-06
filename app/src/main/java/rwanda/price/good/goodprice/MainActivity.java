package rwanda.price.good.goodprice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import rwanda.price.good.goodprice.phone.VerifyPhoneFragment;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flags);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new VerifyPhoneFragment())
                    .commit();
        }
    }
    }






