package tokyo.mp015v.mycamera

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject

class RequestsJson(jsonString:String){
    val root = Json.parse( jsonString ).asObject()
    val responses = root.get("responses").asArray()
    val response = responses[0] as JsonObject
    val faceAnnotations = FaceAnnotations( response.asArray() )

}

class FaceAnnotations( ){
    val faceAnnotations :MutableList<FaceAnnotation> = mutableListOf()

    constructor( jsonArray : JsonArray):this(){
        jsonArray.forEach{
            faceAnnotations.add( FaceAnnotation((it as JsonObject)))
        }
    }
}

class FaceAnnotation(jsonObject:JsonObject){
    val boundingPoly = jsonObject.get("boundingPoly").asObject()
    val fdBoundingPoly = jsonObject.get("fdBoundingPoly").asObject()

    val rollAngle = mapOf("rollAngle" to jsonObject.get("rollAngle").asDouble())
    val panAngle = mapOf("panAngle" to jsonObject.get("panAngle").asDouble())
    val tiltAngle = mapOf("tiltAngle" to jsonObject.get("tiltAngle").asDouble() )
    val detectionConfidence = mapOf("detectionConfidence" to jsonObject.get("detectionConfidence").asDouble() )
    val landmarkingConfidence = mapOf( "landmarkingConfidence" to jsonObject.get("landmarkingConfidence").asDouble() )
    val joyLikelihood = mapOf( "joyLikelihood" to jsonObject.get("joyLikelihood").asString() )
    val sorrowLikelihood = mapOf( "sorrowLikelihood" to jsonObject.get("sorrowLikelihood").asString() )
    val angerLikelihood = mapOf("angerLikelihood" to jsonObject.get("angerLikelihood").asString() )
    val surpriseLikelihood = mapOf( "surpriseLikelohood" to jsonObject.get("surpriseLikelihood").asString() )
    val underExposedLikelihood = mapOf( "underExposedLikelihood" to jsonObject.get("underExposedLikelihood").asString() )
    val blurredLikelihood = mapOf( "blurredLikelihood" to jsonObject.get("blurredLikelihood").asString() )
    val headwearLikelihood = mapOf( "headwearLikelihood" to jsonObject.get("headwearLikelihood").asString() )
}