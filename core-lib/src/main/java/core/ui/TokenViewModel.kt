package core.ui


import core.domain.GetTokenUseCase
import javax.inject.Inject

class TokenViewModel @Inject constructor(
    private val tokenUseCase: GetTokenUseCase
)  : BaseViewModel() {

}