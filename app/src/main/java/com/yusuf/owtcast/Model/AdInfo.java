package com.yusuf.owtcast.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by DragonWarrior on 1/30/2017.
 */

public class AdInfo {

    @SerializedName("error")
    public boolean error;

    @SerializedName("message")
    public String message;

    @SerializedName("Trip")
    public String trip;

    @SerializedName("Ad")
    public String ad;

    @SerializedName("AdName")
    public String adName;

    @SerializedName("Description")
    public String description;

    @SerializedName("AudioSource")
    public String audioSource;

    @SerializedName("ImageSource")
    public String imageSource;

    @SerializedName("Schedule")
    public String schedule;


    public AdInfo(boolean error, String message, String trip, String ad, String adName, String description, String audioSource, String imageSource, String schedule)
    {
        this.error = error;
        this.message = message;
        this.trip = trip;
        this.ad = ad;
        this.adName = adName;
        this.description = description;
        this.audioSource = audioSource;
        this.imageSource = imageSource;
        this.schedule = schedule;
    }
}
