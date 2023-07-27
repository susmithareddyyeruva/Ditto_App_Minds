package core.models

data class Nsdservicedata(
    val nsdServiceName: String,
    var nsdSericeHostAddress: String,
    val nsdServicePort: Int,
    var isConnected : Boolean,
    var nsdMacAddress: String,
    var nsdVersion: String
    )