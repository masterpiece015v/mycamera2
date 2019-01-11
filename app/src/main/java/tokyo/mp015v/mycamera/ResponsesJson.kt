package tokyo.mp015v.mycamera

data class ResponsesJson(
    val responses: List<Response>
)

data class Response(
    val faceAnnotations: List<FaceAnnotation>
)

data class FaceAnnotation(
    val angerLikelihood: Double,
    val blurredLikelihood: Double,
    val boundingPoly: BoundingPoly,
    val detectionConfidence: Double,
    val fdBoundingPoly: FdBoundingPoly,
    val headwearLikelihood: Double,
    val joyLikelihood: Double,
    val landmarkingConfidence: Double,
    val landmarks: List<Landmark>,
    val panAngle: Double,
    val rollAngle: Double,
    val sorrowLikelihood: Double,
    val surpriseLikelihood: Double,
    val tiltAngle: Double,
    val underExposedLikelihood: Double
)

data class BoundingPoly(
    val vertices: List<Vertice>
)

data class Vertice(
    val x: Int,
    val y: Int
)

data class FdBoundingPoly(
    val vertices: List<Vertice>
)

data class Landmark(
    val position: Position,
    val type: String
)

data class Position(
    val x: Double,
    val y: Double,
    val z: Double
)