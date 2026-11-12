package com.example.chatapp.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;


public class MessagingService extends FirebaseMessagingService {

    /**
     * runs when a new token is created and logs the token
     *
     * @param token The token used for sending messages to this application instance. This token is
     *     the same as the one retrieved by {@link FirebaseMessaging#getToken()}.
     */
    @Override
    public void onNewToken(@NonNull String token){
        super.onNewToken(token);
        Log.d("FCM", "TOKEN: "+token);
    }

    /**
     * logs the message that was received
     *
     * @param message Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message){
         super.onMessageReceived(message);
         Log.d("FCM", "460 MESSAGE: "+ Objects.requireNonNull(message.getNotification()).toString());
     }

}
