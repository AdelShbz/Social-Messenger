package com.example.socialmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityCreateGroupBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class CreateGroupActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreateGroupBinding
    private var contactList = mutableListOf<ContactList>()
    private lateinit var adapter: CreateGroupAdapter
    private val apiService = RetrofitClient.instance
    lateinit var token: String
    lateinit var self_username: String
    private var gson: Gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        token = intent.getStringExtra("TOKEN").toString()
        self_username = intent.getStringExtra("USERNAME").toString()
        binding.button.setOnClickListener {
            if(adapter.selectedItems.size == 0){
                Toast.makeText(this, "اعضای گروهتو انتخاب کن.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this@CreateGroupActivity, GroupNamingActivity::class.java)
//            Log.d("test", adapter.selectedItems.toString())
            intent.putExtra("TOKEN", token)
            intent.putExtra("USERNAME", self_username)
            adapter.selectedItems.add(ContactList(self_username,""))
            val selectedItemsJson = gson.toJson(adapter.selectedItems)
            intent.putExtra("SELECTED_ITEMS", selectedItemsJson)
            startActivity(intent)
        }
        apiService.getContactList("Bearer $token").enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    val jsonString = response.body()?.string()
                    val type = object : TypeToken<MutableList<ContactList>>() {}.type
                    contactList = gson.fromJson(jsonString, type)
                    setupRecyclerView()
                    adapter.notifyDataSetChanged()
                } else {
                    val error = "Error: ${response.code()}"
                    Toast.makeText(this@CreateGroupActivity, "network error." , Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure( call: Call<ResponseBody>, t: Throwable) {
                val error = "Error: ${t.message}"
                Toast.makeText(this@CreateGroupActivity, "network error.", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun setupRecyclerView() {
        adapter = CreateGroupAdapter(contactList, this@CreateGroupActivity)
        binding.rvCreateGroup.layoutManager = LinearLayoutManager(this)
        binding.rvCreateGroup.adapter = adapter
    }
}