package com.example.newsuserapp.worker

import android.content.Context
import com.example.newsuserapp.repository.NewsRepository
import javax.inject.Inject

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters



class RefreshNewsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @Inject
    lateinit var repository: NewsRepository

    init {
        // Inject dependencies manually (since Hilt doesn't support Worker constructor injection directly)
//        (appContext.applicationContext as MyApplication).appComponent.inject(this)
    }

    override suspend fun doWork(): Result {
        return try {
            repository.refreshNews()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
