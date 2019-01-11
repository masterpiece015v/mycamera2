package tokyo.mp015v.mycamera

class RequestsJson(requests:Array<RequestJson>){
    val requests = requests
}
class RequestJson(image:ImageJson,features:Array<FeatureJson>){
    val image = image
    val features = features

}
class ImageJson(content:String){
    val content = content
}

class FeatureJson(type : String, maxResults : Int){
    val type = type
    val maxResults = maxResults
}

