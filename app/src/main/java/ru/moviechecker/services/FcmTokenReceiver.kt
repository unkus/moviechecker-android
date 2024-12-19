package ru.moviechecker.services

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging

class FcmTokenReceiver(appConext: Context, workerParams: WorkerParameters): Worker(appConext, workerParams) {

    override fun doWork(): Result {
        Log.d(TAG, "Receiving FCM Registration token...")
        Firebase.messaging.getToken()
            .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM Registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "FCM Registration token: $token"
            Log.d(TAG, msg)
        }
        return Result.success()
    }

    companion object {
        private const val TAG = "FcmTokenUpdater"
    }
}