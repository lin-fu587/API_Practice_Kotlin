package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var btn_query: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_query = findViewById(R.id.btn_query)
        btn_query.setOnClickListener(View.OnClickListener {
            //String url = "http://140.124.73.33:3001/station";
            val url = "https://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=55ec6d6e-dc5c-4268-a725-d04cc262172b"
            val req = Request.Builder().url(url).build()

            OkHttpClient().newCall(req).enqueue(object : Callback {
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        if (response.body == null) return

                        val data = Gson().fromJson(response.body!!.string(), Data::class.java)

                        val items = arrayOfNulls<String>(data.result!!.results.size)

                        for (i in items.indices)
                            items[i] = "\n列車即將進入 :" +data.result!!.results[i].Station+"\n列車行駛目的地 : " + data.result!!.results[i].Destination

                        runOnUiThread {
                            AlertDialog.Builder(this@MainActivity)
                                    .setTitle("台北捷運列車進站站名")
                                    .setItems(items, null)
                                    .show()
                        }
                    } else if (!response.isSuccessful)
                        Log.e("伺服器錯誤", response.code.toString() + "" + response.message)
                    else
                        Log.e("其他錯誤", response.code.toString() + "" + response.message)
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.e("其他錯誤", e.toString())
                }
            })
        })
    }

    class Data {
        var result: Result? = null

        class Result {
            lateinit var results: Array<Results>

            class Results {
                var Station: String? = null
                var Destination: String? = null
            }
        }
    }
}