package com.example.bloodbank006_3.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bloodbank006_3.data.ClientDatabase
import com.example.bloodbank006_3.data.entity.DonorRequestEntity
import com.example.bloodbank006_3.data.entity.UserEntity
import com.example.bloodbank006_3.databinding.ActivityNotificationListBinding
import com.example.bloodbank006_3.MainActivity
import com.example.bloodbank006_3.data.entity.RequestStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var adapter: NotificationAdapter
    private lateinit var clientDatabase: ClientDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        clientDatabase = ClientDatabase.getInstance(applicationContext)!!

        val recyclerView: RecyclerView = binding.notificationRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        val donorRequests = ArrayList<DonorRequestEntity>()

        getRegisteredFormUploadedUserEmail { loggedInUserEmail, currentUserDonorRequests ->
            adapter = NotificationAdapter(
                currentUserDonorRequests,
                { donorRequest, newStatus ->
                    updateRequestStatusInBackground(donorRequest, newStatus)
                },
                {
                    onRequestStatusUpdate()
                }
            )

            recyclerView.adapter = adapter

            for (donorRequest in currentUserDonorRequests) {
                Log.d("Notification", "Donor Request: $donorRequest")
                Log.d("Notification", "Uploaded By: ${donorRequest.uploadedBy}")
            }

            Log.d("Notification", "Current User: $loggedInUserEmail")
            Log.d("Notification", "Total Donor Requests: ${currentUserDonorRequests.size}")
        }
    }

    private fun getRegisteredFormUploadedUserEmail(
        callback: (String, List<DonorRequestEntity>) -> Unit,
    ) {
        val userLiveData = clientDatabase.appDatabase.authDao().getUsers()

        val observer = object : Observer<List<UserEntity>> {
            override fun onChanged(users: List<UserEntity>) {
                if (users.isNotEmpty()) {
                    val sharedPreferences =
                        getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val email = sharedPreferences.getString("email", "")

                    clientDatabase.appDatabase.donorRequestDao().getAllDonorRequests()
                        .observe(this@NotificationListActivity) { donorRequestList ->
                            val currentUserDonorRequests =
                                donorRequestList.filter { it.uploadedBy == email }

                            for (donorRequest in currentUserDonorRequests) {
                                Log.d("Notification", "Donor Request: $donorRequest")
                                Log.d("Notification", "Uploaded By: ${donorRequest.uploadedBy}")
                            }

                            callback(email.toString(), currentUserDonorRequests)

                            logRequestStatus(email.toString())
                        }
                }

                userLiveData.removeObserver(this)
            }
        }

        userLiveData.observe(this@NotificationListActivity, observer)
    }

    private fun logRequestStatus(email: String) {
        val statusLiveData = clientDatabase.appDatabase.donorRequestDao().getRequestStatus(email)

        statusLiveData.observe(this@NotificationListActivity) {
            val actualStatus = statusLiveData.value
            Log.d("Notification", "Request Status for $email: $actualStatus")
        }
    }

    private fun updateRequestStatusInBackground(
        donorRequest: DonorRequestEntity,
        newStatus: RequestStatus
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            clientDatabase.appDatabase.donorRequestDao().updateRequestStatusValidate(
                donorRequest.uploadedBy,
                donorRequest.requesterValidate,
                newStatus,
                false
            )

            // Log the current status after the update
            val updatedStatus = clientDatabase.appDatabase.donorRequestDao()
                .getRequestStatus(donorRequest.uploadedBy)
            Log.d("Notification", "Updated Request Status: $updatedStatus")

//            Log.d("Notification", "Status Updated: ${donorRequest.uploadedBy}")
//            Log.d("Notification", "Status Updated: ${donorRequest.uid} - Status: $newStatus")

            runOnUiThread {
                // Perform UI updates or any other necessary actions
                // This code will be executed on the main UI thread
            }
        }
    }


    private fun onRequestStatusUpdate() {
        Log.d("NotificationAdapter", "Request status updated")
    }
}
