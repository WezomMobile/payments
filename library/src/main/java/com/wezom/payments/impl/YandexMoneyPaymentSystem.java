package com.wezom.payments.impl;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.wezom.payments.BasePaymentSystem;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.methods.params.PaymentParams;

import ru.yandex.money.android.PaymentActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created: Zorin A.
 * Date: 18.05.2016.
 */
public class YandexMoneyPaymentSystem extends BasePaymentSystem<P2pTransferParams, String> {
    private static final String TAG = "YandexMoneyPaymentSystem";

    public static final Integer PAYMENT_SYSTEM_ID = 3;
    private static final String HOST = "https://demomoney.yandex.ru";
    private static final int REQUEST_CODE = 101;

    private String mApplicationId;
    private String mMerchantId;
    private P2pTransferParams mTransferParams;

    public String getMerchantId() {
        return mMerchantId;
    }

    public YandexMoneyPaymentSystem(AppCompatActivity activity, String applicationId, String merchantId) {
        super(activity);
        mApplicationId = applicationId;
        mMerchantId = merchantId;
    }

    public YandexMoneyPaymentSystem(Fragment fragment, String applicationId, String merchantId) {
        super(fragment);
        mApplicationId = applicationId;
        mMerchantId = merchantId;
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
    public void makePayment(P2pTransferParams transferParams) {
        mTransferParams = transferParams;

        if (transferParams == null) {
            throw new NullPointerException("Set PaymentParams");
        }

        Intent intent = PaymentActivity.getBuilder(super.getContext())
                .setPaymentParams(transferParams)
                .setClientId(mApplicationId)
                .setHost(HOST)
                .build();

        if (getActivity() != null) {
            super.getActivity().startActivityForResult(intent, REQUEST_CODE);
        } else {
            super.getFragment().startActivityForResult(intent, REQUEST_CODE);
        }
    }
}
