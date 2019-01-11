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
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import com.github.kittinunf.fuel.httpPost

class Main3Activity : AppCompatActivity() {
    val ENDPOINT_URL = "https://vision.googleapis.com/v1/images:annotate"
    val API_KEY = ""
    val FILE_NAME ="images/toshimio01.jpg"


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
            var async = object : AsyncTask<Void,Int,Void>(){

                lateinit var j_str : String

                override fun doInBackground( vararg prm : Void?) : Void?{
                    //request用jsonの生成
                    val image = ImageJson( encodeToBase64( bitmap ) )
                    val feature = FeatureJson("FACE_DETECTION",5)
                    val f_ary : Array<FeatureJson> = arrayOf( feature )
                    val request = RequestJson(image,f_ary)
                    val r_ary : Array<RequestJson> = arrayOf( request )
                    val requests = RequestsJson( r_ary )
                    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                    val req = moshi.adapter( RequestsJson::class.java)
                    j_str = req.toJson( requests ).toString()


                    val list = listOf( Pair<String,String>("key",API_KEY))

                    //val (_,_,result) = Fuel.post(ENDPOINT_URL).header().body(j_str).responseString()
                    ENDPOINT_URL.httpPost(listOf("key" to API_KEY) ).responseString{
                        
                    }
                    //val (data,_) = result
                    Log.d("debug",j_str)
                    //publishProgress( 0 )
                    return null
                }

                override fun onProgressUpdate( vararg  values : Int?){
                    //textView.text = values[0].toString()
                }
                override fun onPostExecute(result : Void?){
                    textView.text = j_str
                }
            }.apply{
                execute()
            }
        }

    }

}
