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

    //絶対パスのArrayListを返却する
    fun getAbPathArrayList() : ArrayList<String>{
        val list = ArrayList<String>()

        for (key in map.keys) {
            list.add( key )
        }

        return list
    }

    //絶対パス直下の画像ファイル名のArrayListを返却する
    fun getImgFileNameArrayList( key : String) : ArrayList<String>{
        val list = map.get(key)!!.imgFileNameList
        return list
    }

    //絶対パス直下の画像数を返却する
    fun getCount( key :String ) : Int{
        return map.get(key)!!.count

    }
}

class DirProperty{
    var count : Int = 0
    var imgFileNameList = ArrayList<String>()
}