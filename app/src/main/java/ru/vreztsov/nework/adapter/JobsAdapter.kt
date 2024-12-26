package ru.vreztsov.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.vreztsov.nework.databinding.CardJobBinding
import ru.vreztsov.nework.dto.Job
import ru.vreztsov.nework.util.DataViewTransform

class JobsAdapter : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return JobViewHolder(
            CardJobBinding.inflate(layoutInflater, parent, false),
        )
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        getItem(position)?.let { job ->
            holder.bind(job)
        }
    }
}

class JobViewHolder(
    private val binding: CardJobBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        with(binding) {
            company.text = job.name
            val start = DataViewTransform.jobDataToTextView(job.start)
            val finish = job.finish?.let { DataViewTransform.jobDataToTextView(it) } ?: "НВ"
            period.text = String.format("%s - %s", start, finish)
            position.text = job.position
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
