package tokyo.mp015v.mycamera

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import java.io.File
import java.io.FileInputStream

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    override fun onResume(){
        super.onResume()
        findViewById<Button>(R.id.button3).setOnClickListener {
            val selection = "_data like '%casalack%'"
            val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,selection,null,null)
            cursor.moveToFirst()

            val str = String.format("MediaStore.Images=%s\n\n",cursor.getCount())
            lateinit var path : String
            lateinit var fileName : String
            val listItem = ArrayList<ListItem>()
            val options = BitmapFactory.Options().apply{
                inSampleSize = 10
            }

            do{

                path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA))
                fileName = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DISPLAY_NAME ))
                val inputStream = FileInputStream( File( path ) )
                val bitmap = BitmapFactory.decodeStream( inputStream,null,options )
                val item = ListItem( bitmap, fileName)
                listItem.add( item )
            }while( cursor.moveToNext())

            cursor.close()

            findViewById<ListView>(R.id.listView).adapter =
                    ListAdapter(applicationContext,
                            R.layout.list_item,
                            listItem)

        }
    }
    private fun createTextView( text : String) : TextView{
        var textView = TextView( this )
        textView.text = text
        return textView
    }
}
