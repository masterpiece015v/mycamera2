package tokyo.mp015v.mycamera

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    override fun onResume(){
        super.onResume()
        findViewById<Button>(R.id.button3).setOnClickListener {
            val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,null,null,null)
            cursor.moveToFirst()

            val str = String.format("MediaStore.Images=%s\n\n",cursor.getCount())
            lateinit var path : String
            val sb = StringBuilder( str ).apply{
                do{
                    //append("ID:")
                    //append(cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media._ID)))
                    //append("\n")
                    //append(cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.TITLE)))
                    //append("\n")
                    //append("Path:")

                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))

                    if( path.contains("casalack")) {
                        append(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))
                        append("\n")
                    }
                }while( cursor.moveToNext())
            }
            cursor.close()

            findViewById<TextView>(R.id.textView).text = sb
        }
    }
}
