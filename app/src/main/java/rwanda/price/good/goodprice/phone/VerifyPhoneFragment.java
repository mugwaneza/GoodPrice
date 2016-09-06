package rwanda.price.good.goodprice.phone;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rwanda.price.good.goodprice.Config;
import rwanda.price.good.goodprice.MainActivity;
import rwanda.price.good.goodprice.MyApplication;
import rwanda.price.good.goodprice.R;
import rwanda.price.good.goodprice.helper.PrefManager;
import rwanda.price.good.goodprice.home.Home;
import rwanda.price.good.goodprice.httpservice.HttpService;

public class VerifyPhoneFragment extends BaseFlagFragment implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();


      private EditText inputOtp;
      NetworkInfo nf;
      Vibrator vibrator ;

     private ProgressBar progressBar;
      private Button btnVerifyOtp;
     private ImageButton btnEditMobile;

     private PrefManager pref;

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TextView txtEditMobile;
    private LinearLayout layoutEditMobile;

     //Volley RequestQueue
      private RequestQueue requestQueue;
    //String variables to hold  phone
      private String phone;

       public VerifyPhoneFragment() {
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);
        initUI(rootView);

        viewPager = (ViewPager)rootView.findViewById(R.id.viewPagerVertical);
        inputOtp = (EditText)rootView.findViewById(R.id.inputOtp);
        btnVerifyOtp = (Button)rootView.findViewById(R.id.btn_verify_otp);
        btnEditMobile = (ImageButton)rootView.findViewById(R.id.btn_edit_mobile);
        txtEditMobile = (TextView)rootView.findViewById(R.id.txt_edit_mobile);
        layoutEditMobile = (LinearLayout)rootView.findViewById(R.id.layout_edit_mobile);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);


        // view click listeners
        btnEditMobile.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);


        // hiding the edit mobile number
        layoutEditMobile.setVisibility(View.GONE);
        pref = new PrefManager(getActivity());


        ConnectivityManager cn=(ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        nf=cn.getActiveNetworkInfo();



        if (pref.isLoggedIn()) {
            Intent intent = new Intent(getActivity(), Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
            layoutEditMobile.setVisibility(View.GONE);


        }

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (pref.isWaitingForSms()) {
            viewPager.setCurrentItem(1);
            layoutEditMobile.setVisibility(View.VISIBLE);

        }

        return rootView;
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCodes(getActivity());
    }

    @Override
    protected void send() {


        hideKeyboard(mPhoneEdit);
        mPhoneEdit.setError(null);
        String phone = validate();
        if (phone == null) {
            mPhoneEdit.requestFocus();
            mPhoneEdit.setError(getString(R.string.label_error_incorrect_phone));
            return;
        }

       else if(nf != null && nf.isConnected()==true )
        {
            // request for sms
            progressBar.setVisibility(View.VISIBLE);

            // saving the mobile number in shared preferences
            pref.setMobileNumber(phone);
            // requesting for sms
            requestForSMS(phone);
        }

        else
        {
            Toast.makeText(getActivity(),"Please check your internet connection ",Toast.LENGTH_LONG).show();
            vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);

            mPhoneEdit.setText("");
        }
    }

    /**
     * Method initiates the SMS request on the server
     * @param phone user valid mobile number
     */
    private void requestForSMS( final String phone) {

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_REQUEST_SMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {
                        // boolean flag saying device is waiting for sms
                        pref.setIsWaitingForSms(true);

                        // moving the screen to next pager item i.e otp screen
                        txtEditMobile.setText(pref.getMobileNumber());
                        viewPager.setCurrentItem(1);
                        layoutEditMobile.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity(),
                                "Error: " + message,
                                Toast.LENGTH_LONG).show();
                    }

                    // hiding the progress bar
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();

                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        "Please check your network ",
                        Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }
        };

        int socketTimeout = 60000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        strReq.setRetryPolicy(policy);

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }



    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp() {
        String otp = inputOtp.getText().toString().trim();

        if (!otp.isEmpty()) {
            Intent grapprIntent = new Intent(getActivity(), HttpService.class);
            grapprIntent.putExtra("otp", otp);
            getActivity().startService(grapprIntent);
        } else {
            Toast.makeText(getActivity(), "Please enter sms code you received", Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_verify_otp:
                verifyOtp();
                break;

            case R.id.btn_edit_mobile:
                viewPager.setCurrentItem(0);
                layoutEditMobile.setVisibility(View.GONE);
                pref.setIsWaitingForSms(false);
                break;
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_sms;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
            }
            return getView().findViewById(resId);
        }


    }
}

