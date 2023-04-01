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
import com.example.loginpage.databinding.RvAttendanceBinding
import com.example.loginpage.databinding.RvMembersBinding
import com.example.loginpage.others.Constants
import java.text.SimpleDateFormat
import java.util.*


class AttendanceAdapter()  : RecyclerView.Adapter<AttendanceAdapter.JoinedMemberViewHolder>()  {

    class JoinedMemberViewHolder(val binding : RvAttendanceBinding) : RecyclerView.ViewHolder(binding.root)

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(position: (String) -> Unit) {
        onItemClickListener = position
    }


    private val diffCallback = object : DiffUtil.ItemCallback<String>() {

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var membersList : List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JoinedMemberViewHolder {
        val binding = RvAttendanceBinding.inflate(
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

                binding.classTv.text = data

                binding.classTv.isChecked = Constants.presentMembers.contains(data)

                binding.classTv.setOnCheckedChangeListener { compoundButton, b ->
                    if (b){
                        Constants.presentMembers.add(data)
                    } else {
                        Constants.presentMembers.remove(data)
                    }
                }
                
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