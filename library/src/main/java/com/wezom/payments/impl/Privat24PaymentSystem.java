package com.wezom.payments.impl;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.wezom.payments.BasePaymentSystem;
import com.wezom.payments.R;

import ua.privatbank.framework.api.Message;
import ua.privatbank.payoneclicklib.Api;
import ua.privatbank.payoneclicklib.Pay;
import ua.privatbank.payoneclicklib.model.PayData;

/**
 * Created by kartavtsev.s on 09.11.2015.
 */
public class Privat24PaymentSystem extends BasePaymentSystem<PayData, String> implements
        ua.privatbank.framework.api.Api.ApiEventListener<Api>, Pay.PaymentCallBack, Pay.OtpCallBack {

    private static final String TAG = "Privat24PaymenSystem";
    public static final Integer PAYMENT_SYSTEM_ID = 0;
    private static final int GPS_ENABLE_CODE = 900;

    private Pay mPay;
    private PayData mPayData;
    private String mClientId;

    public Privat24PaymentSystem(AppCompatActivity activity, String clientId) {
        super(activity);
        mClientId = clientId;
    }

    public Privat24PaymentSystem(Fragment fragment, String privat24MerchantId) {
        super(fragment);
        mClientId = privat24MerchantId;
    }

    @Override
    public void initSystem() {
        mPay = new Pay(getContext(), this, mClientId);
    }

    @Override
    public void makePayment(PayData paymentData) {
        mPayData = paymentData;
        mPay.pay(paymentData, this);
    }

    @Override
    public void onApiStartRequest() {
        Log.d(TAG, "Api start request");
    }

    @Override
    public void onApiFinishRequest() {
        Log.d(TAG, "Api finish request");
    }

    @Override
    public void onApiError(Api api, Message.ErrorCode errorCode) {
        Log.d(TAG, "Api error " + errorCode.name());
        switch (errorCode) {
            case CODE_GEOLOCATION_DISABLED:
                showGeolocationEnableDialog();
                return;
        }
        switch (api.getLastServerFailCode()) {
            case "err_link_device_phone_not_exist":
                showPhoneNumberDialog();
                return;
        }

        if (mPaymentCallback != null) {
            mPaymentCallback.onPaymentError(api.getLastServerFailCode());
        }
    }

    @Override
    public void onPaymentSuccess() {
        mPaymentCallback.onPaymentSuccess();
    }

    @Override
    public void onReceiveOtpSend(Pay.OtpCheckListener otpCheckListener) {
        showSmsCodeDialog(otpCheckListener);
    }

    @Override
    public void onPaymentFailed() {
        mPay.reset();
        if (mPaymentCallback != null) {
            mPaymentCallback.onPaymentError("Payment failed");
        }
    }

    @Override
    public void onPaymentProcessing() {
        Log.d(TAG, "Payment processing");
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed");
    }

    private void showPhoneNumberDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle(R.string.enter_phone_number);
        alert.setMessage(R.string.enter_your_phone_number);
        final EditText input = new EditText(getContext());
        alert.setView(input);

        alert.setPositiveButton("OK", (dialog, whichButton) -> {
            String phone = input.getText().toString();
            mPayData.setPhone(phone);
            mPay.pay(mPayData, this);
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void showSmsCodeDialog(Pay.OtpCheckListener otpCheckListener) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle(R.string.code);
        final EditText input = new EditText(getContext());
        alert.setView(input);

        alert.setPositiveButton("Ок", (dialog, whichButton) -> {
            otpCheckListener.onOtpCheck(mPayData.getPhone(), input.getText().toString(), this);
        });
    }

    private void showGeolocationEnableDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.need_enable_geolocation_in_order_to_pay)
                .setMessage(R.string.need_enable_geolocation_in_order_to_pay)
                .setPositiveButton(R.string.enable, (dialog, which) -> {
                    final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    if (getActivity() != null) {
                        getActivity().startActivityForResult(new Intent(action), GPS_ENABLE_CODE);
                    } else {
                        getFragment().startActivityForResult(new Intent(action), GPS_ENABLE_CODE);
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    if (mPaymentCallback != null) {
                        mPaymentCallback.onPaymentError("Used didn't enable GPS");
                    }
                })
                .create();

        alertDialog.show();
    }

    @Override
    public void onOtpSuccess() {
        Log.d(TAG, "Otp success");
    }

    @Override
    public void onOtpFailed() {
        Log.d(TAG, "Otp failed");
        if (mPaymentCallback != null) {
            mPaymentCallback.onPaymentError("Wrong sms code");
        }
    }

    @Override
    public void onDestroy() {
        mPay.destroy();
        super.onDestroy();
    }

/*    public void showCardList(List<Card> list) {
        String[] cardIdList = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            cardIdList[i] = list.get(i).getCard_id();
        }
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, cardIdList);
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.choose_card_for_the_payment))
                .setAdapter(stringArrayAdapter, (dialog, which) -> {
                    getPresenter().payWithCardNumber(list.get(which).getCard_id(),
                            String.valueOf(mOrder.getRecommendedCost()), mOrder.getCurrency());
                }).show();
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
