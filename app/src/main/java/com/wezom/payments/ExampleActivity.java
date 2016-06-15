package com.wezom.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.EditText;

import com.paymentwall.sdk.pwlocal.message.CustomRequest;
import com.paymentwall.sdk.pwlocal.utils.Const;
import com.wezom.payments.impl.PaymentWallPaymentSystem;
import com.wezom.payments.impl.YandexMoneyPaymentSystem;
import com.yandex.money.api.methods.params.P2pTransferParams;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExampleActivity extends AppCompatActivity implements PaymentCallback<String> {

    @Bind(R.id.merchant_id)
    EditText mMerchantIdEditText;
    @Bind(R.id.amount)
    EditText mAmountEditText;

    private PaymentSystemManager mPaymentManager;
    private YandexMoneyPaymentSystem mYandexMoneyPaymentSystem;
    private PaymentWallPaymentSystem mPaymentWallPaymentSystem;

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
                .yandexMoneyApplicationId("370190E9AC2656043498E48F7A8CCEBAD03D15E4CC4CC988A757825A560631EC")
                .yandexMoneyMerchantId("1212")
                .paymentWallProjectKey("1405c9f43842efdce0c1de8056b2f809")
                .paymentWallSecretKey("5d7d79644627101fe5b3568c4f3dbe72")
                .build();

        mPaymentManager.setPaymentCallback(this);

        mYandexMoneyPaymentSystem = mPaymentManager.getYandexPaymentSystem();
        mPaymentWallPaymentSystem = mPaymentManager.getPaymentWallPaymentSystem();

        mYandexMoneyPaymentSystem.initSystem(true);
        mPaymentWallPaymentSystem.initSystem(true);
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

    @OnClick(R.id.pay_payment_wall)
    void payPaymentWall() {

        final String amount = mAmountEditText.getText().toString();

        CustomRequest request = new CustomRequest();
        request.put(Const.P.WIDGET, "p4_1");
        request.put(Const.P.UID, "vitaliy");
        request.put(Const.P.AG_EXTERNAL_ID, "test product");
        request.put(Const.P.AG_NAME, "Test item");
        request.put(Const.P.CURRENCYCODE, "USD");
        request.put(Const.P.AMOUNT, amount);
        request.put(Const.P.AG_TYPE, "fixed");
        request.put(Const.P.LOCATION_COUNTRY, "UNITED STATES");
        request.put(Const.P.COUNTRY_CODE, "USA");
        request.put(Const.P.COUNTRY, "UNITED STATES");
        request.setSignVersion(2);

        mPaymentWallPaymentSystem.makePayment(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mPaymentManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPaymentSuccess() {

    }

    @Override
    public void onPaymentError(String error) {

    }
}
