package com.example.bloodbank006_3.notification

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbank006_3.R
import com.example.bloodbank006_3.data.entity.DonorRequestEntity
import com.example.bloodbank006_3.data.entity.RequestStatus
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val donorRequests: List<DonorRequestEntity>,
    private val updateStatusListener: (DonorRequestEntity, RequestStatus) -> Unit,
    private val statusUpdateCallback: () -> Unit
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val donorRequest = donorRequests[position]
        holder.bind(donorRequest)
    }

    override fun getItemCount(): Int {
        return donorRequests.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)

        //        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)

        fun bind(donorRequest: DonorRequestEntity) {

            val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
            val btnDeclined: Button = itemView.findViewById(R.id.btnDecline)
            val imgWa: TextView = itemView.findViewById(R.id.imgWa)
            val tvDeclined: TextView = itemView.findViewById(R.id.tvDeclined)

            when (donorRequest.status) {
                RequestStatus.ACCEPTED -> {
                    // If the status is false, show the accept and decline buttons, and hide the imgWa ImageView
                    btnAccept.visibility = View.GONE
                    btnDeclined.visibility = View.GONE
                    imgWa.visibility = View.VISIBLE
                    tvDeclined.visibility = View.GONE
                }
//                RequestStatus.PROCESSED -> {
//                    // If the status is true, hide the accept and decline buttons, and make the imgWa ImageView appear
//                    btnAccept.visibility = View.GONE
//                    btnDeclined.visibility = View.GONE
//                    imgWa.visibility = View.VISIBLE
//                    tvDeclined.visibility = View.GONE
//                }
                RequestStatus.DECLINED -> {
                    // If the status is false, show the accept and decline buttons, and hide the imgWa ImageView
                    btnAccept.visibility = View.GONE
                    btnDeclined.visibility = View.GONE
                    imgWa.visibility = View.GONE
                    tvDeclined.visibility = View.VISIBLE
                }
                else -> {
                    btnAccept.visibility = View.VISIBLE
                    btnDeclined.visibility = View.VISIBLE
                    imgWa.visibility = View.GONE
                    tvDeclined.visibility = View.GONE
                }
            }

            btnAccept.setOnClickListener {
                Log.d("ButtonClick", "Accept button clicked")

                updateStatusListener.invoke(donorRequest, RequestStatus.ACCEPTED)
                Log.d("StatusUpdate", "Request status updated to ACCEPTED")

                // Show a toast message
                val toastMessage = "Request Accepted"
                Toast.makeText(itemView.context, toastMessage, Toast.LENGTH_SHORT).show()

                // Call the statusUpdateCallback function
                statusUpdateCallback.invoke()
                Log.d("Callback", "statusUpdateCallback invoked")
            }

            btnDeclined.setOnClickListener {
                updateStatusListener.invoke(donorRequest, RequestStatus.DECLINED)

                // Show a toast message
                val toastMessage = "Request Declined"
                Toast.makeText(itemView.context, toastMessage, Toast.LENGTH_SHORT).show()

                // Call the statusUpdateCallback function
                statusUpdateCallback.invoke()
            }

            // Set the data from donorRequest to the views
            val formattedDate = formatDate(donorRequest.calendarDetails.date)
            titleTextView.text = formattedDate
//            descriptionTextView.text = donorRequest.calendarDetails.time
            timestampTextView.text = donorRequest.additionalMessage
        }

        private fun formatDate(date: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH)

            val parsedDate: Date? = inputFormat.parse(date)
            return parsedDate?.let { outputFormat.format(it) } ?: ""
        }
    }
}