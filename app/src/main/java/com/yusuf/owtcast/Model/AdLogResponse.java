package com.yusuf.owtcast.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by DragonWarrior on 1/30/2017.
 */

public class AdLogResponse extends OwtResponse {

    @SerializedName("Play")
    @Expose
    String play;

    AdLogResponse(boolean error, String message, String play)
    {
        this.error = error;
        this.message = message;
        this.play = play;
    }
}
