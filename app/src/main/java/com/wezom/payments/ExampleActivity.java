package com.wezom.payments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.wezom.payments.impl.YandexMoneyPaymentSystem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExampleActivity extends AppCompatActivity implements PaymentCallback<String> {

    @BindView(R.id.merchant_id)
    EditText mMerchantIdEditText;
    @BindView(R.id.amount)
    EditText mAmountEditText;

    YandexMoneyPaymentSystem mYandexMoneyPaymentSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initPaymentSystem();
    }

    private void initPaymentSystem() {
        String testYandexClientId = "370190E9AC2656043498E48F7A8CCEBAD03D15E4CC4CC988A757825A560631EC";

        PaymentSystemManager mPaymentManager = PaymentSystemManager.Builder.from(this).yandexMoney(testYandexClientId).build();
        mPaymentManager.setPaymentCallback(this);
        mYandexMoneyPaymentSystem = mPaymentManager.getYandexPaymentSystem();

    }

    @OnClick(R.id.pay_yandex_money)
    void payYandexMoney() {
        String merchantId = mMerchantIdEditText.getText().toString();
        String amount = mAmountEditText.getText().toString();
        mYandexMoneyPaymentSystem.setTransferParams(merchantId, amount);
        mYandexMoneyPaymentSystem.makePayment(null);

    }

    @Override
    public void onPaymentSuccess() {

    }

    @Override
    public void onPaymentError(String error) {

    }
}
