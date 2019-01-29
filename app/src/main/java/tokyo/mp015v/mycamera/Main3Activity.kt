package tokyo.mp015v.mycamera

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class Main3Activity : AppCompatActivity() {
    val ENDPOINT_URL = "https://vision.googleapis.com/v1"
    val API_KEY = "AIzaSyBCPU4CLhm_TwguXXXFYSvz20wWPqad1Rc"
    val LIKELIHOOD = mapOf("VERY_LIKELY" to "100%" , "LIKELY" to "75%" , "POSSIBLE" to "50%" , "UNLIKELY" to "25%" , "VERY_UNLIKELY" to "0%")
    lateinit var likelihood : String
    lateinit var canvas : MyCanvas
    val rects = Rects()
    lateinit var newBitmap : Bitmap
    lateinit var curPath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        //Toolbarを追加する
        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar( toolbar )
        //supportActionBar!!.setDisplayHomeAsUpEnabled( true )

        //キャンバスの設定
        canvas = this.findViewById<MyCanvas>(R.id.myCanvas1)

        //インテントから画像のパスを取得する
        val path = intent.getStringExtra("path")
        val size = intent.getLongExtra("size",0)
        this.curPath = intent.getStringExtra( "curPath")
        //画像のビットマップを作成する
        val inputStream = FileInputStream( File( path ) )
        //画像の圧縮
        val options = BitmapFactory.Options().apply{
            if( size > 4000000) {
                inSampleSize = 16
            }else if( size > 2000000){
                inSampleSize = 8
            }else if( size > 1000000) {
                inSampleSize = 4
            }else if( size > 500000) {
                inSampleSize = 2
            }else{
                inSampleSize = 1
            }
        }
        val bitmap = BitmapFactory.decodeStream( inputStream ,null,options )

        //val bitmap = BitmapFactory.decodeStream( inputStream )
        val bitmapWidth = bitmap.width
        val bitmapHeight = bitmap.height
        val display = windowManager.defaultDisplay
        val displaySize = Point()
        display.getSize( displaySize )
        //Log.d("debug","bitmap.byte="  + bitmap.byteCount)
        val scale = displaySize.x.toFloat() / bitmapWidth

        //Log.d( "debug","scale:${scale}")

        newBitmap = Bitmap.createScaledBitmap(bitmap,(bitmapWidth * scale).toInt(),(bitmapHeight*scale).toInt(),false)
        //Log.d("debut" , "newBitmap.byte=" + bitmap.byteCount )
        //Log.d("debug","displaySize:${displaySize}")

        //キャンバスをタッチしたときのイベント
        canvas.setOnTouchListener { v, event ->

            val x = event.getX()
            val y = event.getY()

            val rect = rects.check( x.toInt(), y.toInt() )
            if( rect != null ){
                Log.d("debug","x:${x},y:${y},text:${rect.text}")
                Toast.makeText(applicationContext,"${rect.text}",Toast.LENGTH_SHORT).show()
            }
            Log.d("debug","x:${x.toInt()},y:${y.toInt()}")
            return@setOnTouchListener false
        }

        //キャンバスに写真を表示する
        canvas.showCanvas( newBitmap )

    }
    //Toolbarにtool_menuを追加する
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool2_menu,menu )
        return super.onCreateOptionsMenu(menu)
    }

    //Toolbarのitemにイベントを登録する
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId

        when( id ){
            R.id.action_smile -> {
                GlobalScope.launch(Dispatchers.Main,CoroutineStart.DEFAULT) {
                    val url: String = ENDPOINT_URL + "/images:annotate?key=" + API_KEY
                    val body: String =
                            "{requests:[" +
                                    "{image:{content:\"${encodeToBase64(newBitmap)}\"}," +
                                    "features:{type:\"FACE_DETECTION\",maxResults:5}" +
                                    "}]" +
                                    "}"

                    val resJsonString = JsonPost().getJsonString(url,body).await()
                    val resultJson = Json.parse( resJsonString).asObject()
                    val responses = (resultJson.get("responses").asArray()[0]) as JsonObject

                    if( responses.size() > 0 ){
                        val faceAnnotations = responses.get("faceAnnotations").asArray()

                        faceAnnotations.forEach {
                            val boundingPoly = (it as JsonObject).get("boundingPoly").asObject()

                            val vertices = boundingPoly.get("vertices").asArray()

                            val x1 = vertices[0].asObject().get("x").asInt()
                            val y1 = vertices[0].asObject().get("y").asInt()
                            val x2 = vertices[2].asObject().get("x").asInt()
                            val y2 = vertices[2].asObject().get("y").asInt()

                            canvas.addRectPoint( x1,y1,x2,y2 )

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
                            rects.addRect(Rect(Point(x1,y1),Point(x2,y2),likelihood))
                        }
                        canvas.showCanvas()
                    }else{
                        Toast.makeText(applicationContext,"顔がないのでは?",Toast.LENGTH_SHORT).show()
                    }

                }
            }
            R.id.action_back->{
                //NavUtils.navigateUpFromSameTask(this)
                val intent = Intent()
                intent.putExtra("curPath",this.curPath )
                setResult(Activity.RESULT_OK,intent)
                finish()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Base64へエンコード
    private fun encodeToBase64(image: Bitmap):String{
        val out = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG,100,out)
        val b : ByteArray = out.toByteArray()
        val str = Base64.encodeToString( b , Base64.NO_WRAP )
        return str
    }

    //正方形のインナークラス
    inner class Rects{
        val rects : ArrayList<Rect> = arrayListOf()
        fun addRect( rect : Rect ){
            rects.add( rect )
        }
        fun check( x:Int , y:Int ):Rect?{
            rects.forEach{
                if(it.p1.x <= x && it.p2.x >= x && it.p1.y <= y && it.p2.y >= y ){
                    return it
                }
            }
            return null
        }
    }
    inner class Rect(p1: Point,p2:Point,text:String){
        val p1 = p1
        val p2 = p2
        val text = text
        constructor(x1:Int,y1:Int,x2:Int,y2:Int,text:String):this(Point(x1,y1),Point(x2,y2),text){}
    }
}
