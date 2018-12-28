package com.innov8.memeit.Fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import androidx.work.WorkManager

class MemeTemplateViewModel : ViewModel() {
    val workinfos: LiveData<MutableList<WorkInfo>> by lazy {
        WorkManager.getInstance().getWorkInfosByTagLiveData("template download")
    }

}