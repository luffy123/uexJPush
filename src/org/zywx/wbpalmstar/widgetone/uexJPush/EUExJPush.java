package org.zywx.wbpalmstar.widgetone.uexJPush;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BDebug;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.data.JPushLocalNotification;

public class EUExJPush extends EUExBase implements CallBack {

    private static final String BUNDLE_DATA = "data";
    private static final int MSG_INIT = 1;
    private static final int MSG_STOPPUSH = 2;
    private static final int MSG_RESUMEPUSH = 3;
    private static final int MSG_ISPUSHSTOPPED = 4;
    private static final int MSG_SETALIASANDTAGS = 5;
    private static final int MSG_SETALIAS = 6;
    private static final int MSG_SETTAGS = 7;
    private static final int MSG_GETREGISTRATIONID = 8;
    private static final int MSG_REPORTNOTIFICATIONOPENED = 9;
    private static final int MSG_CLEARALLNOTIFICATIONS = 10;
    private static final int MSG_CLEARNOTIFICATIONBYID = 11;
    private static final int MSG_SETPUSHTIME = 12;
    private static final int MSG_SETSILENCETIME = 13;
    private static final int MSG_SETLATESTNOTIFICATIONNUMBER = 14;
    private static final int MSG_GETCONNECTIONSTATE = 15;
    private static final int MSG_ADDLOCALNOTIFICATION = 16;
    private static final int MSG_REMOVELOCALNOTIFICATION = 17;
    private static final int MSG_CLEARLOCALNOTIFICATIONS = 18;

    NotificationManager notificationManager;

    public EUExJPush(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
    }

    @Override
    protected boolean clean() {
        return false;
    }

