package dipan.ProjectManagement.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dipan.ProjectManagement.R
import dipan.ProjectManagement.activities.MainActivity

class MyFirebaseMessagingService: FirebaseMessagingService(){
    //data messages and alert messages

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        //where the message is from
        Log.d("FCM", "From: ${remoteMessage.from}")

        //check the notification body
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
        }

        //if the message has notification
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
    }

    //this is called when the instance id token is updated
    //this is called when the app is installed or uninstalled
    //or security of previous token is compromised
    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }
    //on new token generation -> send registration to the server
    private fun sendRegistrationToServer(token: String) {
        //send the token to the server
    }

    //send the notification to the device
    private fun sendNotification(messageBody: String) {
        //if we click on the notification, we will be taken to the main activity
        val intent = Intent(this, MainActivity::class.java)

        //put this activity on top of the stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //we can be in another application
        //we can't just directly pass the intent in some other app to our app
        //read about pending intent
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )

        //create a channel
        val channelId=this.getString(R.string.default_notification_channel_id)
        //notification sound
        val defaultSoundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION)

        //create the actual notification
        val notificationBuilder= NotificationCompat
            .Builder(this,channelId)
            .setSmallIcon(R.drawable.ic_notification_24dp)
            .setContentTitle("Project Manager Notification")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //for greater than oreo it is different
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,
                                                "Channel Project Manager title",
                                                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        //send the notification
        notificationManager.notify(0,notificationBuilder.build())




    }
}