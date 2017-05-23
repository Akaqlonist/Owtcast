package com.yusuf.owtcast;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yusuf.owtcast.Model.UserInfo;
import com.yusuf.owtcast.Rest.ApiInterface;
import com.yusuf.owtcast.Rest.OwtcastApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private CircularProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.findViewById(R.id.drivermobile).requestFocus();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffbd4a")));
        getSupportActionBar().setTitle("SIGN IN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);
        progressView.setVisibility(View.INVISIBLE);
        progressView.stopAnimation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void onSignInClicked(View v)
    {
        String driverMobile = ((EditText)findViewById(R.id.drivermobile)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        if(driverMobile.isEmpty() || password.isEmpty())
        {
            Toast.makeText(SignInActivity.this, "Please input required information.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        ApiInterface apiService = OwtcastApiClient.getClient().create(ApiInterface.class);
        Call<UserInfo> call = apiService.login(driverMobile, password);

        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {

                UserInfo result = response.body();

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                if(result.error == false)
                {
                    OwtcastApplication.getInstance().setUserInfo(result);

                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);

                    SignInActivity.this.finish();
                }
                else
                {
                    Toast.makeText(SignInActivity.this, result.message, Toast.LENGTH_SHORT).show();

                    progressView.stopAnimation();
                    progressView.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "Connection failure , please try again.", Toast.LENGTH_SHORT).show();

                progressView.stopAnimation();
                progressView.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        });
    }

    void onNewPasswordClicked(View v)
    {

    }
}
