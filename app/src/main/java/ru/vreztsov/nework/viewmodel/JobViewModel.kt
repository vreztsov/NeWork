package ru.vreztsov.nework.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vreztsov.nework.auth.AppAuth
import ru.vreztsov.nework.dto.Job
import ru.vreztsov.nework.repository.Repository
import ru.vreztsov.nework.util.DataViewTransform
import java.util.Date
import javax.inject.Inject

private val empty = Job(
    id = 0L,
    name = "",
    position = "",
    start = "0"
)

@HiltViewModel
class JobViewModel @Inject constructor(
    application: Application,
    private val repository: Repository,
    private val appAuth: AppAuth
) : AndroidViewModel(application) {

    val data: Flow<List<Job>>
        get() = repository.dataJobs
    //_data
//            flow {
////            while (true) {
//                loadAllJobs()
//                emit(_data)
////                delay(1_000)
////            }
//        }

    private var _data: MutableLiveData<List<Job>> = MutableLiveData(emptyList())
    private val edited = MutableLiveData(empty)
    var startDate: Long? = null // TODO пересмотри формат даты. Точность нужна до дня, не более того. Одинаковые даты (без учёта времени) должны быть равны.
    var finishDate: Long? = null

    init {
        loadMyJobs()
    }

    fun loadMyJobs() = viewModelScope.launch {
        try {
            repository.getMyJobsAsync(appAuth.authStateFlow.value.id)
        } catch (e: Exception) {
            Log.e("JobViewModel", "Failed to get my jobs list")
        }
        repository.dataJobs.collectLatest { jobList ->
            _data.value = jobList.filter { job ->
                job.ownerId == appAuth.authStateFlow.value.id
            }
        }
    }

    fun loadUserJobs(id: Long) = viewModelScope.launch {
        try {
            repository.getJobsAsync(id)
        } catch (e: Exception) {
            Log.e("JobViewModel", "Failed to get job list of user with id = $id")
        }
        repository.dataJobs.collectLatest { jobList ->
            _data.value = jobList.filter { job ->
                job.ownerId == id
            }
        }
    }

    fun edit(job: Job) {
        edited.value = job
        startDate = DataViewTransform.parseDateFromNeWorkString(job.start)?.time
        finishDate = job.finish?.let { DataViewTransform.parseDateFromNeWorkString(it)?.time } ?: startDate
    }

    fun changeContent(
        name: String,
        position: String,
        startDate: Long,
        finishDate: Long,
        link: String?
    ) {
        if (edited.value?.name == name
            && edited.value?.position == position
            && this.startDate == startDate
            && this.finishDate == finishDate
            && edited.value?.link == link
        ) return
        edited.value = edited.value?.copy(
            name = name,
            position = position,
            start = DataViewTransform.parseDateToNeWorkString(Date(startDate)),
            finish = if (startDate == finishDate) null else DataViewTransform.parseDateToNeWorkString(Date(finishDate)),
            link = link
        )
    }

    fun save() {
        viewModelScope.launch {
            try {
                edited.value?.let {
                    repository.saveMyJob(it)
                }
            } catch (e: Exception) {
                Log.e("JobViewModel", e.message ?: "Failed to save job")
            }
        }
        edited.value = empty
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
           repository.removeMyJobById(id)
        } catch (e: Exception) {
            Log.e("JobViewModel", e.message ?: "Failed to remove job with id = $id")
        }
    }


}