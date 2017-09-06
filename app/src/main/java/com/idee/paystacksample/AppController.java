package com.idee.paystacksample;

import android.app.Application;

import co.paystack.android.PaystackSdk;

/**
 * Created by idee on 8/29/17.
 */

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PaystackSdk.initialize(getApplicationContext());
    }
}
