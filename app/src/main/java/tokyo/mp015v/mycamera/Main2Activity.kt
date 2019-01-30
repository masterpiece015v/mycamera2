package tokyo.mp015v.mycamera

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Main2Activity : AppCompatActivity() {
    companion object{
        const val CAMERA_CODE = 1
        const val CAMERA_PERMISSION_CODE = 2
        const val STORAGE_PERMISSION_CODE = 3
        const val Main3Activity_CODE = 4
    }

    lateinit var timeStamp:String
    lateinit var imageFileName:String
    lateinit var picPath:String
    var depath = 0
    lateinit var curPath:String
    val dirMap = DirMap()

    //作るときの処理
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //Toolbarを追加する
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        //ツールバーにナビゲーションを表示するボタン
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        this.curPath = "casalack"

    }

    //表示するときの処理
    override fun onResume(){
        super.onResume()

        //ファイルアクセス権のチェック
        if( checkStoragePermission() ){
            //サムネイルを表示
            setListView(curPath)
            //フォルダの作成は時間がかかるので非同期処理
            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT){
                createDirMap()
                createDrawerView()
            }
        }else{
            grantWriteStoragePermission()
        }
    }

    //dirMapを作る
    fun createDirMap(){
        val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI , null, null,null,null)
        cursor.moveToFirst()
        do{
            val path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA))
            dirMap.putPath( path )
        }while( cursor.moveToNext() )
        cursor.close()
    }

    //DrawerViewを作成する
    fun createDrawerView(){
        val dirBitMap = BitmapFactory.decodeResource(resources, R.drawable.ic_dir_black)
        val directoryList = ArrayList<DirListItem>()

        dirMap.getAbPathArrayList().forEach {
            directoryList.add( DirListItem(dirBitMap, it.split("/")[it.split("/").size - 1] , it ))
        }

        var adapter = DirListAdapter( applicationContext, R.layout.dir_list_item , directoryList )

        //ドロワーのナビゲーションリストの設定
        val dListView = findViewById<ListView>(R.id.drawer_list)
        dListView.adapter = adapter

        //イベント発生時のメソッド

        dListView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as DirListItem
            setListView( item.dir_path )
            curPath = item.dir_path
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(Gravity.LEFT)
            /*

            //←が選択されたら
            if( item.dir_name.equals("←")){
                depath-=1
                var newCurPath = "/"
                tempCurPath.split("/").forEachIndexed { index, s ->
                    if(index > 0 && tempCurPath.split("/").size-1 > index ){
                        newCurPath = if( index == 1 ) newCurPath + s else newCurPath + "/" + s
                    }
                }
                tempCurPath = newCurPath
            }else{
                depath+=1
                //ルートはそのままディレクトリ名を付ける
                tempCurPath = if( tempCurPath.equals("/") ) tempCurPath + item.dir_name else tempCurPath + "/" + item.dir_name
            }

            //DrawerList用の設定
            val directoryList = ArrayList<DirListItem>()
            //ルートより深い場合
            if( depath > 1 ) {
                directoryList.add(DirListItem(dirBitMap, "←", "/"))
            }

            val selection = "_data like ?"
            val args = arrayOf("%" + tempCurPath + "%")
            Log.d("debug","2:" + tempCurPath )
            val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null , selection, args,null)

            cursor.moveToFirst()
            val checkPath =cursor.getString(cursor.getColumnIndex( MediaStore.Images.Media.DATA)).split("/")[depath]

            //直下に画像が見つかった時の処理
            if( checkPath.contains(".jpg") || checkPath.contains(".JPG")){
                curPath = tempCurPath

                findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(Gravity.LEFT)
                setListView(tempCurPath)
                depath--
                return@setOnItemClickListener
            }

            do{
                val path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA ))
                val paths = path.split("/")
                if( !directoryList.any { it.dir_name==paths[depath] }) {
                    directoryList.add(DirListItem(dirBitMap, paths[depath], "/"))
                }
            }while(cursor.moveToNext())
            cursor.close()

            val adapter = DirListAdapter( applicationContext, R.layout.dir_list_item , directoryList )
            findViewById<ListView>(R.id.drawer_list).adapter = adapter
        */
        }

    }
    //ルートディレクトリを取得する
    fun getRootFolder() : HashSet<String>{
        val retSet = HashSet<String>()
        //コンテンツリゾルバ―より画像の情報を取得する
        val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI , null, null,null,null)

        cursor.moveToFirst()
        do{
            val path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA))
            val paths = path.split("/")
            Log.d( "*****" , "paths=" + paths[1])
            retSet.add( paths[1] )

        }while( cursor.moveToNext() )
        cursor.close()

        return retSet
    }
    //下位層のフォルダを取得する
    fun getChildFolder(parentFolder : String) : HashSet<String>{
        val retSet = HashSet<String>()
        //コンテンツリゾルバ―より画像の情報を取得する
        val selection ="_data like ?"
        val args = arrayOf( "${parentFolder}%")
        val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI , null, selection,args,null)

        return retSet
    }

    //フォルダの作成
    fun createFolder(){
        val dirBitMap = BitmapFactory.decodeResource(resources, R.drawable.ic_dir_black)
        val directoryList = ArrayList<DirListItem>()
        var tempCurPath = "/"

        depath = 1

        //ルートフォルダの取得
        val set = getRootFolder()

        set.forEach {
            Log.d("******" , "it=" + it)
            directoryList.add( DirListItem(dirBitMap, it , "/" + it ))
        }

        var adapter = DirListAdapter( applicationContext, R.layout.dir_list_item , directoryList )

        //ドロワーのナビゲーションリストの設定
        val dListView = findViewById<ListView>(R.id.drawer_list)
        dListView.adapter = adapter

        //イベント発生時のメソッド
        dListView.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position) as DirListItem
            //←が選択されたら
            if( item.dir_name.equals("←")){
                depath-=1
                var newCurPath = "/"
                tempCurPath.split("/").forEachIndexed { index, s ->
                    if(index > 0 && tempCurPath.split("/").size-1 > index ){
                        newCurPath = if( index == 1 ) newCurPath + s else newCurPath + "/" + s
                    }
                }
                tempCurPath = newCurPath
            }else{
                depath+=1
                //ルートはそのままディレクトリ名を付ける
                tempCurPath = if( tempCurPath.equals("/") ) tempCurPath + item.dir_name else tempCurPath + "/" + item.dir_name
            }

            //DrawerList用の設定
            val directoryList = ArrayList<DirListItem>()
            //ルートより深い場合
            if( depath > 1 ) {
                directoryList.add(DirListItem(dirBitMap, "←", "/"))
            }

            val selection = "_data like ?"
            val args = arrayOf("%" + tempCurPath + "%")
            Log.d("debug","2:" + tempCurPath )
            val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null , selection, args,null)

            cursor.moveToFirst()
            val checkPath =cursor.getString(cursor.getColumnIndex( MediaStore.Images.Media.DATA)).split("/")[depath]

            //直下に画像が見つかった時の処理
            if( checkPath.contains(".jpg") || checkPath.contains(".JPG")){
                curPath = tempCurPath

                findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(Gravity.LEFT)
                setListView(tempCurPath)
                depath--
                return@setOnItemClickListener
            }

            do{
                val path = cursor.getString( cursor.getColumnIndex( MediaStore.Images.Media.DATA ))
                val paths = path.split("/")
                if( !directoryList.any { it.dir_name==paths[depath] }) {
                    directoryList.add(DirListItem(dirBitMap, paths[depath], "/"))
                }
            }while(cursor.moveToNext())
            cursor.close()

            val adapter = DirListAdapter( applicationContext, R.layout.dir_list_item , directoryList )
            findViewById<ListView>(R.id.drawer_list).adapter = adapter
        }
    }

    //Toolbarにtool_menuを追加する
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_menu,menu )
        return super.onCreateOptionsMenu(menu)
    }

    //Toolbarのitemにイベントを登録する
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when( id ){
            R.id.action_camera -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(packageManager)?.let{
                    if( checkCameraPermission() ) takePicture() else grantCameraPermission()
                }?: Toast.makeText(this,"カメラを扱うアプリがありません", Toast.LENGTH_SHORT).show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    //ファイルを保存する際のフォルダのチェックやファイルの名前付けをする
    private fun createSaveFileUri() : Uri {
        timeStamp = SimpleDateFormat("yyMMdd_HHmmss", Locale.JAPAN).format(Date())
        imageFileName = timeStamp

        //DCIMにcasalackがなければ作る
        val storageDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM + "/casalack")
        if(!storageDir.exists()){
            storageDir.mkdir()
        }

        val file = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )

        picPath = file.absolutePath
        return FileProvider.getUriForFile(this,"tokyo.mp015v.mycamera",file )
    }

    //リストにサムネイルを作成する
    private fun setListView( filter_path : String ){
        val selection = "_data like ?"
        val args = arrayOf("%${filter_path}%")
        val cursor = contentResolver.query( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null,selection,args,null)
        val listItem = ArrayList<ListItem>()

        //画像がない場合は何もしない
        if( cursor.count > 0 ) {

            cursor.moveToFirst()

            lateinit var path: String
            lateinit var fileName: String
            var size: Long

            do {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                val sizeColumn = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)
                size = cursor.getLong(sizeColumn)
                val id = cursor.getLong(cursor.getColumnIndex("_id"))
                val thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null)
                val item = ListItem(thumbnail, fileName, path, size)
                listItem.add(item)

            } while (cursor.moveToNext())

            cursor.close()
        }

        //リストビューのイベントを登録する
        findViewById<ListView>(R.id.listView).apply {
            adapter = ListAdapter(applicationContext, R.layout.list_item, listItem )

            setOnItemClickListener{parent, view, position, id ->
                val item = parent.getItemAtPosition(position) as ListItem
                Toast.makeText( applicationContext, item.path ,Toast.LENGTH_LONG ).show()

                val intent = Intent().apply{
                    setClassName( "tokyo.mp015v.mycamera","tokyo.mp015v.mycamera.Main3Activity")
                    putExtra("path", item.path )
                    putExtra("size", item.size)
                    putExtra( "curPath" , curPath)
                }
                //startActivity( intent )
                startActivityForResult(intent , Main3Activity_CODE)
            }
        }
    }

    //カメラを起動する
    private fun takePicture(){
        val intent = Intent( MediaStore.ACTION_IMAGE_CAPTURE).apply{
            addCategory(Intent.CATEGORY_DEFAULT)
            putExtra( MediaStore.EXTRA_OUTPUT,createSaveFileUri())
        }
        startActivityForResult( intent , Main2Activity.CAMERA_CODE)
    }

    //他のActivityから戻ってきたときの処理
    override fun onActivityResult( requestCode: Int,resultCode : Int,data:Intent?){
        //Log.d("***onActivityResult***" , "4:")
        //カメラから戻ってきたときの処理
        if( requestCode == Main2Activity.CAMERA_CODE && resultCode== Activity.RESULT_OK){
            val contentValues = ContentValues().apply{
                put(MediaStore.Images.Media.DISPLAY_NAME,imageFileName+".jpg")
                put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg")
                put("_data",picPath)
            }
            contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues
            )
        }else if( requestCode == Main3Activity_CODE && resultCode == Activity.RESULT_OK){
            //Main3Activityからの復帰
            this.curPath = data!!.getStringExtra("curPath")
        }
    }

    //カメラパーミッションのチェック
    private fun checkCameraPermission():Boolean{
        //カメラへのアクセス
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.CAMERA)
    }

    //ストレージパーミッションのチェック
    private fun checkStoragePermission():Boolean{
        //外部ストレージの書き込み
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    //カメラへのアクセス権を設定
    private fun grantCameraPermission() {
        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), Main2Activity.CAMERA_PERMISSION_CODE)
    }

    //外部ストレージへの書き込み権を設定
    private fun grantWriteStoragePermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Main2Activity.STORAGE_PERMISSION_CODE)
    }

    //許可ダイヤログの承認結果を受け取る
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        var isGranted = true

        when( requestCode ){
            Main2Activity.STORAGE_PERMISSION_CODE->{
                if( grantResults.isNotEmpty()){
                    grantResults.forEach{
                        if( it != PackageManager.PERMISSION_GRANTED){
                            isGranted = false
                        }
                    }
                }else{
                    isGranted = false
                }

                //ストレージパーミッションを許可した場合(はじめての処理になるはず)
                if( isGranted ){

                    //DCIMにcasalackがなければ作る
                    val storageDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DCIM + "/casalack")
                    if(!storageDir.exists()){
                        storageDir.mkdir()
                    }
                    //casalackのリストビューを表示する
                    setListView( "casalack" )


                }else{
                    grantWriteStoragePermission()
                }
            }

            Main2Activity.CAMERA_PERMISSION_CODE->{
                if( grantResults.isNotEmpty()){
                    grantResults.forEach{
                        if( it != PackageManager.PERMISSION_GRANTED){
                            isGranted = false
                        }
                    }
                }else{
                    isGranted = false
                }

                //カメラパーミッションを許可した場合
                if( isGranted ){
                    takePicture()
                }else{
                    grantCameraPermission()
                }
            }
        }
    }
}
