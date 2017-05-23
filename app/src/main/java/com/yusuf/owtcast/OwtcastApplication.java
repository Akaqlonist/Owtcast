package com.yusuf.owtcast;

import android.app.Application;
import android.content.Context;

import com.yusuf.owtcast.Model.AdInfo;
import com.yusuf.owtcast.Model.UserInfo;

import java.util.List;

/**
 * Created by DragonWarrior on 1/30/2017.
 */

public class OwtcastApplication extends Application {

    private static OwtcastApplication instance = new OwtcastApplication();
    private static Context appContext;

    public static OwtcastApplication getInstance() { return instance;}
    public static Context getContext() { return appContext;}

    private UserInfo userInfo;
    private List<AdInfo> adSchedule;

    public void setUserInfo(UserInfo info)
    {
        userInfo = info;
    }

    public UserInfo getUserInfo()
    {
        return userInfo;
    }

    public void setAdSchedule(List<AdInfo> list)
    {
        adSchedule = list;
    }

    public List<AdInfo> getAdSchedule()
    {
        return adSchedule;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        appContext = this.getApplicationContext();

    }
}
