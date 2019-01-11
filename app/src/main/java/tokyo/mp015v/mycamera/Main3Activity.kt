package tokyo.mp015v.mycamera

import android.annotation.SuppressLint
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
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class Main3Activity : AppCompatActivity() {
    val ENDPOINT_URL = "https://vision.googleapis.com/v1"
    val API_KEY = ""

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

        val textView = findViewById<TextView>(R.id.textView)

        val path = intent.getStringExtra("path")
        Log.d( "debug", "path=" + intent.getStringExtra("path "))
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
                    //Log.d("debug","json=" + body )

                    Fuel.post(path).body(body).responseString { request, response, result ->
                        Log.d("debug", "response=" + String(response.data))

                        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                        val adapter = moshi.adapter(ResponsesJson::class.java)
                        val res = adapter.fromJson(String(response.data))
                        Log.d("debug",res.toString())
                        for (r in res!!.responses) {
                            for (fa in r.faceAnnotations) {
                                val bp = fa.boundingPoly
                                for( vt in bp.vertices){
                                    Log.d("debug","x=" + vt.x )
                                    Log.d("debug","y=" + vt.y )
                                }
                            }
                        }
                    }
                    return null
                }

                override fun onProgressUpdate( vararg  values : Int?){
                    //textView.text = values[0].toString()
                }
                override fun onPostExecute(result : Void?){
                    //textView.text = j_str
                }
            }.apply{
                execute()
            }
        }

    }

}
