package com.example.dacs3.ui.view

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dacs3.ui.view.PlayerActivity.Companion.ACTION_CLOSE
import com.example.dacs3.ui.view.PlayerActivity.Companion.NOTIFICATION_ID

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_CLOSE -> {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(NOTIFICATION_ID)

                val actionIntent = Intent(context, PlayerActivity::class.java).apply {
                    action = intent.action
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("EXIT", true)
                }
                context.startActivity(actionIntent)
            }
        }
    }
}