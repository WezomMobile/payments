package com.wezom.payments.impl;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.paymentwall.sdk.pwlocal.message.CustomRequest;
import com.paymentwall.sdk.pwlocal.ui.PwLocalActivity;
import com.paymentwall.sdk.pwlocal.utils.ApiType;
import com.paymentwall.sdk.pwlocal.utils.Const;
import com.paymentwall.sdk.pwlocal.utils.Key;
import com.paymentwall.sdk.pwlocal.utils.ResponseCode;
import com.wezom.payments.BasePaymentSystem;
import com.wezom.payments.R;

/**
 * Created: Vitaliy Oskalenko
 * Date: 15.06.2016.
 */
public class PaymentWallPaymentSystem extends BasePaymentSystem<CustomRequest, String> {

    private static final String TAG = "PaymentWallPaymentSystem";
    public static final Integer PAYMENT_SYSTEM_ID = 4;

    private String mProjectKey;
    private String mSecretKey;
    private boolean mIsSandBox;

    public PaymentWallPaymentSystem(AppCompatActivity activity, String projectKey, String secretKey) {
        super(activity);
        mProjectKey = projectKey;
        mSecretKey = secretKey;
    }

    public PaymentWallPaymentSystem(Fragment fragment, String projectKey, String secretKey) {
        super(fragment);
        mProjectKey = projectKey;
        mSecretKey = secretKey;
    }

    @Override
    public void initSystem(boolean isSandBox) {
        mIsSandBox = isSandBox;
    }

    @Override
    public void makePayment(CustomRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Payment request can't be null");
        }

        if (mProjectKey == null) {
            throw new IllegalArgumentException("ProjectKey can't be null");
        }

        if (mSecretKey == null) {
            throw new IllegalArgumentException("SecretKey can't be null");
        }


        request.put(Const.P.KEY, mProjectKey);
        request.put(Const.P.EVALUATION, mIsSandBox ? "1" : "2");
        request.setSecret(mSecretKey);

        Intent intent = new Intent(getContext(), PwLocalActivity.class);
        intent.putExtra(Key.CUSTOM_REQUEST_TYPE, ApiType.DIGITAL_GOODS);
        intent.putExtra(Key.CUSTOM_REQUEST_MAP, request);

        if (getActivity() != null) {
            super.getActivity().startActivityForResult(intent, PwLocalActivity.REQUEST_CODE);
        } else {
            super.getFragment().startActivityForResult(intent, PwLocalActivity.REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mPaymentCallback == null) {
            throw new IllegalArgumentException("mPaymentCallback can't be null");
        }

        if (requestCode == PwLocalActivity.REQUEST_CODE) {

            switch (resultCode) {
                case ResponseCode.CANCEL:
                    mPaymentCallback.onPaymentError(getContext().getString(R.string.user_cancel_payment));
                    break;
                case ResponseCode.ERROR:
                    final String errorMessage = data.getStringExtra(Key.SDK_ERROR_MESSAGE);
                    mPaymentCallback.onPaymentError(errorMessage);
                    break;
                case ResponseCode.SUCCESSFUL:
                    mPaymentCallback.onPaymentSuccess();
                    break;
                default:
                    break;
            }
        }
    }
}
