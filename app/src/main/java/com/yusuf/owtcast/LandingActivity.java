package com.yusuf.owtcast;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        getSupportActionBar().hide();
    }

    void onSignInClicked(View v)
    {
        Intent intent = new Intent(LandingActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    void onRegisterClicked(View v)
    {
        Intent intent = new Intent(LandingActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


}