    public void init(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_INIT;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void initMsg(String[] params) {
        String json = params[0];
        String debug=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            debug=jsonObject.optString("debug");
        } catch (JSONException e) {
        }
        if ("1".equals(debug)){
            JPushInterface.setDebugMode(true);
        }
        notificationManager= (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        JPushInterface.init(mContext.getApplicationContext());
        MyReceiver.setCallBack(this);
    }

    public void stopPush(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_STOPPUSH;
        mHandler.sendMessage(msg);
    }

    private void stopPushMsg(String[] params) {
        JPushInterface.stopPush(mContext.getApplicationContext());
    }

    public void resumePush(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_RESUMEPUSH;
        mHandler.sendMessage(msg);
    }

    private void resumePushMsg(String[] params) {
        JPushInterface.resumePush(mContext.getApplicationContext());
    }

    public void isPushStopped(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_ISPUSHSTOPPED;
        mHandler.sendMessage(msg);
    }

    private void isPushStoppedMsg(String[] params) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("result",JPushInterface.isPushStopped(mContext.getApplicationContext())?0:1);
            boolean result=JPushInterface.isPushStopped(mContext.getApplicationContext());
            String data =jsonObject.toString();
            String js = SCRIPT_HEADER + "if(" + JsConst.CALLBACK_ISPUSHSTOPPED + "){"
                    + JsConst.CALLBACK_ISPUSHSTOPPED + "('" + data + "');}";
            onCallback(js);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setAliasAndTags(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETALIASANDTAGS;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setAliasAndTagsMsg(String[] params) {
        String json = params[0];
        Set<String> tags=null;
        String alias=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            alias=jsonObject.getString("alias");
            JSONArray tagsArray=jsonObject.getJSONArray("tags");
            if (tagsArray!=null) {
                tags=new HashSet<String>();
                for (int i = 0; i < tagsArray.length(); i++) {
                    tags.add(tagsArray.getString(i));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JPushInterface.setAliasAndTags(mContext.getApplicationContext(), alias, JPushInterface.filterValidTags(tags), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("result", i);
                    jsonObject.put("tags", s);
                    jsonObject.put("alias", set);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data = jsonObject.toString();
                String js = SCRIPT_HEADER + "if(" + JsConst.CALLBACK_SETALIASANDTAGS + "){"
                        + JsConst.CALLBACK_SETALIASANDTAGS + "('" + data + "');}";
                onCallback(js);
            }
        });

    }

    public void setAlias(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETALIAS;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setAliasMsg(String[] params) {
        String json = params[0];
        String alias=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            alias=jsonObject.getString("alias");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JPushInterface.setAlias(mContext.getApplicationContext(), alias, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("result", i);
                    jsonObject.put("tags", s);
                    jsonObject.put("alias", set);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data = jsonObject.toString();
                String js = SCRIPT_HEADER + "if(" + JsConst.CALLBACK_SETALIAS + "){"
                        + JsConst.CALLBACK_SETALIAS + "('" + data + "');}";
                onCallback(js);

            }
        });
     }

    public void setTags(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETTAGS;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setTagsMsg(String[] params) {
        String json = params[0];
        Set<String> tags=new HashSet<String>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray=jsonObject.getJSONArray("tags");
            if (jsonArray!=null){
                for (int i=0;i<jsonArray.length();i++){
                    tags.add(jsonArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JPushInterface.setTags(mContext.getApplicationContext(), JPushInterface.filterValidTags(tags), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("result", i);
                    jsonObject.put("tags", s);
                    jsonObject.put("alias", set);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String data = jsonObject.toString();
                String js = SCRIPT_HEADER + "if(" + JsConst.CALLBACK_SETTAGS + "){"
                        + JsConst.CALLBACK_SETTAGS + "('" + data + "');}";
                onCallback(js);
            }
        });


    }

    public void getRegistrationID(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_GETREGISTRATIONID;
        mHandler.sendMessage(msg);
    }

    private void getRegistrationIDMsg(String[] params) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("registrationID",JPushInterface.getRegistrationID(mContext.getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = jsonObject.toString();
        String js = SCRIPT_HEADER + "if(" + JsConst.CALLBACK_GETREGISTRATIONID + "){"
                + JsConst.CALLBACK_GETREGISTRATIONID + "('" + data + "');}";
        onCallback(js);
    }

    public void reportNotificationOpened(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_REPORTNOTIFICATIONOPENED;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void reportNotificationOpenedMsg(String[] params) {
        String json = params[0];
        String msgId=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            msgId=jsonObject.optString("msgId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JPushInterface.reportNotificationOpened(mContext.getApplicationContext(), msgId);
    }

    public void clearAllNotifications(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLEARALLNOTIFICATIONS;
        mHandler.sendMessage(msg);
    }

    private void clearAllNotificationsMsg(String[] params) {
       JPushInterface.clearAllNotifications(mContext.getApplicationContext());
    }

    public void clearNotificationById(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLEARNOTIFICATIONBYID;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void clearNotificationByIdMsg(String[] params) {
        String json = params[0];
        int id=0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            id=jsonObject.getInt("notificationId");
            JPushInterface.clearNotificationById(mContext.getApplicationContext(),id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setPushTime(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETPUSHTIME;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setPushTimeMsg(String[] params) {
        String json = params[0];
        Set<Integer> weekDays=new HashSet<Integer>();
        int startHour=0;
        int endHour=0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray=jsonObject.getJSONArray("weekDays");
            if (jsonArray!=null){
                for (int i=0;i<jsonArray.length();i++){
                    weekDays.add(jsonArray.getInt(i));
                }
            }
            startHour=jsonObject.getInt("startHour");
            endHour=jsonObject.getInt("endHour");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JPushInterface.setPushTime(mContext.getApplicationContext(), weekDays, startHour, endHour);
    }

    public void setSilenceTime(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETSILENCETIME;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setSilenceTimeMsg(String[] params) {
        String json = params[0];
        int startHour;
        int startMinute;
        int endHour;
        int endMinute;
        try {
            JSONObject jsonObject = new JSONObject(json);
            startHour=jsonObject.getInt("startHour");
            startMinute=jsonObject.getInt("startMinute");
            endHour=jsonObject.getInt("endHour");
            endMinute=jsonObject.getInt("endMinute");
            JPushInterface.setSilenceTime(mContext.getApplicationContext(),startHour,startMinute,endHour,endMinute);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLatestNotificationNumber(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SETLATESTNOTIFICATIONNUMBER;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setLatestNotificationNumberMsg(String[] params) {
        String json = params[0];
        int maxNum;
        try {
            JSONObject jsonObject = new JSONObject(json);
            maxNum=jsonObject.getInt("maxNum");
            JPushInterface.setLatestNotificationNumber(mContext.getApplicationContext(),maxNum);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getConnectionState(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_GETCONNECTIONSTATE;
        mHandler.sendMessage(msg);
    }

    private void getConnectionStateMsg(String[] params) {
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("result",JPushInterface.getConnectionState(mContext.getApplicationContext())?0:1);
            String data =jsonObject.toString();
            String js = SCRIPT_HEADER + "if(" + JsConst.CALLBACK_GETCONNECTIONSTATE + "){"
                    + JsConst.CALLBACK_GETCONNECTIONSTATE + "('" + data + "');}";
            onCallback(js);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addLocalNotification(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_ADDLOCALNOTIFICATION;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void addLocalNotificationMsg(String[] params) {
        String json = params[0];
        long builderId;
        String title;
        String content;
        String extras;
        long notificationId;
        long broadCastTime;
        System.currentTimeMillis();
        try {
            JSONObject jsonObject = new JSONObject(json);
            builderId=jsonObject.getLong("builderId");
            title=jsonObject.optString("title");
            content=jsonObject.optString("content");
            extras=jsonObject.optString("extras");
            notificationId=jsonObject.getLong("notificationId");
            broadCastTime=jsonObject.getLong("broadCastTime");
            JPushLocalNotification ln=new JPushLocalNotification();
            ln.setBuilderId(builderId);
            ln.setContent(content);
            ln.setTitle(title);
            ln.setNotificationId(notificationId);
            long atTime=System.currentTimeMillis()+broadCastTime;
            Log.i("ylt",atTime+"");
            ln.setBroadcastTime(atTime);
            ln.setExtras(extras);
            JPushInterface.addLocalNotification(mContext.getApplicationContext(),ln);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeLocalNotification(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_REMOVELOCALNOTIFICATION;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void removeLocalNotificationMsg(String[] params) {
        String json = params[0];
        long notificationId;
        try {
            JSONObject jsonObject = new JSONObject(json);
            notificationId=jsonObject.getLong("notificationId");
            JPushInterface.removeLocalNotification(mContext.getApplicationContext(),notificationId);
            if (notificationManager!=null){
                notificationManager.cancel((int) notificationId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void clearLocalNotifications(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_CLEARLOCALNOTIFICATIONS;
        mHandler.sendMessage(msg);
    }

    private void clearLocalNotificationsMsg(String[] params) {
       JPushInterface.clearLocalNotifications(mContext.getApplicationContext());
        if (notificationManager!=null){
            notificationManager.cancelAll();
        }
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {
            case MSG_INIT:
                initMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_STOPPUSH:
                stopPushMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_RESUMEPUSH:
                resumePushMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_ISPUSHSTOPPED:
                isPushStoppedMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETALIASANDTAGS:
                setAliasAndTagsMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETALIAS:
                setAliasMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETTAGS:
                setTagsMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_GETREGISTRATIONID:
                getRegistrationIDMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_REPORTNOTIFICATIONOPENED:
                reportNotificationOpenedMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLEARALLNOTIFICATIONS:
                clearAllNotificationsMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLEARNOTIFICATIONBYID:
                clearNotificationByIdMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETPUSHTIME:
                setPushTimeMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETSILENCETIME:
                setSilenceTimeMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_SETLATESTNOTIFICATIONNUMBER:
                setLatestNotificationNumberMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_GETCONNECTIONSTATE:
                getConnectionStateMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_ADDLOCALNOTIFICATION:
                addLocalNotificationMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_REMOVELOCALNOTIFICATION:
                removeLocalNotificationMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            case MSG_CLEARLOCALNOTIFICATIONS:
                clearLocalNotificationsMsg(bundle.getStringArray(BUNDLE_DATA));
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    @Override
    public void onReceiveRegistration(String jsonData) {
        String js = SCRIPT_HEADER + "if(" + JsConst.ONRECEIVEREGISTRATION + "){"
                + JsConst.ONRECEIVEREGISTRATION + "('" + jsonData + "');}";
        onCallback(js);
    }

    @Override
    public void onReceiveMessage(String jsonData) {
        String js = SCRIPT_HEADER + "if(" + JsConst.ONRECEIVEMESSAGE + "){"
                + JsConst.ONRECEIVEMESSAGE + "('" + jsonData + "');}";
        onCallback(js);
    }

    @Override
    public void onReceiveNotification(String jsonData) {
        String js = SCRIPT_HEADER + "if(" + JsConst.ONRECEIVENOTIFICATION + "){"
                + JsConst.ONRECEIVENOTIFICATION + "('" + jsonData + "');}";
        onCallback(js);
    }

    @Override
    public void onReceiveNotificationOpen(String jsonData) {
        String js = SCRIPT_HEADER + "if(" + JsConst.ONRECEIVENOTIFICATIONOPEN + "){"
                + JsConst.ONRECEIVENOTIFICATIONOPEN + "('" + jsonData + "');}";
        onCallback(js);
    }

    @Override
    public void onReceiveConnectionChange(String jsonData) {
        String js = SCRIPT_HEADER + "if(" + JsConst.ONRECEIVECONNECTIONCHANGE + "){"
                + JsConst.ONRECEIVECONNECTIONCHANGE + "('" + jsonData + "');}";
        onCallback(js);
    }
}