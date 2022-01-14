package com.ditto.login.ui

import android.content.Context
import com.ditto.logger.LoggerFactory
import com.ditto.login.domain.model.CBodyDomain
import com.ditto.login.domain.model.GetLoginDbUseCase
import com.ditto.login.domain.model.LandingContentDomain
import com.ditto.storage.domain.StorageManager
import core.event.UiEvents
import core.ui.common.Utility
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import non_core.lib.error.UnknownError
import org.hamcrest.CoreMatchers.`is`
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

    @Mock
    lateinit var view: LoginFragment
    private lateinit var viewModel: LoginViewModel
    private lateinit var loggerFactory: LoggerFactory

    @Mock
    private lateinit var uiEvents: UiEvents<LoginViewModel.Event>
    private lateinit var testScheduler: TestScheduler

    private val fakeData = LandingContentDomain(
        "",
        "",
        CBodyDomain("", "", "", "fakeImageUrl", "hello::Aparna"),
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
        uiEvents = spy(UiEvents<LoginViewModel.Event>()::class.java)
        viewModel =
            spy(LoginViewModel(context, loggerFactory, getLoginDbUseCase, storageManager, utility))
    }

    @Test
    fun `when landing details call returned valid response, then update view model`() {
        viewModel.handleLandingScreenFetchDetails(
            Result.withValue(fakeData)
        )
        ignoreStubs(loggerFactory)
        // verify(uiEvents).post(LoginViewModel.Event.OnHideProgress)
        assertThat(viewModel.videoUrl, `is`("hello::Aparna"))
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
        val response = Single.just(Result.withValue<LandingContentDomain>(fakeData))
        `when`(getLoginDbUseCase.getLandingContentDetails()).thenReturn(response)
        viewModel.getLandingScreenDetails()
        ignoreStubs(loggerFactory)
        assertEquals("hello::Aparna", viewModel.videoUrl)
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
}