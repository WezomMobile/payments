package com.wezom.payments.impl;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.wezom.payments.BasePaymentSystem;

import org.json.JSONException;

/**
 * Created by kartavtsev.s on 11.11.2015.
 */
public class PaypalPaymentSystem extends BasePaymentSystem<PayPalPayment, Object> {

    public static final Integer PAYMENT_SYSTEM_ID = 1;

    private PayPalConfiguration mPayPalConfiguration;
    private String mMerchantId;

    public PaypalPaymentSystem(AppCompatActivity activity, String merchantId) {
        super(activity);
        mMerchantId = merchantId;
    }

    public PaypalPaymentSystem(Fragment fragment, String paypalClientId) {
        super(fragment);
        mMerchantId = paypalClientId;
    }

    @Override
    public void initSystem() {
        mPayPalConfiguration = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(mMerchantId);

        startPaypalService();
    }

    @Override
    public void makePayment(PayPalPayment paymentData) {
        Intent intent = new Intent(getContext(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, paymentData);
        if (getActivity() != null) {
            getActivity().startActivityForResult(intent, 0);
        } else {
            getFragment().startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onDestroy() {
        if (getActivity() != null) {
            getActivity().stopService(new Intent(getContext(), PayPalService.class));
        } else {
            getFragment().getActivity().stopService(new Intent(getContext(), PayPalService.class));
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {

                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.
                    if (mPaymentCallback != null) {
                        mPaymentCallback.onPaymentSuccess();
                    }
                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (mPaymentCallback != null) {
                mPaymentCallback.onPaymentError("User canceled");
            }
            Log.i("paymentExample", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            if (mPaymentCallback != null) {
                mPaymentCallback.onPaymentError("An invalid Payment or PayPalConfiguration was submitted");
            }
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    private void startPaypalService() {
        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, mPayPalConfiguration);
        if (getActivity() != null) {
            getActivity().startService(intent);
        } else {
            getFragment().getActivity().startService(intent);
        }
    }
}
