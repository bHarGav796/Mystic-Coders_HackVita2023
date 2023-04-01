package com.example.loginpage.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.loginpage.data.ClassData
import com.example.loginpage.databinding.ClassItemBinding
import com.example.loginpage.databinding.RvMembersBinding
import java.text.SimpleDateFormat
import java.util.*


class ClassAdapter2()  : RecyclerView.Adapter<ClassAdapter2.JoinedMemberViewHolder>()  {

    class JoinedMemberViewHolder(val binding : ClassItemBinding) : RecyclerView.ViewHolder(binding.root)

    private var onItemClickListener: ((ClassData) -> Unit)? = null

    fun setOnItemClickListener(position: (ClassData) -> Unit) {
        onItemClickListener = position
    }


    private val diffCallback = object : DiffUtil.ItemCallback<ClassData>() {

        override fun areContentsTheSame(oldItem: ClassData, newItem: ClassData): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: ClassData, newItem: ClassData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var membersList : List<ClassData>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinedMemberViewHolder {
        val binding = ClassItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return  JoinedMemberViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return membersList.size
    }



    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: JoinedMemberViewHolder, position: Int) {
        val data = membersList[position]
        holder.itemView.apply {

            with(holder) {

                binding.classTv.text = data.className
                binding.subjectTv.text = data.subjectName
                binding.UniqueTv.text = data.uniqueName



            }

            setOnClickListener {
                onItemClickListener?.let {
                        click ->
                    click(data)
                }
            }
        }
    }

}