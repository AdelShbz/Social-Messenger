package com.example.socialmessenger

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.socialmessenger.databinding.ActivityAuthBinding
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private var gson: Gson = Gson()
    private val apiService = RetrofitClient.instance
    var isRegister: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.tvRegLog.setOnClickListener {
            if(isRegister){
                binding.apply {
                    buttonSubmit.text = "ورود"
                    tvRegLog.text = "برای ثبت نام اینجا کلیک کنید"
                }
                isRegister = false
            } else {
                binding.apply {
                    buttonSubmit.text = "ثبت نام"
                    tvRegLog.text = "برای ورود اینجا کلیک کنید"
                }
                isRegister = true
            }
        }

        binding.buttonSubmit.setOnClickListener {
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (isRegister) {
                val user = User(username, password)
                apiService.postRegisterUser(user).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = gson.fromJson(response.body()?.string() , AuthResponse::class.java)
                            if (res.msg == "done"){
                                val intent = Intent(this@AuthActivity, ChatListActivity::class.java)
                                intent.putExtra("TOKEN",res.token)
                                startActivity(intent)
                                Toast.makeText(this@AuthActivity,res.msg,Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@AuthActivity,res.msg,Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val error = "Error: ${response.code()}"
                            Toast.makeText(this@AuthActivity,error,Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure( call: Call<ResponseBody>, t: Throwable) {
                        val error = "Error: ${t.message}"
                        Toast.makeText(this@AuthActivity,error,Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                val user = User(username, password)
                apiService.postLoginUser(user).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val res = gson.fromJson(response.body()?.string() , AuthResponse::class.java)
                            if (res.msg == "done"){
                                val intent = Intent(this@AuthActivity, ChatListActivity::class.java)
                                intent.putExtra("TOKEN", res.token)
                                startActivity(intent)
                                Toast.makeText(this@AuthActivity,res.msg,Toast.LENGTH_LONG).show()
                            } else {
//                                res.msg
                                Toast.makeText(this@AuthActivity,"username or password is incorrect",Toast.LENGTH_LONG).show()
                            }

                        } else {
                            val error = "Error: ${response.code()}"
                            Toast.makeText(this@AuthActivity,error,Toast.LENGTH_LONG).show()
                        }
                    }
                    override fun onFailure( call: Call<ResponseBody>, t: Throwable) {
                        val error = "Error: ${t.message}"
                        Toast.makeText(this@AuthActivity,error,Toast.LENGTH_LONG).show()
                    }
                })
            }

        }

    }
}