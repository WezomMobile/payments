package com.wezom.payments;

import android.content.Intent;

/**
 * Created by kartavtsev.s on 09.11.2015.
 */
public interface PaymentSystem<T> {
    void makePayment(T paymentData);
    void onResume();
    void onPause();
    void onStart();
    void onStop();
    void onDestroy();
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
