package com.huawei.mdm.sample;


import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.huawei.android.app.admin.DeviceRestrictionManager;
import com.huawei.mdm.sample.pojo.MdmConstant;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JCommonService;
import cn.jpush.android.service.JPushMessageService;

//https://docs.jiguang.cn/jpush/client/Android/android_api#%E6%94%B6%E5%88%B0%E9%80%9A%E7%9F%A5%E5%9B%9E%E8%B0%83
public class JPushMsgService extends JPushMessageService {
    //    @Override
//    public void onMessage(Context context, CustomMessage customMessage) {
//        super.onMessage(context, customMessage);
//        Log.i("极光","接收消息");
//        Log.i("极光", JSONUtil.toJsonStr(customMessage));
//    }
    private DeviceRestrictionManager mDeviceRestrictionManager = null;
    private ComponentName mAdminName = null;

//    @Override
//    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
//        super.onTagOperatorResult(context, jPushMessage);
//        Log.i("极光", "接收消息");
//        Log.i("极光", JSONUtil.toJsonStr(jPushMessage));
//    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
        Log.i("极光", "收到通知回调");
        //{"inAppClickAction":"","notificationSmallIcon":"","sspWxAppId":"","inAppMsgTitle":"","notificationTitle":"MDM提醒","inAppMsgShowPos":0,"notificationBigPicPath":"","notificationChannelId":"","notificationPriority":0,"msgId":"18101272296670672","notificationBigText":"","notificationType":0,"notificationContent":"逾期了222","notificationInbox":"","notificationLargeIcon":"","platform":0,"notificationNormalSmallIcon":"","targetPkgName":"","appId":"com.huawei.mdm.sample","notificationId":517210996,"notificationImportance":-1,"sspWmOriginId":"","failedAction":0,"isRichPush":false,"notificationAlertType":7,"sspWmType":0,"notificationStyle":0,"deeplink":"","developerArg0":"","notificationCategory":"","inAppMsgContentBody":"","notificationBuilderId":0,"inAppType":0,"notificationExtras":"{}","_webPagePath":"","inAppMsgShowType":2,"inAppMsgType":1,"richType":0,"showResourceList":[],"inAppShowTarget":"","displayForeground":"","inAppExtras":"","isWmDeepLink":false,"appkey":"e733f3d1f5449c9441497591","failedLink":""}
        Log.i("极光", JSONUtil.toJsonStr(notificationMessage));
        String msg = notificationMessage.notificationTitle;
        Log.i("极光", msg);
        if("禁用WIFI".equals(msg)){
            mDeviceRestrictionManager = new DeviceRestrictionManager();
            mAdminName = new ComponentName(this, SampleDeviceReceiver.class);
            mDeviceRestrictionManager.setWifiDisabled(mAdminName, true);
        }else{
            mDeviceRestrictionManager = new DeviceRestrictionManager();
            mAdminName = new ComponentName(this, SampleDeviceReceiver.class);
            mDeviceRestrictionManager.setWifiDisabled(mAdminName, false);
        }

    }

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
        String registrationID = JPushInterface.getRegistrationID(context);
        Log.i(MdmConstant.JIGUANG, "极光注册ID=" + registrationID);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("OPT","极光注册");
        jsonObject.set("SN","CPDD111");
        jsonObject.set("REGID",registrationID);

        String result = HttpRequest.post(MdmConstant.WEBHOOK_URL).body(JSONUtil.toJsonStr(jsonObject)).execute().body();
    }
    //    @Override
//    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
//        super.onNotifyMessageOpened(context, notificationMessage);
//        Log.i("极光", "点击通知回调");
//        //2024-02-20 14:13:47.391  6854-6854  极光                      com.huawei.mdm.sample                I  {"inAppClickAction":"","notificationSmallIcon":"","sspWxAppId":"","inAppMsgTitle":"","notificationTitle":"MDM提醒","inAppMsgShowPos":0,"notificationBigPicPath":"","notificationChannelId":"","notificationPriority":0,"msgId":"18101272296670672","notificationBigText":"","notificationType":0,"notificationContent":"逾期了222","notificationInbox":"","notificationLargeIcon":"","platform":0,"notificationNormalSmallIcon":"","targetPkgName":"","appId":"com.huawei.mdm.sample","notificationId":517210996,"notificationImportance":-1,"sspWmOriginId":"","failedAction":0,"isRichPush":false,"notificationAlertType":7,"sspWmType":0,"notificationStyle":0,"deeplink":"","developerArg0":"","notificationCategory":"","inAppMsgContentBody":"","notificationBuilderId":0,"inAppType":0,"notificationExtras":"{}","_webPagePath":"","inAppMsgShowType":2,"inAppMsgType":1,"richType":0,"showResourceList":[],"inAppShowTarget":"","displayForeground":"","inAppExtras":"","isWmDeepLink":false,"appkey":"e733f3d1f5449c9441497591","failedLink":""}
//        Log.i("极光", JSONUtil.toJsonStr(notificationMessage));
//    }
}
