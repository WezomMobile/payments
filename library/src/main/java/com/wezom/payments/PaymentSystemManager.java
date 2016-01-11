package com.wezom.payments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.wezom.payments.impl.PaypalPaymentSystem;
import com.wezom.payments.impl.Privat24PaymentSystem;

import java.util.HashMap;

import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 09.11.2015.
 */
@Accessors(prefix = "m")
public class PaymentSystemManager {

    private HashMap<Integer, BasePaymentSystem> mPaymentSystems = new HashMap<>();

    private PaymentSystemManager(AppCompatActivity activity, Fragment fragment, String privat24MerchantId,
                                 String paypalClientId) {
        if (!TextUtils.isEmpty(privat24MerchantId)) {
            Privat24PaymentSystem paymentSystem;
            if (activity != null) {
                paymentSystem = new Privat24PaymentSystem(activity, privat24MerchantId);
            } else {
                paymentSystem = new Privat24PaymentSystem(fragment, privat24MerchantId);
            }
            mPaymentSystems.put(Privat24PaymentSystem.PAYMENT_SYSTEM_ID, paymentSystem);
        }

        if (!TextUtils.isEmpty(paypalClientId)) {
            PaypalPaymentSystem paymentSystem;
            if (activity != null) {
                paymentSystem = new PaypalPaymentSystem(activity, paypalClientId);
            } else {
                paymentSystem = new PaypalPaymentSystem(fragment, paypalClientId);
            }
            mPaymentSystems.put(PaypalPaymentSystem.PAYMENT_SYSTEM_ID, paymentSystem);
        }

        for (BasePaymentSystem system : mPaymentSystems.values()) {
            system.initSystem();
        }
    }

    public void setPaymentCallback(PaymentCallback paymentCallback) {
        for (BasePaymentSystem system : mPaymentSystems.values()) {
            system.setPaymentCallback(paymentCallback);
        }
    }

    public Privat24PaymentSystem getPrivat24PaymentSystem() {
        if (mPaymentSystems.containsKey(Privat24PaymentSystem.PAYMENT_SYSTEM_ID)) {
            return (Privat24PaymentSystem) mPaymentSystems.get(Privat24PaymentSystem.PAYMENT_SYSTEM_ID);
        } else {
            throw new IllegalStateException("Privat24 is not initialized");
        }
    }

    public PaypalPaymentSystem getPaypalPaymentSystem() {
        if (mPaymentSystems.containsKey(PaypalPaymentSystem.PAYMENT_SYSTEM_ID)) {
            return (PaypalPaymentSystem) mPaymentSystems.get(PaypalPaymentSystem.PAYMENT_SYSTEM_ID);
        } else {
            throw new IllegalStateException("Paypal is not initialized");
        }
    }

    public void onResume() {
        for (PaymentSystem paymentSystem : mPaymentSystems.values()) {
            paymentSystem.onResume();
        }
    }

    public void onPause() {
        for (PaymentSystem paymentSystem : mPaymentSystems.values()) {
            paymentSystem.onPause();
        }

    }

    public void onStart() {
        for (PaymentSystem paymentSystem : mPaymentSystems.values()) {
            paymentSystem.onStart();
        }

    }

    public void onStop() {
        for (PaymentSystem paymentSystem : mPaymentSystems.values()) {
            paymentSystem.onStop();
        }

    }

    public void onDestroy() {
        for (PaymentSystem paymentSystem : mPaymentSystems.values()) {
            paymentSystem.onDestroy();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (PaymentSystem paymentSystem : mPaymentSystems.values()) {
            paymentSystem.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static class Builder {

        private AppCompatActivity mActivity;
        private Fragment mFragment;
        private String mPrivat24MerchantId;
        private String mPaypalClientId;

        private Builder(AppCompatActivity activity) {
            mActivity = activity;
        }

        private Builder(Fragment fragment) {
            mFragment = fragment;
        }

        public static Builder from(AppCompatActivity activity) {
            return new Builder(activity);
        }

        public static Builder from(Fragment fragment) {
            return new Builder(fragment);
        }

        public Builder privat24(String merchantId) {
            mPrivat24MerchantId = merchantId;
            return this;
        }

        public Builder paypal(String clientId) {
            mPaypalClientId = clientId;
            return this;
        }

        public PaymentSystemManager build() {
            return new PaymentSystemManager(mActivity, mFragment, mPrivat24MerchantId, mPaypalClientId);
        }
    }
}
