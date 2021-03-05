package com.mobiquel.udhampur.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobiquel.udhampur.R;
import com.mobiquel.udhampur.ui.home.HomeActivity;
import com.mobiquel.udhampur.ui.settings.FirstAidActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Vibhor Gupta on 2/19/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private PendingIntent contentIntent = null;
    private TextToSpeech tts;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());
        String title = null;
        String message = null;
        int requestID = (int) System.currentTimeMillis();
        mNotificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            if(object.getString("key").equals("first_aid")){
                Intent intent = new Intent(getApplicationContext(), FirstAidActivity.class);
                intent.putExtra("POS", "1");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                contentIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            }
            else{
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.putExtra("POS", "1");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                contentIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            title = object.getString("title");
            message = object.getString("body");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (message != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                int notifyID = 1;
                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

                notificationBuilder.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent)
                        .setTicker("Notifyy Notification")
                        .setPriority(Notification.PRIORITY_MAX) // this is deprecated in API 26 but you can still use for below 26. check below update for 26 API
                        .setContentTitle(title)
                        .setContentText(message);
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                mChannel.setSound(defaultSoundUri, att);

                mNotificationManager.createNotificationChannel(mChannel);

                mNotificationManager.notify(notifyID, notificationBuilder.build());
            } else {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle(title).setContentText(message);
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                mBuilder.setSound(defaultSoundUri);

                mBuilder.setSmallIcon(R.mipmap.ic_launcher);

                mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                mBuilder.setContentIntent(contentIntent);
                mBuilder.setAutoCancel(true);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }


        }


    }
}