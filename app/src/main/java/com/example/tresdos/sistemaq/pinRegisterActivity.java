package com.example.tresdos.sistemaq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;

public class pinRegisterActivity extends AppCompatActivity {

    private PinLockView mPinLockView, mPinLockView2;
    private IndicatorDots mIndicatorDots, mIndicatorDots2;
    private final static String TAG = pinRegisterActivity.class.getSimpleName();
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    //    tipo p=padre h=hijo n=ninguno
    public static final String Password = "passwordKey";
    public static final String BtnVisible = "btnKey";
    private final static String TRUE_CODE = "123456";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_register);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView2 = (PinLockView) findViewById(R.id.pin_lock_view2);
        mIndicatorDots2 = (IndicatorDots) findViewById(R.id.indicator_dots2);
        mPinLockView2.setVisibility(View.INVISIBLE);
        mIndicatorDots2.setVisibility(View.INVISIBLE);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLength(6);

        mPinLockView2.attachIndicatorDots(mIndicatorDots2);
        mPinLockView2.setPinLength(6);

        mPinLockView.setPinLockListener(new PinLockListener() {
            @Override
            public void onComplete(final String pin) {
                Log.d(TAG, "lock code: " + pin);
                Toast.makeText(pinRegisterActivity.this, "Confirma tu password", Toast.LENGTH_LONG).show();
                mPinLockView.setVisibility(View.INVISIBLE);
                mIndicatorDots.setVisibility(View.INVISIBLE);
                mPinLockView2.setVisibility(View.VISIBLE);
                mIndicatorDots2.setVisibility(View.VISIBLE);
                mPinLockView2.setPinLockListener(new PinLockListener() {
                    @Override
                    public void onComplete(String pin2) {
                        if (pin.equals(pin2)){
                            Toast.makeText(pinRegisterActivity.this, "Pin Aceptado", Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(BtnVisible, "true");
                            editor.putString(Password, pin);
                            editor.commit();
                            Intent intent=new Intent();
                            intent.putExtra("MESSAGE","true");
                            setResult(4,intent);
                            finish();
                        }
                        else {
                            Toast.makeText(pinRegisterActivity.this, "Error en los pin", Toast.LENGTH_LONG).show();
                            final Intent intent = getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onEmpty() {

                    }

                    @Override
                    public void onPinChange(int pinLength, String intermediatePin) {

                    }
                });
//                //User input true code
//                if (pin.equals(TRUE_CODE)) {
//                    Toast.makeText(pinRegisterActivity.this, "Correcto", Toast.LENGTH_SHORT).show();
////                    Intent intent = new Intent(LockActivity.this, MainActivity.class);
////                    intent.putExtra("code", pin);
////                    startActivity(intent);
////                    finish();
//                } else {
//                    Toast.makeText(pinRegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "lock code is empty!");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
            }
        });

    }
}
