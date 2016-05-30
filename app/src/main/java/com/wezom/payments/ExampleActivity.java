package com.wezom.payments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.wezom.payments.impl.YandexMoneyPaymentSystem;
import com.yandex.money.api.methods.params.P2pTransferParams;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExampleActivity extends AppCompatActivity implements PaymentCallback<String> {

    private static final String APPLICATION_ID = "370190E9AC2656043498E48F7A8CCEBAD03D15E4CC4CC988A757825A560631EC";

    @Bind(R.id.merchant_id)
    EditText mMerchantIdEditText;
    @Bind(R.id.amount)
    EditText mAmountEditText;

    private YandexMoneyPaymentSystem mYandexMoneyPaymentSystem;
    private PaymentSystemManager mPaymentManager;

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
        mPaymentManager = PaymentSystemManager.Builder.from(this)
                .yandexMoneyApplicationId(APPLICATION_ID)
                .yandexMoneyMerchantId("")
                .build();

        mPaymentManager.setPaymentCallback(this);
        mYandexMoneyPaymentSystem = mPaymentManager.getYandexPaymentSystem();

    }

    @OnClick(R.id.pay_yandex_money)
    void payYandexMoney() {
        final String amount = mAmountEditText.getText().toString();
        final String merchantId = mMerchantIdEditText.getText().toString().replaceAll("\\D", "");

        P2pTransferParams.Builder builder = new P2pTransferParams.Builder(
                !TextUtils.isEmpty(merchantId) ? merchantId :
                        mYandexMoneyPaymentSystem.getMerchantId());
        builder.setAmount(new BigDecimal(amount));

        mYandexMoneyPaymentSystem.makePayment(builder.create());
    }

    @Override
    public void onPaymentSuccess() {

    }

    @Override
    public void onPaymentError(String error) {

    }
}
