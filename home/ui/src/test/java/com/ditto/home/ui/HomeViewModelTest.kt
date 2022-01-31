package com.ditto.home.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.ditto.home.domain.HomeUsecase
import com.ditto.home.domain.model.MyLibraryDetailsDomain
import com.ditto.home.domain.model.ProdDomain
import com.ditto.mylibrary.domain.model.*
import com.ditto.storage.domain.StorageManager
import com.nhaarman.mockitokotlin2.verify
import core.appstate.AppState
import core.ui.common.Utility
import io.mockk.mockk
import non_core.lib.Result
import non_core.lib.error.NoNetworkError
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class HomeViewModelTest {
    @Mock
    lateinit var context: Context

    @Mock
    lateinit var storageManager: StorageManager

    @Mock
    lateinit var homeUsecase: HomeUsecase
    private lateinit var viewModel: HomeViewModel

    private val fakeDataPatternIdData = ArrayList<PatternIdData>()

    private val fakeDataPatternIdDataValidResponse = arrayListOf<PatternIdData>(
        PatternIdData(
            "", true, "", "", "1", "", "", "", "", "", "",
            NumberOfPiecesData(1, 2, 3), "", "", "", "", listOf(
                PatternPieceData(
                    true, "", "", 1, "", "", "", "", true, false, "", "", "", "",
                    listOf(SplicedImageData(1, "", 1, "", "", "", "", 0, 0)), ""
                )
            )
        ),
        PatternIdData(
            "", true, "", "", "1", "", "", "", "", "", "",
            NumberOfPiecesData(1, 2, 3), "", "", "", "", listOf(
                PatternPieceData(
                    true, "", "", 1, "", "", "", "", true, false, "", "", "", "",
                    listOf(SplicedImageData(1, "", 1, "", "", "", "", 0, 0)), ""
                )
            )
        )
    )

    private val fakeOfflinePatternData = ArrayList<OfflinePatternData>()

    private val fakeOfflinePatternDataValidResponse = arrayListOf<OfflinePatternData>(
        OfflinePatternData(
            "1",
            "",
            "",
            "",
            OfflineNumberOfCompletedPiece(1, 2, 3),
            listOf(OfflinePatternPieces(1, false)),
            mutableListOf<WorkspaceItemOfflineDomain>(
                WorkspaceItemOfflineDomain(
                    1, 1, true, 1.1f, 1.2f, 1.2f, 1.2f, "", "", 60.0f, true, false, true, "", 1, 2
                )
            ),
            mutableListOf<WorkspaceItemOfflineDomain>(
                WorkspaceItemOfflineDomain(
                    1, 1, true, 1.1f, 1.2f, 1.2f, 1.2f, "", "", 60.0f, true, false, true, "", 1, 2
                )
            ),
            mutableListOf<WorkspaceItemOfflineDomain>(
                WorkspaceItemOfflineDomain(
                    1, 1, true, 1.1f, 1.2f, 1.2f, 1.2f, "", "", 60.0f, true, false, true, "", 1, 2
                )
            ),
            "",
            "",
            "",
            "",
            OfflineNumberOfCompletedPiece(1, 2, 3),
            "22/2/2012",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            listOf(SelvageDomain("", 1, "", "", ""), SelvageDomain("", 2, "", "", "")),
            listOf(
                PatternPieceDataDomain(
                    true,
                    "",
                    "",
                    1,
                    "",
                    "",
                    "",
                    "",
                    false,
                    "",
                    "",
                    "",
                    "",
                    listOf(SplicedImageDomain(1, "", 1, "", "", "", "", 1, 2)),
                    "",
                    ""
                )
            ), "","","",true,"","",""
        )

    )
    private val fakeDataMyLibraryDetailsDomain = MyLibraryDetailsDomain(
        "",
        "",
        listOf(
            ProdDomain("1", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""),
            ProdDomain("2", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        ),
        "",
        2,
        0,
        1,
        ""
    )


    companion object {
        @ClassRule
        @JvmField
        val schedulerRule = RxImmediateSchedulerRule()// For Retrofit

        @ClassRule
        @JvmField
        var instantTaskExecutorRule = InstantTaskExecutorRule()//For LiveData


    }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        val utility = mock(Utility::class.java)
        viewModel = spy(HomeViewModel(context, storageManager, homeUsecase, utility))
    }

    @Test
    fun `when API call return No Network error, update ViewModel`() {
        viewModel.handleError(NoNetworkError())
        assertFalse(viewModel.activeInternetConnection.get())
    }

    @Test
    fun `when fetchData API call returned the valid response then update the viewModel`() {
        viewModel.handleFetchResult(Result.withValue(fakeDataMyLibraryDetailsDomain))
        assertEquals(2, viewModel.productCount)
    }

    @Test
    fun `when fetchData API call returned the empty response then update the viewModel`() {
        viewModel.handleFetchResult(Result.withValue(fakeDataMyLibraryDetailsDomain))
        assertNotEquals(0, viewModel.productCount)
    }

    @Test
    fun `when trial pattern Api returned the empty response then update viewModel`() {
        Mockito.doNothing().`when`(viewModel).fetchListOfTrialPatternFromInternalStorage()
        viewModel.handleTrialPatternResult(Result.withValue(fakeDataPatternIdData))
        assertEquals(0, viewModel.trialPatternData.size)
    }


    @Test
    fun `when trial pattern Api returned the valid response then update viewModel`() {
        Mockito.doNothing().`when`(viewModel).fetchListOfTrialPatternFromInternalStorage()
        viewModel.handleTrialPatternResult(Result.withValue(fakeDataPatternIdDataValidResponse))
        assertNotEquals(0, viewModel.trialPatternData.size)
    }

    @Test
    fun `when offline pattern returned the valid response then update viewModel`() {
        viewModel.handleOfflineFetchResult(Result.withValue(fakeOfflinePatternDataValidResponse))
        assertNotEquals(2, viewModel.trialPatternData.size)
    }

    @Test
    fun `when offline pattern returned the empty response then update viewModel`() {
        viewModel.handleOfflineFetchResult(Result.withValue(fakeOfflinePatternData))
        assertEquals(0, viewModel.trialPatternData.size)
    }

    @Test
    fun `when trial pattern Api returned the valid response when user is not logged in update viewModel`() {
        val appstate: AppState = spy(AppState::class.java)
        val sharedPrefs: SharedPreferences = mock(SharedPreferences::class.java)
        Mockito.`when`(context.getSharedPreferences(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt()
        )).thenReturn(sharedPrefs)
        appstate.init(context)
        Mockito.`when`(sharedPrefs.getBoolean(
            ArgumentMatchers.eq("logged"),
            ArgumentMatchers.anyBoolean()
        )).thenReturn(false)
        Mockito.doNothing().`when`(viewModel).fetchListOfTrialPatternFromInternalStorage()
        viewModel.handleTrialPatternResult(Result.withValue(fakeDataPatternIdDataValidResponse))
        assertNotEquals(0, viewModel.trialPatternData.size)
        verify(viewModel).fetchListOfTrialPatternFromInternalStorage()


    }

    @Test
    fun `when trial pattern Api returned the valid response when user logged in update viewModel`() {
        val appstate: AppState = spy(AppState::class.java)
        val sharedPrefs: SharedPreferences = mock(SharedPreferences::class.java)
        Mockito.`when`(context.getSharedPreferences(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyInt()
        )).thenReturn(sharedPrefs)
        appstate.init(context)
        Mockito.doNothing().`when`(viewModel).fetchData()
        Mockito.`when`(sharedPrefs.getBoolean(
            ArgumentMatchers.eq("logged"),
            ArgumentMatchers.anyBoolean()
        )).thenReturn(true)
        viewModel.handleTrialPatternResult(Result.withValue(fakeDataPatternIdDataValidResponse))
        assertNotEquals(0, viewModel.trialPatternData.size)
        verify(viewModel).fetchData()
    }

}