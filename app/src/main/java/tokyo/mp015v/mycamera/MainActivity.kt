package tokyo.mp015v.mycamera

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    companion object{
        const val CAMERA_REQUEST_CODE = 1
        const val CAMERA_PERMISSION_REQUEST_CODE = 2
    }

    private lateinit var path : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume(){
        super.onResume()
        findViewById<Button>(R.id.button).setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let{
                if( checkPermission() ){
                    takePicture()
                }else{
                    grantCameraPermission()
                }
            }?: Toast.makeText(this,"カメラを扱うアプリがありません", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onActivityResult( requestCode: Int,resultCode : Int,data:Intent?){
        if( requestCode == CAMERA_REQUEST_CODE && resultCode== Activity.RESULT_OK){

            val contentValues = ContentValues().apply{
                put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
                put("_data",path)
            }
            contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues
            )

            val inputStream = FileInputStream( File( path ))
            val bitmap = BitmapFactory.decodeStream( inputStream )

            findViewById<ImageView>(R.id.imageView).setImageBitmap( bitmap )

            //val image = data?.extras?.get("data")?.let{
            //    findViewById<ImageView>(R.id.imageView).setImageBitmap(it as Bitmap)
            //}

        }
    }



    private fun takePicture(){
        val intent = Intent( MediaStore.ACTION_IMAGE_CAPTURE).apply{
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra( MediaStore.EXTRA_OUTPUT,createSaveFileUri())
        }

        startActivityForResult( intent , CAMERA_REQUEST_CODE)

    }

    private fun checkPermission():Boolean{
        val cameraPermission = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.CAMERA)

        val extraStoragePermission = PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission && extraStoragePermission
    }

    private fun grantCameraPermission() = ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),
            CAMERA_PERMISSION_REQUEST_CODE)

    private fun createSaveFileUri() : Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.JAPAN).format(Date())
        val imageFileName = "casalack_" + timeStamp

        val storageDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM + "/casalack")
        if(!storageDir.exists()){
            storageDir.mkdir()
        }
        val file = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )

        path = file.absolutePath

        return FileProvider.getUriForFile(this,"tokyo.mp015v.mycamera",file )

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var isGranted = true

        if( requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            //if( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //    takePicture()
            //}
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
            takePicture()
        }else{
            grantCameraPermission()
        }

    }
}
