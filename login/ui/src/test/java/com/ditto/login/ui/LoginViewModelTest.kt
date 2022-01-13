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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
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
        CBodyDomain("", "", "", "", "hello::Aparna"),
        "",
        ""
    )

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testScheduler = TestScheduler()
        loggerFactory = spy(LoggerFactory::class.java)
        val utility = mock(Utility::class.java)
        uiEvents = spy(UiEvents<LoginViewModel.Event>()::class.java)
        viewModel =
            spy(LoginViewModel(context, loggerFactory, getLoginDbUseCase, storageManager, utility))
    }

    @Test
    fun `test handle landing screen fetch details`() {

        viewModel.handleLandingScreenFetchDetails(
            Result.withValue(fakeData)
        )
        Mockito.ignoreStubs(loggerFactory)
        // verify(uiEvents).post(LoginViewModel.Event.OnHideProgress)
        assertEquals("hello::Aparna", viewModel.videoUrl)
    }

    @Test
    fun `test error when no internet connection`() {
        viewModel.handleError(
            NoNetworkError()
        )
        assertTrue(viewModel.activeInternetConnection.get())
    }

    @Test
    fun `test landing screen details call`() {
        val response = Single.just(Result.withValue(fakeData))
        Mockito.`when`(getLoginDbUseCase.getLandingContentDetails()).thenReturn(response)
        viewModel.getLandingScreenDetails()
        testScheduler.triggerActions()
        Mockito.ignoreStubs(loggerFactory)
        assertEquals("hello::Aparna", viewModel.videoUrl)
    }
}