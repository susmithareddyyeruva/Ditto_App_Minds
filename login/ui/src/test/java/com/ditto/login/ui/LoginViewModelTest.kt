package com.ditto.login.ui

import android.content.Context
import com.ditto.logger.LoggerFactory
import com.ditto.login.data.mapper.mapResponseToModel
import com.ditto.login.domain.model.CBodyDomain
import com.ditto.login.domain.model.GetLoginDbUseCase
import com.ditto.login.domain.model.LandingContentDomain
import com.ditto.login.domain.model.LoginResultDomain
import com.ditto.storage.domain.StorageManager
import core.ui.common.Utility
import io.reactivex.Single
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import non_core.lib.error.UnknownError
import org.junit.Assert.*
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var getLoginDbUseCase: GetLoginDbUseCase

    @Mock
    lateinit var storageManager: StorageManager
    private lateinit var viewModel: LoginViewModel
    private lateinit var loggerFactory: LoggerFactory

    private val fakeData = LandingContentDomain(
        "",
        "",
        CBodyDomain("", "", "", "fakeImageUrl", "fakeVideoUrl"),
        "",
        ""
    )

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        loggerFactory = spy(LoggerFactory::class.java)
        val utility = mock(Utility::class.java)
        viewModel =
            spy(LoginViewModel(context, loggerFactory, getLoginDbUseCase, storageManager, utility))
        ignoreStubs(loggerFactory)
    }

    @Test
    fun `when landing details call returned valid response, then update view model`() {
        viewModel.handleLandingScreenFetchDetails(
            Result.withValue(fakeData)
        )
        ignoreStubs(loggerFactory)
        assertEquals("fakeVideoUrl",viewModel.videoUrl)
        assertEquals("fakeImageUrl",viewModel.imageUrl.get())
    }

    @Test
    fun `when landing details call returned no network error, then update view model`() {
        viewModel.handleError(
            NoNetworkError()
        )
        assertFalse(viewModel.activeInternetConnection.get())
    }

    @Test
    fun `when landing details requested then return valid response`() {
        val response = Single.just(Result.withValue(fakeData))
        `when`(getLoginDbUseCase.getLandingContentDetails()).thenReturn(response)
        viewModel.getLandingScreenDetails()
        ignoreStubs(loggerFactory)
        assertEquals("fakeVideoUrl", viewModel.videoUrl)
    }

    @Test
    fun `when landing details requested with no internet connection, return no network error`() {
        val errorResponse = Single.just(Result.withError<LandingContentDomain>(NoNetworkError()))
        `when`(getLoginDbUseCase.getLandingContentDetails()).thenReturn(errorResponse)
        viewModel.getLandingScreenDetails()
        assertFalse(viewModel.activeInternetConnection.get())
    }

    @Test
    fun `when landing details requested with unknown error, then return unknown error message`() {
        val errorResponse = Single.just(Result.withError<LandingContentDomain>(UnknownError()))
        `when`(getLoginDbUseCase.getLandingContentDetails()).thenReturn(errorResponse)
        viewModel.getLandingScreenDetails()
        assertEquals("Unknown Error", viewModel.errorString.get())

    }

    @Test
    fun `when user login is requested with no internet connection, return no network error`() {
        val errorResponse = Single.just(Result.withError<LoginResultDomain>(NoNetworkError()))
        `when`(getLoginDbUseCase.loginUserWithCredential(com.nhaarman.mockitokotlin2.any())).thenReturn(errorResponse)
        ignoreStubs(loggerFactory)
        viewModel.makeUserLoginApiCall()
        assertFalse(viewModel.activeInternetConnection.get())
        assertEquals("No Internet connection available !", viewModel.errorString.get())
    }

    @Test
    fun `given valid user credentials when user login is requested then return valid response`() {
        viewModel.userName.set("test@test.com")
        viewModel.password.set("password")
        val fakeLoginResponse = mapResponseToModel(false)
        val response = Single.just(Result.withValue(fakeLoginResponse))
        `when`(getLoginDbUseCase.loginUserWithCredential(com.nhaarman.mockitokotlin2.any())).thenReturn(response)
        ignoreStubs(loggerFactory)
        doReturn(true).`when`(viewModel).isEmailValid()
        doNothing().`when`(viewModel).makeCreateUserCall(com.nhaarman.mockitokotlin2.any())
        viewModel.validateCredentials()
        assertTrue(viewModel.isPasswordValidated.get())
        assertTrue(viewModel.isEmailValidated.get())
        assertEquals("TestFirstName",viewModel.userFirstName)
        assertEquals("test@test.com", viewModel.userEmail)
        assertEquals("9999999999",viewModel.userPhone)
        assertEquals("testLastName",viewModel.userLastName)
    }

    @Test
    fun `given valid user credentials when user login is requested then return valid response with fault domain error`() {
        val fakeLoginResponse = mapResponseToModel(true)
        val response = Single.just(Result.withValue(fakeLoginResponse))
        `when`(getLoginDbUseCase.loginUserWithCredential(com.nhaarman.mockitokotlin2.any())).thenReturn(response)
        ignoreStubs(loggerFactory)
        viewModel.makeUserLoginApiCall()
        assertEquals("error found",viewModel.errorString.get())
        assertTrue(viewModel.isLoginButtonFocusable.get())
    }

    @Test
    fun `when user name is empty then do not make login call`() {
        viewModel.userName.set("")
        viewModel.validateCredentials()
        assertFalse(viewModel.isEmailValidated.get())
        verify(viewModel, never()).makeUserLoginApiCall()
    }
    @Test
    fun `when user name is null then do not make login call`() {
        viewModel.userName.set(null)
        viewModel.validateCredentials()
        assertFalse(viewModel.isEmailValidated.get())
        verify(viewModel, never()).makeUserLoginApiCall()
    }


    @Test
    fun `when user password is empty then do not make login call`() {
        viewModel.userName.set("test@test.com")
        viewModel.password.set("")
        viewModel.validateCredentials()
        assertFalse(viewModel.isPasswordValidated.get())
        verify(viewModel, never()).makeUserLoginApiCall()
    }

    @Test
    fun `when user password is null then do not make login call`() {
        viewModel.userName.set("test@test.com")
        viewModel.password.set(null)
        viewModel.validateCredentials()
        assertFalse(viewModel.isPasswordValidated.get())
        verify(viewModel, never()).makeUserLoginApiCall()
    }

    @Test
    fun `Test invalid user email format`() {
        viewModel.userName.set("test@test..")
        viewModel.isEmailValid()
        assertFalse(viewModel.isEmailValid())
    }

    @Test
    fun `test valid user email format`() {
        viewModel.userName.set("test@test.inc")
        viewModel.isEmailValid()
        assertTrue(viewModel.isEmailValid())
    }

}