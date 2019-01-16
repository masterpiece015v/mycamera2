package tokyo.mp015v.mycamera

import android.util.Log
import android.widget.TextView
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.internal.tls.OkHostnameVerifier

class JsonPost{
    fun getJsonObject(url:String, body:String) : Deferred<JsonObject> = GlobalScope.async{
        val client = OkHttpClient()
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)
        val request = Request.Builder().url( url ).post(requestBody).build()

        val response = client.newCall(request).execute()
        return@async Json.parse(response.body()!!.string()).asObject()
    }
    fun getJsonString(url:String, body:String) : Deferred<String> = GlobalScope.async{
        val client = OkHttpClient()
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)
        val request = Request.Builder().url( url ).post(requestBody).build()

        val response = client.newCall(request).execute()
        return@async response.body()!!.string()
    }
}