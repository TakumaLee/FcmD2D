package com.kuma.sample.fcmd2d.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kuma.sample.fcmd2d.MainActivity;
import com.kuma.sample.fcmd2d.R;
import com.kuma.sample.fcmd2d.context.ApplicationContextSingleton;
import com.kuma.sample.fcmd2d.manager.FcmNotificationManager;
import com.kuma.sample.fcmd2d.manager.FirebaseManager;

import java.util.Map;

/**
 * Created by TakumaLee on 2016/5/24.
 */
public class FcmListenerService extends FirebaseMessagingService {
    private static final String TAG = FcmListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.v(TAG, remoteMessage.getData().toString());
            Map<String, String> map = remoteMessage.getData();
            final String nickName = map.get(FirebaseManager.KeyData.FROM_USER_NAME);
            final String message = map.get(FirebaseManager.KeyData.MESSAGE);
            String id = map.get(FirebaseManager.KeyData.FROM_USER_ID);
            final String userPhoto = map.get(FirebaseManager.KeyData.FROM_USER_HEAD_PIC);
//            if (id != null && Integer.valueOf(id) != FcmAccountManager.getInstance().getMyAccount().getAccountId()) {
                Log.v(TAG, "FCM Received, and now will generate notification!");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(ApplicationContextSingleton.getApplicationContext())
                                .load(userPhoto)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        Log.v(TAG, "FCM Received, and now Loaded Image!");
                                        FcmNotificationManager.getInstance().generateNotification(ApplicationContextSingleton.getApplicationContext(), resource, nickName, message);
                                    }

                                    @Override
                                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                        super.onLoadFailed(e, errorDrawable);
                                        FcmNotificationManager.getInstance().generateNotification(ApplicationContextSingleton.getApplicationContext(), nickName, message);
                                    }
                                });
                    }
                });

//            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.v(TAG, "Remote Notification: " + remoteMessage.getNotification().getBody());
        }
//        MLog.v(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.v(TAG, s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
