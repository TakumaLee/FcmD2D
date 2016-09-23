package com.kuma.sample.fcmd2d.manager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.kuma.sample.fcmd2d.BuildConfig;
import com.kuma.sample.fcmd2d.entity.push.PushDevice;
import com.kuma.sample.fcmd2d.entity.push.PushUser;
import com.kuma.sample.fcmd2d.http.NetworkCallback;
import com.kuma.sample.fcmd2d.http.OkHttpClientConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by TakumaLee on 2016/7/31.
 */
public class FirebaseManager {
    private static final String TAG = FirebaseManager.class.getSimpleName();

    private static final long CACHE_EXPIRATION = 0;

    public class KeyData {
        public static final String TO = "to";
        public static final String DATA = "data";
        public static final String TO_USER_ID = "toUserId";
        public static final String FROM_USER_ID = "fromUserId";
        public static final String FROM_USER_NAME = "fromUserName";
        public static final String FROM_USER_HEAD_PIC = "fromUserHeadPic";
        public static final String MESSAGE = "message";
        public static final String PRIORITY = "priority";

        // for iOS device
        public static final String CONTENT_AVAILABLE = "content_available";
    }

    public class Priority {
        public static final String NORMAL = "normal";
        public static final String HIGH = "high";
    }

    public class KeyNotification {
        public static final String NOTIFICATION = "notification";
        public static final String NOTIFICATION_BODY = "body";
        public static final String NOTIFICATION_TITLE = "title";
//        public static final String NOTIFICATION_ICON = "icon";
        public static final String NOTIFICATION_SOUND = "sound";
        public static final String NOTIFICATION_BADGE = "badge";
    }

    private DatabaseReference databaseRefUsers;

    public FirebaseManager() {
        databaseRefUsers = FirebaseDatabase.getInstance().getReference();
    }

    private static class SingletonHolder {
        private static FirebaseManager INSTANCE = new FirebaseManager();
    }

    public static final FirebaseManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void updateUserPushData(String id) {
        Log.v(TAG, "update user push data.");
        postUserPushData(id);
    }

    private void postUserPushData(final String id) {
        final PushDevice device = new PushDevice(FcmAccountManager.getInstance().getPushToken());
        databaseRefUsers.child(PushUser.DATABASE_USERS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PushUser pushUser = dataSnapshot.getValue(PushUser.class);
                if (pushUser == null) {
                    pushUser = new PushUser();
                }
                for (PushDevice pushDevice : pushUser.getDeviceList()) {
                    if (pushDevice.getToken().equals(device.getToken())) {
                        return;
                    }
                    if (pushDevice.getDevice() != null && pushDevice.getDevice().equals("android")) {
                        return;
                    }
                }
                pushUser.addDeivce(device);
                databaseRefUsers.child(PushUser.DATABASE_USERS).child(id).setValue(pushUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, databaseError.getDetails());
            }
        });
    }

    @Deprecated
    public void sendUserPush(final String accountId, final String message) {
        databaseRefUsers.child(PushUser.DATABASE_USERS).child(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final PushUser pushUser = dataSnapshot.getValue(PushUser.class);
                if (pushUser == null || pushUser.getDeviceList() == null || pushUser.getDeviceList().size() == 0) {
                    return;
                }
                final FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build();
                final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                firebaseRemoteConfig.setConfigSettings(configSettings);

                long cacheExpiration = CACHE_EXPIRATION;
                firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Fetch succeeded.");
                            firebaseRemoteConfig.activateFetched();
                            if (pushUser.getDeviceList() != null) {
                                for (PushDevice device : pushUser.getDeviceList()) {
                                    if (device.getDevice() == null) {
                                        continue;
                                    }
                                    boolean isIOS = !device.getDevice().equals("android");
                                    sendPush(firebaseRemoteConfig, message, accountId, device.getToken(), isIOS);
                                }
                            }
                        } else {
                            Log.d(TAG, "Fetch Failed." + task.getException().getMessage());
                        }
                    }
                });
                firebaseRemoteConfig.fetch(cacheExpiration).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure push request: " + e.getMessage());
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v(TAG, databaseError.getDetails());
            }
        });
    }

    @Deprecated
    private void sendPush(FirebaseRemoteConfig firebaseRemoteConfig, String message, String accountId, String regId, boolean isIOS) {
        String url = firebaseRemoteConfig.getString("server_push_url");
        String key = firebaseRemoteConfig.getString("server_push_key");
        Log.v(TAG, url + "\n" + key);
        JSONObject object = new JSONObject();
        JSONObject dataObj = new JSONObject();
        JSONObject notificationObj = new JSONObject();
        try {
            dataObj.put(KeyData.MESSAGE, message);
            dataObj.put(KeyData.FROM_USER_HEAD_PIC, "https://www.facebook.com/photo.php?fbid=998328863519856&l=bc2da0ae10");
            dataObj.put(KeyData.FROM_USER_NAME, "Takuma");
            dataObj.put(KeyData.FROM_USER_ID, "456");
            dataObj.put(KeyData.TO_USER_ID, accountId);

            // for iOS device
            dataObj.put(KeyData.CONTENT_AVAILABLE, true);

            object.put(KeyData.TO, regId);
            object.put(KeyData.PRIORITY, Priority.HIGH);
            object.put(KeyData.DATA, dataObj);

            if (isIOS) {
                notificationObj.put(KeyNotification.NOTIFICATION_BODY, message);
                notificationObj.put(KeyNotification.NOTIFICATION_TITLE, "Takuma");
                notificationObj.put(KeyNotification.NOTIFICATION_SOUND, "default");
                notificationObj.put(KeyNotification.NOTIFICATION_BADGE, "1");
//            notificationObj.put(KeyNotification.NOTIFICATION_ICON, "url");
                object.put(KeyNotification.NOTIFICATION, notificationObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v(TAG, object.toString());
        if ((url == null || url.isEmpty()) && (key == null || key.isEmpty())) {
            return;
        }
        OkHttpClientConnect.excuteAutoPost("Authorization", key, url, object.toString(), OkHttpClientConnect.CONTENT_TYPE_JSON, new NetworkCallback() {
            @Override
            public void onFailure(IOException e) {
                Log.v(TAG, "onFailure: " + e);
            }

            @Override
            public void onResponse(int responseCode, String result) {
                Log.v(TAG, result);
            }
        });
    }

}
