package com.example.bloodbank006_3.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bloodbank006_3.data.entity.FormEntitiy
import com.example.bloodbank006_3.databinding.ItemDonorFormBinding

class HomeAdapter(
    private val modelList: MutableList<FormEntitiy>,
    private val adapterCallback: MainAdapterCallback
) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemDonorFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = modelList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    fun setData(data: List<FormEntitiy>) {
        modelList.clear()
        modelList.addAll(data)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemDonorFormBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(model: FormEntitiy) {
            binding.apply {
                tvItemName.text = model.name
                tvItemBloodType.text = model.bloodType

                // Log the image URI
                Log.d("HomeAdapter", "Image URI: ${model.imageUri}")

                // Load and display the image using Glide and the provided imageUri
                Glide.with(itemView)
                    .load(model.imageUri)
                    .into(roundedImageView)

                imageDelete.setOnClickListener {
                    val modelLaundry: FormEntitiy = modelList[adapterPosition]
                    adapterCallback.onDelete(modelLaundry)
                }
//                Log.d(
//                    "Upload Info",
//                    "Form Uploader: ${model.formUploader}, Username: ${model.username}"
//                )
            }
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val modelLaundry: FormEntitiy = modelList[position]
                adapterCallback.onItemClick(modelLaundry)
//                Log.d(
//                    "Upload Info",
//                    "Form Uploader: ${modelLaundry.formUploader}, Username: ${modelLaundry.username}"
//                )

                adapterCallback.onFormUploaderClick(modelLaundry.formUploader)
            }
        }
    }

    interface MainAdapterCallback {
        fun onDelete(model: FormEntitiy)
        fun onItemClick(model: FormEntitiy)
        fun onFormUploaderClick(formUploader: String)
    }
}