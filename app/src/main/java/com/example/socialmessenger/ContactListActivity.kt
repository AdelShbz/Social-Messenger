package com.example.socialmessenger

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityContactListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactListActivity : AppCompatActivity() {
    lateinit var binding: ActivityContactListBinding
    private var gson: Gson = Gson()
    private var contactList = mutableListOf<ContactList>()
    private lateinit var adapter: ContactAdapter
    private val apiService = RetrofitClient.instance
    lateinit var token: String
    lateinit var self_username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        token = intent.getStringExtra("TOKEN").toString()
        self_username = intent.getStringExtra("USERNAME").toString()
        Toast.makeText(this, token, Toast.LENGTH_LONG).show()
        apiService.getContactList().enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonString = response.body()?.string()
                    val type = object : TypeToken<MutableList<ContactList>>() {}.type
                    contactList = gson.fromJson(jsonString, type)
                    setupRecyclerView()
                    adapter.notifyDataSetChanged()
                } else {
                    val error = "Error: ${response.code()}"
                    Toast.makeText(this@ContactListActivity, "network error." , Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure( call: Call<ResponseBody>, t: Throwable) {
                val error = "Error: ${t.message}"
                Toast.makeText(this@ContactListActivity, "network error.", Toast.LENGTH_LONG).show()
            }
        })




    }

    private fun setupRecyclerView() {
        adapter = ContactAdapter(contactList, this@ContactListActivity, token, self_username)
        binding.rvContact.layoutManager = LinearLayoutManager(this)
        binding.rvContact.adapter = adapter
    }
}