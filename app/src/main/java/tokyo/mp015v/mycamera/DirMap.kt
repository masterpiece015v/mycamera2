package tokyo.mp015v.mycamera

import android.util.Log

class DirMap{
    val map = HashMap<String,DirProperty>()

    //パスを入れるとマップを構成する
    fun putPath( abPath : String){
        val abPaths = abPath.split( "/" )
        var key = "/" + abPaths[1]
        for( i in 2..abPaths.size-2){
            key = key + "/" + abPaths[i]
        }
        val imgFileName = abPaths[ abPaths.size - 1]

        //マップが空ではない
        if( !map.isEmpty() ){
            //マップにkeyが存在する
            if( map.containsKey(key) ){
                val pro = map.get(key)
                pro!!.count += 1
                pro!!.imgFileNameList.add( imgFileName )
                map.put( key , pro )
            }else{
                val pro = DirProperty()
                pro!!.count = 1
                pro!!.imgFileNameList.add( imgFileName )
                map.put( key , pro )
            }
        }else{
            val pro = DirProperty()
            pro!!.count = 1
            pro!!.imgFileNameList.add( imgFileName )
            map.put( key , pro )
        }
    }
}

class DirProperty{
    var count : Int = 0
    var imgFileNameList = ArrayList<String>()
}