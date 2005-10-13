package com.innov8.memeit.Activities;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.innov8.memeit.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    Typeface avenir;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        avenir = Typeface.createFromAsset(getAssets(),"fonts/avenir.ttf");

        name.setTypeface(avenir);
        password.setTypeface(avenir);

    }
}
