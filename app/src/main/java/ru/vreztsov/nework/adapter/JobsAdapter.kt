package ru.vreztsov.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.vreztsov.nework.databinding.CardJobBinding
import ru.vreztsov.nework.dto.Job
import ru.vreztsov.nework.util.DataViewTransform
import ru.vreztsov.nework.util.listener.JobOnInteractionListener

class JobsAdapter(
    private val isOwn: Boolean,
    private val listener: JobOnInteractionListener
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return JobViewHolder(
            isOwn,
            CardJobBinding.inflate(layoutInflater, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let { job ->
            holder.bind(job)
        }
    }
}

class JobViewHolder(
    private val isOwn: Boolean,
    private val binding: CardJobBinding,
    private val listener: JobOnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        with(binding) {
            company.text = job.name
            period.text = DataViewTransform.periodToString(job)
            position.text = job.position
            removeJob.isVisible = isOwn
            if (isOwn) {
                jobCard.setOnClickListener {
                    listener.onJobClick(job)
                }
                removeJob.setOnClickListener {
                    listener.onJobDelete(job.id)
                }
            }
        }
    }
}

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        if (oldItem::class != newItem::class) return false
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}
