package ru.vreztsov.nework.util.listener

import ru.vreztsov.nework.dto.Job

interface JobOnInteractionListener {
    fun onJobClick(job: Job)
    fun onJobDelete(jobId: Long)
}