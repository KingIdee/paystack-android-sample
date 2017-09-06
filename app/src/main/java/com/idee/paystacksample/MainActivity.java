package com.idee.paystacksample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    Charge charge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
          card number, expiry month, expiry year, card CVC
         */

        Card card = new Card.Builder("5060666666666666666", 8, 2019, "123").build();
        if (!card.isValid())
            return;

        charge = new Charge();

        charge.setCard(card);
        charge.setCurrency("NGN");
        charge.setAmount(100);
        charge.setEmail("customer@email.com");



        fetchAccessCode();

    }

    private void fetchAccessCode() {

        Map<String,Object> object = new HashMap<>();
        object.put("reference",charge.getReference());
        object.put("amount", charge.getAmount());
        object.put("email", charge.getEmail());

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),(new JSONObject(object)).toString());
        Call<ResponseBody> call = NetworkClass.providesRetrofitRequestClient().fetchAccessCode("Bearer "+PAYSTACK_SECRET_KEY,body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.body().string());
                    Log.i(TAG,jsonObject.toString());
                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
                    //Log.d(TAG,jsonObject1.getString("reference"));
                    charge.setAccessCode(jsonObject1.getString("access_code"));
                    charge.setReference(jsonObject1.getString("reference"));
                    chargeCard();

                } catch (JSONException | IOException e) {
                    e.printStackTrace();

                    try {
                        Log.d(TAG,jsonObject.getString("message"));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                Log.i(TAG,t.getMessage());
            }
        });

    }



    private void chargeCard() {

        Log.i(TAG,"Preparing to perform transaction on card");
        PaystackSdk.chargeCard(MainActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                Log.i(TAG,"Successful transaction");
                Log.i(TAG,transaction.getReference());
                //TODO: verify here
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                Log.i(TAG+" beforeValidate","About to request OTP");
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }


            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
                Log.i(TAG+" Error",error.getMessage());
            }

        });
    }



}
