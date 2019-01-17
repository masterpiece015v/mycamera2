package tokyo.mp015v.mycamera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.util.Log
import android.widget.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    override fun onResume(){
        super.onResume()
        if( checkPermission()){
            setListView()
        }else{
            grantStoragePermission()
        }
    }

    private fun checkPermission():Boolean{
        val extraStoragePermission = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)

        return extraStoragePermission
    }

    private fun grantStoragePermission() = ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            MainActivity.STORAGE_PERMISSION_REQUEST_CODE)


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var isGranted = true

        if( requestCode == MainActivity.STORAGE_PERMISSION_REQUEST_CODE){

            if( grantResults.isNotEmpty()){
                grantResults.forEach{
                    if( it != PackageManager.PERMISSION_GRANTED){
                        isGranted = false
                    }
                }
            }else{
                isGranted = false
            }
        }else{
            isGranted = false
        }

        if( isGranted ){
            setListView()
        }else{
            grantStoragePermission()
        }

    }

    //リストに画像を表示する
    private fun setListView(){
        findViewById<Button>(R.id.button3).setOnClickListener {
            val selection = "_data like '%casalack%'"
            val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,selection,null,null)
            cursor.moveToFirst()

            val str = String.format("MediaStore.Images=%s\n\n",cursor.getCount())
            lateinit var path : String
            lateinit var fileName : String
            var size : Long
            val listItem = ArrayList<ListItem>()

            do{

                path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA))
                fileName = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DISPLAY_NAME ))
                val sizeColumn = cursor.getColumnIndex( MediaStore.MediaColumns.SIZE)
                size = cursor.getLong( sizeColumn )
                val id = cursor.getLong(cursor.getColumnIndex("_id"))
                val thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null)

                val item = ListItem( thumbnail , fileName, path ,size)
                listItem.add( item )

            }while( cursor.moveToNext())

            cursor.close()

            findViewById<ListView>(R.id.listView).apply {
                adapter = ListAdapter(applicationContext, R.layout.list_item, listItem )
                setOnItemClickListener{parent, view, position, id ->
                    val item = parent.getItemAtPosition(position) as ListItem
                    Toast.makeText( applicationContext, item.path ,Toast.LENGTH_LONG ).show()

                    val intent = Intent().apply{
                        setClassName( "tokyo.mp015v.mycamera","tokyo.mp015v.mycamera.Main3Activity")
                        putExtra("path", item.path )
                        putExtra("size", item.size)
                    }
                    startActivity( intent )
                }
            }

        }

    }


    //bitmapをbase64にエンコード
    private fun encodeToBase64(image: Bitmap):String{
        val out = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG,100,out)
        val b : ByteArray = out.toByteArray()
        val str = Base64.encodeToString( b , Base64.NO_WRAP )
        return str
    }
}
