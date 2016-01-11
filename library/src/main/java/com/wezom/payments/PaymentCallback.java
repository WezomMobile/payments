package com.wezom.payments;

/**
 * Created by kartavtsev.s on 09.11.2015.
 */
public interface PaymentCallback<T> {
    void onPaymentSuccess();
    void onPaymentError(T error);
}
