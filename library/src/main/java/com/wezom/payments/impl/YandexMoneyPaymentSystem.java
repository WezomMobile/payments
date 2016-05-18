package com.wezom.payments.impl;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.wezom.payments.BasePaymentSystem;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.methods.params.PaymentParams;

import java.math.BigDecimal;

import ru.yandex.money.android.PaymentActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created: Zorin A.
 * Date: 18.05.2016.
 */
public class YandexMoneyPaymentSystem extends BasePaymentSystem<PaymentParams, String> {
    private static final String TAG = "YandexMoneyPaymentSystem";

    public static final Integer PAYMENT_SYSTEM_ID = 3;
    private static final String HOST = "https://demomoney.yandex.ru";
    private static final int REQUEST_CODE = 101;

    String mClientId;
    P2pTransferParams mTransferParams;

    public YandexMoneyPaymentSystem(AppCompatActivity activity, String clientId) {
        super(activity);
        mClientId = clientId;
    }

    public YandexMoneyPaymentSystem(Fragment fragment, String clientId) {
        super(fragment);
        mClientId = clientId;
    }

    @Override
    public void initSystem() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mPaymentCallback.onPaymentSuccess();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (mPaymentCallback != null) {
                mPaymentCallback.onPaymentError("User canceled");
            }
        }
    }

    @Override
    public void makePayment(PaymentParams paymentData) {
        if (mTransferParams == null && paymentData == null) {
            throw new NullPointerException("Set PaymentParams or P2pTransferParams");
        }
        Intent intent = PaymentActivity.getBuilder(super.getContext())
                .setPaymentParams(mTransferParams == null ? paymentData : mTransferParams)
                .setClientId(mClientId)
                .setHost(HOST)
                .build();

        if (getActivity() != null) {
            super.getActivity().startActivityForResult(intent, REQUEST_CODE);
        } else {
            super.getFragment().startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private String getPaymentTo(String paymentTo) {
        return paymentTo.replaceAll("\\D", "");
    }

    private BigDecimal getAmount(String amount) {
        return new BigDecimal(amount);
    }

    public void setTransferParams(String paymentTo, String amount) {
        if (isValid(paymentTo, amount)) {
            mTransferParams = new P2pTransferParams.Builder(getPaymentTo(paymentTo))
                    .setAmount(getAmount(amount))
                    .create();
        }
    }

    private boolean isValid(String paymentTo, String amount) {
        return !TextUtils.isEmpty(paymentTo) &&
                !TextUtils.isEmpty(amount) && getAmount(amount).doubleValue() > 0;
    }


}
