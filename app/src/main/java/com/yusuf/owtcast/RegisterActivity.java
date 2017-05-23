package com.yusuf.owtcast;

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
import com.yusuf.owtcast.Model.OwtResponse;
import com.yusuf.owtcast.Rest.ApiInterface;
import com.yusuf.owtcast.Rest.OwtcastApiClient;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private CircularProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffbd4a")));
        getSupportActionBar().setTitle("REGISTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);
        progressView.setVisibility(View.INVISIBLE);
        progressView.stopAnimation();


    }


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

    void onContinueClicked(View v)
    {
        String firstName = ((EditText)findViewById(R.id.firstname)).getText().toString();
        String lastName = ((EditText)findViewById(R.id.lastname)).getText().toString();
        String email = ((EditText)findViewById(R.id.email)).getText().toString();
        String driverMobile = ((EditText)findViewById(R.id.drivermobile)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();
        String driverCity = ((EditText)findViewById(R.id.drivercity)).getText().toString();
        String driverRef = ((EditText)findViewById(R.id.driver_ref)).getText().toString();

        if(firstName.isEmpty() || lastName.isEmpty() || driverMobile.isEmpty()|| password.isEmpty()|| driverCity.isEmpty())
        {
            Toast.makeText(RegisterActivity.this, "Please input required information.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ApiInterface apiService = OwtcastApiClient.getClient().create(ApiInterface.class);
        Call<OwtResponse> call = apiService.signup(firstName, lastName, driverMobile, email, driverCity, driverRef, password);
        RequestBody body = call.request().body();
        call.enqueue(new Callback<OwtResponse>() {
            @Override
            public void onResponse(Call<OwtResponse> call, Response<OwtResponse> response) {

                OwtResponse result = response.body();

                Toast.makeText(RegisterActivity.this, result.message, Toast.LENGTH_SHORT).show();

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                RegisterActivity.this.finish();


            }

            @Override
            public void onFailure(Call<OwtResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Connection failure , please try again.", Toast.LENGTH_SHORT).show();

                progressView.stopAnimation();
                progressView.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        });
    }
}
