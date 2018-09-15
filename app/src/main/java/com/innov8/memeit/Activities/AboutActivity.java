package com.innov8.memeit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.innov8.memeit.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private enum Person{
        jv,biruk,memeit
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.donate).setOnClickListener(this);
        findViewById(R.id.call_jv).setOnClickListener(this);
        findViewById(R.id.call_biruk).setOnClickListener(this);
        findViewById(R.id.email_biruk).setOnClickListener(this);
        findViewById(R.id.email_jv).setOnClickListener(this);
        findViewById(R.id.feedback).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.donate:;
            case R.id.call_biruk: call(Person.biruk);
            case R.id.email_biruk:email(Person.biruk);
            case R.id.call_jv:call(Person.jv);
            case R.id.email_jv:email(Person.jv);
            case R.id.feedback:email(Person.memeit);
        }
    }
    public void call(Person p){
        String uri = "tel:" + (p==Person.jv ? "+251943172286" : "+251946759214") ;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }
    public void email(Person p){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        String s = "";
        switch (p){case biruk:s = "birukabraham741@gmail.com";case jv:s = "";case memeit:s = "innovapp.ethio@gmail.com";}//todo: jv add your email here
        intent.putExtra(Intent.EXTRA_EMAIL,s);
        intent.putExtra(Intent.EXTRA_SUBJECT, p==Person.memeit ? "Feedback on memeit" : "");
        intent.putExtra(Intent.EXTRA_TEXT, " ");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }
}
