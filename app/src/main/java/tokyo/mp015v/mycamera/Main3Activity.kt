package tokyo.mp015v.mycamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.JsonValue
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class Main3Activity : AppCompatActivity() {
    val ENDPOINT_URL = "https://vision.googleapis.com/v1"
    val API_KEY = "AIzaSyBCPU4CLhm_TwguXXXFYSvz20wWPqad1Rc"
    val LIKELIHOOD = mapOf("VERY_LIKELY" to "100%" , "LIKELY" to "75%" , "POSSIBLE" to "50%" , "UNLIKELY" to "25%" , "VERY_UNLIKELY" to "0%")
    lateinit var likelihood : String

    private fun encodeToBase64(image: Bitmap):String{
        val out = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG,100,out)
        val b : ByteArray = out.toByteArray()
        val str = Base64.encodeToString( b , Base64.NO_WRAP )
        return str
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val path = intent.getStringExtra("path")
        Log.d( "debug", "path=" + intent.getStringExtra("path"))

        val inputStream = FileInputStream( File( path ) )

        val options = BitmapFactory.Options().apply{
            inSampleSize = 2
        }

        val bitmap = BitmapFactory.decodeStream( inputStream ,null,options )

        findViewById<ImageView>(R.id.imageView2).setImageBitmap( bitmap  )

        findViewById<Button>(R.id.button4).setOnClickListener {
            GlobalScope.launch(Dispatchers.Main,CoroutineStart.DEFAULT) {
                val url: String = ENDPOINT_URL + "/images:annotate?key=" + API_KEY
                val body: String =
                        "{requests:[" +
                                "{image:{content:\"${encodeToBase64(bitmap)}\"}," +
                                    "features:{type:\"FACE_DETECTION\",maxResults:5}" +
                                "}]" +
                                "}"
                //val client = OkHttpClient()
                //val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body)
                //val request = Request.Builder().url(ENDPOINT_URL + "/" + path).post(requestBody).build()

                //Maiスレッドで通信をするとエラーになるのでコルーチン化
                //val res: Deferred<String?> = GlobalScope.async(Dispatchers.Default,CoroutineStart.DEFAULT) {
                    //val response = client.newCall(request).execute()
                    //return@async response.body()!!.string()
                //}

                //val resultJson = Json.parse( res.await() ).asObject()
                //val resultJson= JsonPost().getJsonObject(url,body).await()
                val resJsonString = JsonPost().getJsonString(url,body).await()
                val resultJson = Json.parse( resJsonString).asObject()

                val responses = (resultJson.get("responses").asArray()[0]) as JsonObject
                val faceAnnotations = responses.get("faceAnnotations").asArray()

                faceAnnotations.forEach {
                    val boundingPoly = (it as JsonObject).get("boundingPoly").asObject()
                    val fdBoundingPoly = (it as JsonObject).get("fdBoundingPoly").asObject()
                    val rollAngle = (it as JsonObject).get("rollAngle").asDouble()
                    val panAngle = (it as JsonObject).get("panAngle").asDouble()
                    val tiltAngle = (it as JsonObject).get("tiltAngle").asDouble()
                    val detectionConfidence = (it as JsonObject).get("detectionConfidence").asDouble()
                    val landmarkingConfidence = (it as JsonObject).get("landmarkingConfidence").asDouble()
                    val joyLikelihood = (it as JsonObject).get("joyLikelihood").asString()
                    val sorrowLikelihood = (it as JsonObject).get("sorrowLikelihood").asString()
                    val angerLikelihood = (it as JsonObject).get("angerLikelihood").asString()
                    val surpriseLikelihood = (it as JsonObject).get("surpriseLikelihood").asString()
                    val underExposedLikelihood = (it as JsonObject).get("underExposedLikelihood").asString()
                    val blurredLikelihood = (it as JsonObject).get("blurredLikelihood").asString()
                    val headwearLikelihood = (it as JsonObject).get("headwearLikelihood").asString()
                    likelihood = "楽しさ${LIKELIHOOD.get(joyLikelihood)}\n悲しさ${LIKELIHOOD.get(sorrowLikelihood)}\n怒り${LIKELIHOOD.get(angerLikelihood)}\n驚き${LIKELIHOOD.get(surpriseLikelihood)}"
                    val outJson = faceAnnotations
                    Log.d("debug", "responses=" + outJson.toString())
                    Log.d("debug", "楽しさ" + LIKELIHOOD.get(joyLikelihood))
                    Log.d("debug", "悲しさ" + LIKELIHOOD.get(sorrowLikelihood))
                    Log.d("debug", "怒り" + LIKELIHOOD.get(angerLikelihood))
                    Log.d("debug", "驚き" + LIKELIHOOD.get(surpriseLikelihood))
                }
                findViewById<TextView>(R.id.textView3).text = likelihood
            }
        }
    }
}
