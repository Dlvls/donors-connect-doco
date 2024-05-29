package com.example.bloodbank006_3.profile.pager

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbank006_3.R
import com.example.bloodbank006_3.data.ClientDatabase
import com.example.bloodbank006_3.data.entity.DonorRequestEntity
import com.example.bloodbank006_3.data.entity.RequestStatus
import com.example.bloodbank006_3.databinding.FragmentDonationBinding
import com.example.bloodbank006_3.databinding.ItemDonorDonatedBinding

class DonationFragment : Fragment() {

    private var _binding: FragmentDonationBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestAdapter: RequestAdapter
    private val requestList = mutableListOf<DonorRequestEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDonationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView and Adapter
        requestAdapter = RequestAdapter(requestList)
        binding.rvDonationFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDonationFragment.adapter = requestAdapter

        // Call a method to fetch the request data and update the list
        fetchRequestData()
    }

    // Call this method to fetch the request data and update the list
    private fun fetchRequestData() {
        Log.d("DonationFragment", "Fetching request data...")

        val sharedPreferences =
            requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")

        // Fetch the request data from the DonorRequestDao in AppDatabase
        val clientDatabase = ClientDatabase.getInstance(requireContext())
        val donorRequestDao = clientDatabase?.appDatabase?.donorRequestDao()

        donorRequestDao?.getAllDonorRequests()?.observe(viewLifecycleOwner) { requestData ->
            val donatedUsers = requestData.filter { it.uploadedBy == email }

            Log.d("DonationFragment", "Email: $email")
            Log.d("DonationFragment", "Donated Users: $donatedUsers")

            // Update the requestList with the retrieved data
            requestList.clear()
            requestList.addAll(donatedUsers)

            // Notify the adapter that the data set has changed
            requestAdapter.notifyDataSetChanged()

            Log.d("DonationFragment", "Request data fetched: $requestData")
        }
    }

    inner class RequestAdapter(private val requestList: List<DonorRequestEntity>) :
        RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemDonorDonatedBinding.inflate(inflater, parent, false)
            return RequestViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
            val request = requestList[position]

            // Bind the request data to the views
            holder.bind(request)
        }

        override fun getItemCount(): Int {
            return requestList.size
        }

        inner class RequestViewHolder(private val binding: ItemDonorDonatedBinding) :
            RecyclerView.ViewHolder(binding.root) {

//            init {
//                binding.btnDone.setOnClickListener {
//                    val position = adapterPosition
//                    if (position != RecyclerView.NO_POSITION) {
//                        val request = requestList[position]
//                        updateRequestStatus(request)
//                    }
//                }
//            }

            fun bind(request: DonorRequestEntity) {
                // Bind the request data to the views in the item layout
                binding.tvUsername.text = request.requester
                binding.tvDate.text = request.calendarDetails.date
                // Bind other views with relevant data

                // Set the appropriate text and text color for tvStatus based on the value of request.status
                val statusText: String
                val statusColor: Int

                when (request.status) {
                    RequestStatus.ACCEPTED -> {
                        statusText = "Donated"
                        statusColor =
                            ContextCompat.getColor(itemView.context, R.color.success_green)
//                        binding.btnDone.visibility = View.GONE
                    }
//                    RequestStatus.PROCESSED -> {
//                        statusText = "On Process"
//                        statusColor =
//                            ContextCompat.getColor(itemView.context, R.color.on_process_yellow)
////                        binding.btnDone.visibility = View.VISIBLE
//                    }
                    RequestStatus.DECLINED -> {
                        statusText = "Failed"
                        statusColor = ContextCompat.getColor(itemView.context, R.color.primary_red)
//                        binding.btnDone.visibility = View.GONE
                    }
                    RequestStatus.PENDING -> {
                        statusText = "Pending"
                        statusColor =
                            ContextCompat.getColor(itemView.context, R.color.pending_orange)
//                        binding.btnDone.visibility = View.GONE
                    }
                }
                binding.tvStatus.text = statusText
                binding.tvStatus.setTextColor(statusColor)
            }
        }

//        private fun updateRequestStatus(request: DonorRequestEntity) {
//            val clientDatabase = ClientDatabase.getInstance(binding.root.context)
//            val donorRequestDao = clientDatabase?.appDatabase?.donorRequestDao()
//
//            // Run the database operation on a background thread using coroutines
//            lifecycleScope.launch(Dispatchers.IO) {
//                // Update the request status in the database
//                donorRequestDao?.updateRequestStatus(request.uploadedBy, RequestStatus.ACCEPTED)
//
//                // Log the request status
//                Log.d("DonationFragment", "Request status updated: ${request.status}")
//
//                // Notify the adapter that the data set has changed
//                withContext(Dispatchers.Main) {
//                    requestAdapter.notifyDataSetChanged()
//                }
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}