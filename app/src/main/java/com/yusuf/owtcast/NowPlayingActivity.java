package com.yusuf.owtcast;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yusuf.owtcast.Extra.Constants;
import com.yusuf.owtcast.Model.AdInfo;
import com.yusuf.owtcast.Model.AdLogResponse;
import com.yusuf.owtcast.Model.OwtResponse;
import com.yusuf.owtcast.Rest.ApiInterface;
import com.yusuf.owtcast.Rest.OwtcastApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NowPlayingActivity extends AppCompatActivity {

    private CircularProgressView progressView;

    private ImageView buttonView;
    private TextView adNameView;
    private TextView currentTimeView;
    private ImageView adImageView;

    private int schedule[] = new int[Constants.totalPlay];
    private int currentSchedule = 0;
    private boolean playingStatus = false;

    MediaPlayer mp = new MediaPlayer();
    MediaRecorder mediaRecorder = new MediaRecorder();
    //ExtAudioRecorder mediaRecorder = ExtAudioRecorder.getInstanse(false);

    List<AdInfo> adList = OwtcastApplication.getInstance().getAdSchedule();

    private final Handler handler = new Handler();

    private Bitmap takenPhoto;

    private String recordedFilePath;
    private String capturedImageName;
    private String capturedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        //nav bar settings
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffbd4a")));
        Spannable text = new SpannableString(" N O W   P L A Y I N G");
        text.setSpan(new ForegroundColorSpan(Color.WHITE), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //hide progress view
        progressView = (CircularProgressView)findViewById(R.id.progress_view);
        progressView.setVisibility(View.INVISIBLE);
        progressView.stopAnimation();

        //get views
        buttonView = (ImageView)findViewById(R.id.playbutton);
        adNameView = (TextView)findViewById(R.id.adname);
        adImageView = (ImageView)findViewById(R.id.adimage);
        currentTimeView = (TextView)findViewById(R.id.currenttime);

        ActivityCompat.requestPermissions(this, Constants.permission, 0);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //parse schedule string and make up schedule array
                parseSchedule();

                //start 'clock'
                updatePlayingStatus();

                //start playing first ad
                startPlayingAd();
            }
            else
            {
                this.finish();
            }
        }
    }

    public void initMediaPlayerRecoder()
    {
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnCompletionListener(completionListener);


        //mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

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


    public void parseSchedule()
    {

        for(int i = 0; i < adList.size(); i++)
        {
            String scheduleString = adList.get(i).schedule;
            List<String> indexes = Arrays.asList(scheduleString.split(","));

            for(int j = 0; j < indexes.size(); j++)
            {
                int index = Integer.parseInt(indexes.get(j));

                schedule[index - 1] = i;
            }
        }
    }

    public void updatePlayingStatus()
    {

        if(mp == null)
            return;

        if(mp.isPlaying() == true) {
            progressView.setProgress((int) (((float) mp.getCurrentPosition() / 30000) * 30));

            String ct = String.valueOf(progressView.getProgress());
            currentTimeView.setText(ct);
        }
        Runnable notification = new Runnable() {
            public void run() {
                updatePlayingStatus();
            }
        };
        handler.postDelayed(notification, 1000);
    }

    @Override
    protected void onDestroy() {

        mp.stop();
        mp.release();
        mp = null;

        //mediaRecorder.stop();
        //mediaRecorder.release();

        super.onDestroy();
    }

    public void onPauseClicked(View view)
    {
        if(playingStatus == true)
        {
            buttonView.setImageResource(R.drawable.play);

            mp.pause();


            //pause recording
            //mediaRecorder.pause();
        }
        else
        {
            buttonView.setImageResource(R.drawable.stop);

            mp.start();

            //resume recording
            //mediaRecorder.resume();
        }
        playingStatus = !playingStatus;
    }

    public void startPlayingAd()
    {
        //init media player & recorder
        initMediaPlayerRecoder();

        //take photo
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) //camera
        {
            Bundle extras = data.getExtras();
            takenPhoto =  (Bitmap) extras.get("data");
            capturedImageName = String.valueOf(System.currentTimeMillis()) + ".png";
            File file = new File(new ContextWrapper(this).getDir("captured", MODE_PRIVATE), capturedImageName);
            capturedImagePath = file.getAbsolutePath();
            try {
                takenPhoto.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(capturedImagePath));

                uploadCapturedPhoto();
            }

            catch(Exception e){}
            startAd();
            startRecording();
        }
    }

    public void startAd()
    {
        adNameView.setText(adList.get(currentSchedule).adName);
        progressView.setProgress(0);
        currentTimeView.setText("00:00");

        AdInfo adInfo = adList.get(currentSchedule);
        //download image and set as background img
        final String urlPath = adInfo.imageSource.substring(adInfo.imageSource.indexOf("API"));

        ApiInterface apiService = OwtcastApiClient.getClient().create(ApiInterface.class);
        Call<okhttp3.ResponseBody> call = apiService.downloadImage(urlPath);

        call.enqueue(new Callback<okhttp3.ResponseBody>(){

            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<ResponseBody> response) {

                if(response.body() == null)
                    return;

                Bitmap bm = BitmapFactory.decodeStream(response.body().byteStream());

                adImageView.setImageBitmap(bm);
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {

                Toast.makeText(NowPlayingActivity.this, urlPath + " downloading failed", Toast.LENGTH_LONG).show();
            }
        });

        //submit ad log
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
        String latitude = String.valueOf(latitudeValue);
        String longitude = String.valueOf(longitudeValue);

        Call<AdLogResponse> logCall = apiService.adLog(adInfo.trip, adInfo.ad, latitude, longitude);
        logCall.enqueue(new Callback<AdLogResponse>(){

            @Override
            public void onResponse(Call<AdLogResponse> call, Response<AdLogResponse> response) {

                //


            }

            @Override
            public void onFailure(Call<AdLogResponse> call, Throwable t) {

                Toast.makeText(NowPlayingActivity.this,"Ad Log failed...", Toast.LENGTH_LONG).show();
            }
        });

        //start playing mp3
        try {
            mp.setDataSource(new File(new ContextWrapper(this).getDir("owtaudio", MODE_PRIVATE), adInfo.adName + ".mp3").getAbsolutePath());
            mp.prepare();
            mp.start();
        }
        catch(Exception e)
        {
            //
        }
    }

    public void startRecording()
    {
        /*
        recordedFilePath = (new File(new ContextWrapper(this).getDir("recorded", MODE_PRIVATE), String.valueOf(System.currentTimeMillis())).getAbsolutePath() + ".mp3");
        mediaRecorder.setOutputFile(recordedFilePath);
        try {
            mediaRecorder.prepare();
        }catch(Exception e) {}
        mediaRecorder.start();
        */
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

            //stop playing & recording process
            mp.stop();
            mp.release();
            mp = new MediaPlayer();

           // mediaRecorder.stop();
           // mediaRecorder.release();
            //mediaRecorder = ExtAudioRecorder.getInstanse(false);
           // mediaRecorder = new MediaRecorder();


            //do correlation test  & submit quality log
            sendAdQualityLog();

            //next ad play
            currentSchedule++;
            if(currentSchedule == 12)
            {
                sendTripEndLog();
            }
            else {
                startPlayingAd();
            }


        }
    };

    public void sendAdQualityLog()
    {
        int volume = ((AudioManager)NowPlayingActivity.this.getSystemService(Context.AUDIO_SERVICE)).getStreamVolume(AudioManager.STREAM_MUSIC);
        //double similarity = Correlation.getSimilarity(new File(new ContextWrapper(this).getDir("owtaudio", MODE_PRIVATE), adList.get(currentSchedule).adName + ".mp3"), new File(recordedFilePath));
        double similarity = 1;
        //Toast.makeText(this, String.valueOf(similarity), Toast.LENGTH_LONG).show();

        ApiInterface apiService = OwtcastApiClient.getClient().create(ApiInterface.class);
        Call<OwtResponse> call = apiService.adQuality(String.valueOf(similarity), String.valueOf(volume), capturedImageName, String.valueOf(currentSchedule + 1));

        call.enqueue(new Callback<OwtResponse>(){

            @Override
            public void onResponse(Call<OwtResponse> call, Response<OwtResponse> response) {

                //
            }

            @Override
            public void onFailure(Call<OwtResponse> call, Throwable t) {

                Toast.makeText(NowPlayingActivity.this, "Sending quality log failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void uploadCapturedPhoto()
    {
        File file = new File(capturedImagePath);
        Uri uri = Uri.fromFile(file);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(uri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(file.getName(), file.getName(), requestFile);

        // add another part within the multipart request
        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);

        // finally, execute the request
        Call<ResponseBody> call = OwtcastApiClient.getClient().create(ApiInterface.class).uploadImage(description, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }

    public void sendTripEndLog()
    {
        ApiInterface apiService = OwtcastApiClient.getClient().create(ApiInterface.class);
        Call<OwtResponse> call = apiService.tripEnd(adList.get(0).trip);

        call.enqueue(new Callback<OwtResponse>(){

            @Override
            public void onResponse(Call<OwtResponse> call, Response<OwtResponse> response) {

                //
            }

            @Override
            public void onFailure(Call<OwtResponse> call, Throwable t) {

                Toast.makeText(NowPlayingActivity.this, "Sending trip end log failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
