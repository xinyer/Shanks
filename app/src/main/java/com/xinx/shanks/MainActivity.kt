package com.xinx.shanks

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.xinx.shanks.data.AccessToken
import com.xinx.shanks.data.GithubService

import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val clientId = "e0a7147e02b715583615"
    private val clientSecret = "146b8e6512a8f5e53e1ab7e37135ae8d778c0234"
    private val redirectUri = "shanks://oauth_callback"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_auth.setOnClickListener {
            val authUri = "https://github.com/login/oauth/authorize?client_id=$clientId&scope=repo&redirect_uri=$redirectUri"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUri))
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            val code = uri.getQueryParameter("code")
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val client = retrofit.create(GithubService::class.java)
            val token = client.getAccessToken(clientId, clientSecret, code)
            token.enqueue(object : Callback<AccessToken> {
                override fun onFailure(call: Call<AccessToken>, t: Throwable) {


                }

                override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                    val accessToken = response.body()!!.accessToken
                    tv_token.text = accessToken
                    Toast.makeText(this@MainActivity, "access_token: $accessToken", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
