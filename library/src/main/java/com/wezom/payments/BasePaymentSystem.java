package com.wezom.payments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 09.11.2015.
 */
@Accessors(prefix = "m")
abstract public class BasePaymentSystem<T1, T2> implements PaymentSystem<T1> {

    protected WeakReference<AppCompatActivity> mActivity;
    @Getter
    protected Fragment mFragment;
    @Setter
    protected PaymentCallback<T2> mPaymentCallback;

    public BasePaymentSystem(AppCompatActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

    public BasePaymentSystem(Fragment fragment) {
        mFragment = fragment;
    }

    protected AppCompatActivity getActivity() {
        return mActivity == null ? null : mActivity.get();
    }

    protected Context getContext() {
        return getActivity() != null ? getActivity() : getFragment().getContext();
    }

    abstract public void initSystem();

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStart() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
