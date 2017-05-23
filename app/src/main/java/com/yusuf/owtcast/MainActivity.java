package com.yusuf.owtcast;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import android.location.LocationManager;
import android.location.Location;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yusuf.owtcast.Model.AdInfo;
import com.yusuf.owtcast.Rest.ApiInterface;
import com.yusuf.owtcast.Rest.OwtcastApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private CircularProgressView progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffbd4a")));
        Spannable text = new SpannableString(" O W T C A S T");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        progressView = (CircularProgressView)findViewById(R.id.progress_view);
        progressView.setVisibility(View.INVISIBLE);
        progressView.stopAnimation();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.logout:
                this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    void onStartClicked(View v)
    {
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        double longitudeValue = 0;
        double latitudeValue = 0;

        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitudeValue = location.getLongitude();
            latitudeValue = location.getLatitude();
        } catch (SecurityException e) {
             // lets the user know there is a problem with the gps
        }



        String driverId = OwtcastApplication.getInstance().getUserInfo().driverID;
        String slots = "30";
        String latitude = String.valueOf(latitudeValue);
        String longitude = String.valueOf(longitudeValue);

        ApiInterface apiService = OwtcastApiClient.getClient().create(ApiInterface.class);
        Call<List<AdInfo>> call = apiService.tripLog(driverId, latitude, longitude, slots);

        call.enqueue(new Callback<List<AdInfo>>() {
            @Override
            public void onResponse(Call<List<AdInfo>> call, Response<List<AdInfo>> response) {

                List<AdInfo> result = response.body();

                OwtcastApplication.getInstance().setAdSchedule(result);

                if(result == null) {
                    Toast.makeText(MainActivity.this, "Oops! Trip not logged at this time.", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    boolean ret = downloadAudio(result);

                }
            }

            @Override
            public void onFailure(Call<List<AdInfo>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Connection failure , please try again.", Toast.LENGTH_SHORT).show();

                progressView.stopAnimation();
                progressView.setVisibility(View.GONE);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }
        });

    }

    int count  = 0;

    public boolean downloadAudio(List<AdInfo> adList)
    {

        for(int i = 0 ; i < adList.size(); i++)
        {

            final String urlPath = adList.get(i).audioSource;
            final String fileName = adList.get(i).adName + ".mp3";

            final File file = new File(new ContextWrapper(this).getDir("owtaudio", MODE_PRIVATE), fileName);
            if(!file.exists())
            {

                ApiInterface apiService = OwtcastApiClient.getClient().create(ApiInterface.class);
                Call<okhttp3.ResponseBody> call = apiService.downloadAudio(urlPath.substring(urlPath.indexOf("API")));

                    call.enqueue(new Callback<okhttp3.ResponseBody>(){

                    @Override
                    public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {

                        storeDownloadedAudio(file, response.body());

                        count++;

                        if(count == 4)
                        {
                            Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {

                        Toast.makeText(MainActivity.this, urlPath + " downloading failed", Toast.LENGTH_LONG).show();

                        count++;

                        if(count == 4)
                        {
                            Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
                            startActivity(intent);
                            MainActivity.this.finish();
                        }
                    }
                });
            }
            else
            {
                count++;
            }

            if(count == 4)
            {
                Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }

        }

        return true;
    }

    public void storeDownloadedAudio(File file, okhttp3.ResponseBody body) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[4096];

            long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;

            inputStream = body.byteStream();

            outputStream = new FileOutputStream(file);
            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

            }

            outputStream.close();

        } catch (IOException e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            Log.e("exception", e.toString());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
