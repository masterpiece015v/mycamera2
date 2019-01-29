package tokyo.mp015v.mycamera

import android.util.Log

class DirMap{
    val map = HashMap<String,DirProperty>()

    fun putPath( abPath : String){

        val abPaths = abPath.split( "/" )
        var key = "/"
        for( i in 1..abPaths.size){
            key = key + abPaths[i]
        }
        val imgFileName = abPaths[ abPaths.size - 1]

        Log.d( "***DirMap***","key=" + key )

        //マップが空ではない
        if( !map.isEmpty() ){
            //マップにkeyが存在する
            if( map.containsKey(key) ){

            }else{

            }

        }
    }
}

class DirProperty{
    var count : Int = 0
    var imgFileNameList = ArrayList<String>()
}