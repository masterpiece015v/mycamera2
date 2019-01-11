package tokyo.mp015v.mycamera

data class RequestsJson(
    val requests: List<Request>
)

data class Request(
    val features: List<Feature>,
    val image: Image
)

data class Image(
    val content: String
)

data class Feature(
    val maxResults: Int,
    val type: String
)