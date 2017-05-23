package com.yusuf.owtcast.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DragonWarrior on 1/30/2017.
 */

public class OwtResponse {

    @SerializedName("error")
    @Expose
    public boolean error;

    @SerializedName("message")
    @Expose
    public String message;

    public OwtResponse(boolean error, String message)
    {
        this.error = error;
        this.message = message;
    }

    public OwtResponse()
    {

    }
}
