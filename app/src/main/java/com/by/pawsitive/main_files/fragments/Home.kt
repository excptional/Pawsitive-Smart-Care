package com.by.pawsitive.main_files.fragments


import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.by.pawsitive.R
import com.by.pawsitive.db.Response
import com.by.pawsitive.db.viewmodels.AuthViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.concurrent.CompletableFuture

class Home : Fragment() {

    private lateinit var connectBtn: Button
    private lateinit var sendBtn: Button
    private lateinit var editText: EditText
    private lateinit var textView: TextView

    private var client: OkHttpClient? = null
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null
    private var connected: Boolean = false
    private val ESP32_MAC_ADDRESS = "E0:5A:1B:A7:22:10"
    private val ESP32_IP_ADDRESS = "192.168.43.46"
    private var port: Int = 80

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        connectBtn = view.findViewById(R.id.connectBtn)
        sendBtn = view.findViewById(R.id.sendBtn)
        editText = view.findViewById(R.id.editText)
        textView = view.findViewById(R.id.textView)

        connectBtn.setOnClickListener {

            if(connected) {
                disconnectFromESP32()
            } else {
                connectToESP32()
                receiveTimeFromESP32()
            }
        }

        sendBtn.setOnClickListener {
            if (connected) {
                sendDataToESP32(editText.text.toString())
            } else {
                showToast("Connect with ESP32 first")
            }
        }

        return view
    }


    private fun connectToESP32() {
        client = OkHttpClient()

        requestData("/connect").thenAccept { result ->
            requireActivity().runOnUiThread {
                textView.text = result
                connectBtn.text = "Disconnect"
                connected = true
            }
        }.exceptionally { ex ->
            requireActivity().runOnUiThread {
                showToast(ex.message.toString())
            }
            null
        }

//        GlobalScope.launch(Dispatchers.IO) {
//            val request = Request.Builder()
//                .url("http://$ESP32_IP_ADDRESS:$port")
//                .build()
//
//            try {
//                val response = client!!.newCall(request).execute()
//                if (response.isSuccessful) {
//                    val responseData = response.body?.string()
//                    updateUI(responseData)
//                    showToast("Connected to ESP32")
//                } else {
//                    connected = false
//                    showToast("Failed to connect to ESP32")
//                }
//            } catch (e: IOException) {
//                connected = false
//                showToast("Failed to connect to ESP32: ${e.message}")
//            }
//        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun receiveTimeFromESP32() {
        client = OkHttpClient()

        requestData("/time").thenAccept { result ->
            requireActivity().runOnUiThread {
                val time = "${result.trim()}000".toLong()
                val date = java.util.Date(time)
                val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
                val dateFormat = SimpleDateFormat("HH:mm MMM dd, yyyy")
                dateFormat.timeZone = timeZone
                println("The current UTC time is: ${dateFormat.format(date)}")
                textView.text = dateFormat.format(date)
                showToast(result)
                connectBtn.text = "Disconnect"
                connected = true
            }
        }.exceptionally { ex ->
            requireActivity().runOnUiThread {
                showToast(ex.message.toString())
            }
            null
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendDataToESP32(data: String) {
        if (client == null) {
            showToast("Not connected to ESP32")
            return
        }

        val requestBody = FormBody.Builder()
            .add("data", data) // Add your data here
            .build()

        val request = Request.Builder()
            .url("http://$ESP32_IP_ADDRESS/") // Assuming the root URL is used for the request
            .post(requestBody)
            .build()

        GlobalScope.launch(Dispatchers.IO) {

            try {
                val response = client!!.newCall(request).execute()
                if (response.isSuccessful) {
                    showToast("Data send successfully to ESP32: ${response.message}")
                } else {
                    val errorMessage = response.body!!.string()
                    showToast("Failed to send data to ESP32: $errorMessage")
                }
            } catch (e: IOException) {
                showToast("Failed to send data to ESP32: ${e.message}")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun requestData(endpoint: String): CompletableFuture<String> {
        val completableFuture = CompletableFuture<String>()

        // Launch a coroutine in the background
        GlobalScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://$ESP32_IP_ADDRESS$endpoint")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    completableFuture.complete(responseData)
                } else {
                    completableFuture.completeExceptionally(IOException("Failed to send request"))
                }
            } catch (e: IOException) {
                completableFuture.completeExceptionally(e)
            }
        }

        return completableFuture
    }

    private fun disconnectFromESP32() {
        client!!.dispatcher.executorService.shutdown()
        client = null
        connected = false
        textView.text = "Disconnected"
        connectBtn.text = "Connect"
        showToast("Disconnected from ESP32")
    }

    private fun updateUI(responseData: String?) {
        requireActivity().runOnUiThread {
            responseData?.let {
                connected = true
                textView.text = it
                connectBtn.text = "Disconnect"
            }
        }
    }

    private fun showToast(message: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }
}