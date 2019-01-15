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
import com.github.kittinunf.fuel.core.FuelManager
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
        val bitmap = BitmapFactory.decodeStream( inputStream )
        findViewById<ImageView>(R.id.imageView2).setImageBitmap( bitmap )

        findViewById<Button>(R.id.button4).setOnClickListener{
            object : AsyncTask<Void,Int,Void>(){
                override fun doInBackground( vararg prm : Void?) : Void?{
                    //request用jsonの生成
                    FuelManager.instance.basePath = ENDPOINT_URL
                    FuelManager.instance.baseHeaders = mapOf("Content-Type" to "application/json")
                    val path:String="images:annotate?key=" + API_KEY

                    val body:String=
                            "{requests:[" +
                                    "{image:{content:\"${encodeToBase64(bitmap)}\"},"+
                                    "features:{type:\"FACE_DETECTION\",maxResults:5}"+
                                    "}]"+
                                "}"

                    //Fuel.post(path).body(body).responseString { request, response, result ->
                    //    val resultJson = Json.parse( result.get()).asObject()
                    //    Log.d( "debug","requests=" + resultJson.get("requests").asString())
                    //}

                    val client = OkHttpClient()
                    val requestBody = RequestBody.create( MediaType.parse("application/json; charset=utf-8"),body)
                    val request = Request.Builder().url( ENDPOINT_URL + "/" + path ).post( requestBody ).build()

                    val response = client.newCall( request ).execute()

                    //Log.d("debug",response.body()!!.string())

                    val resultJson = Json.parse(response.body()!!.string()).asObject()
                    val responses = (resultJson.get("responses").asArray()[0]) as JsonObject
                    val faceAnnotations = (responses.get("faceAnnotations").asArray()[0]) as JsonObject
                    val boundingPoly = faceAnnotations.get("boundingPoly").asObject()
                    val fdBoundingPoly = faceAnnotations.get("fdBoundingPoly").asObject()
                    val rollAngle = faceAnnotations.get("rollAngle").asDouble()
                    val panAngle = faceAnnotations.get("panAngle").asDouble()
                    val tiltAngle = faceAnnotations.get("tiltAngle").asDouble()
                    val detectionConfidence = faceAnnotations.get("detectionConfidence").asDouble()
                    val landmarkingConfidence = faceAnnotations.get("landmarkingConfidence").asDouble()
                    val joyLikelihood = faceAnnotations.get("joyLikelihood").asString()
                    val sorrowLikelihood = faceAnnotations.get("sorrowLikelihood").asString()
                    val angerLikelihood = faceAnnotations.get("angerLikelihood").asString()
                    val surpriseLikelihood = faceAnnotations.get("surpriseLikelihood").asString()
                    val underExposedLikelihood = faceAnnotations.get("underExposedLikelihood").asString()
                    val blurredLikelihood = faceAnnotations.get("blurredLikelihood").asString()
                    val headwearLikelihood = faceAnnotations.get("headwearLikelihood").asString()

                    val outJson = faceAnnotations


                    likelihood = "楽しさ${LIKELIHOOD.get(joyLikelihood)}\n悲しさ${LIKELIHOOD.get(sorrowLikelihood)}\n怒り${LIKELIHOOD.get(angerLikelihood)}\n驚き${LIKELIHOOD.get(surpriseLikelihood)}"
                    Log.d("debug", "responses=" + outJson.toString() )
                    Log.d("debug","楽しさ" + LIKELIHOOD.get(joyLikelihood) )
                    Log.d("debug", "悲しさ" + LIKELIHOOD.get(sorrowLikelihood))
                    Log.d("debug" , "怒り" + LIKELIHOOD.get(angerLikelihood))
                    Log.d("debug","驚き" + LIKELIHOOD.get(surpriseLikelihood))
                    //Log.d("debug", resultJson.toString())
                    return null
                }

                override fun onProgressUpdate( vararg  values : Int?){
                    //textView.text = values[0].toString()
                }
                override fun onPostExecute(result : Void?){
                    findViewById<TextView>(R.id.textView3).text = likelihood

                }
            }.apply{
                execute()
            }
        }

    }

}
