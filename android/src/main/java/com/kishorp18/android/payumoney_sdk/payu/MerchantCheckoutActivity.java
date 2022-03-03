package com.kishorp18.android.payumoney_sdk.payu;

import android.os.Bundle;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;

import com.kishorp18.android.payumoney_sdk.R;

public class MerchantCheckoutActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_checkout);

        TextView postParamsTextView = (TextView) findViewById(R.id.text_view_post_params);

        //post data received by this activity contains all params posted to webview in transaction request.
        String postData = getIntent().getStringExtra("postData");
        postParamsTextView.setText("Merchant's post data : "+postData);

    }
}