package core.data

data class TokenResultDomain(
    val access_token : String?,
    val expires_in : Int?,
    val token_type : String?,
    val error : String?,
    val error_description : String?
)