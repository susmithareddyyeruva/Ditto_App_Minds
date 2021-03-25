package com.ditto.mylibrary.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.ditto.logger.Logger
import com.ditto.logger.LoggerFactory

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.my_library_fragment.*
import trace.mylibrary.domain.model.MyLibraryData
import com.ditto.mylibrary.ui.adapter.ActiveProjectAdapter
import core.ui.BaseFragment
import core.ui.ViewModelDelegate
import trace.mylibrary.ui.R
import trace.mylibrary.ui.databinding.ActiveProjectsFragmentBinding
import javax.inject.Inject

class ActiveProjectsFragment : BaseFragment() {

    @Inject
    lateinit var loggerFactory: LoggerFactory

    val logger: Logger by lazy {
        loggerFactory.create(ActiveProjectsFragment::class.java.simpleName)
    }

    private val viewModel: AllPatternsViewModel by ViewModelDelegate()
    lateinit var binding: ActiveProjectsFragmentBinding
    private var patternId: Int = 0

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        binding = ActiveProjectsFragmentBinding.inflate(
            inflater
        ).also {
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUIEvents()
        viewModel.fetchOnPatternData()
     }

    private fun setUIEvents() {
        viewModel.disposable += viewModel.events
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                handleEvent(it)
            }
    }

    private fun setPatternAdapter() {
        //val adapter = PatternAdapter()
        val adapter = ActiveProjectAdapter()
        binding.activeProjectsList.adapter = adapter
        adapter.viewModel = viewModel
        val patternData: List<MyLibraryData>? =
            viewModel.data.value?.filter { it.status == "Active" }
        if (patternData?.isNotEmpty() == true) {
            binding.emptyLayout.visibility = View.GONE
            binding.activeProjectsList.visibility = View.VISIBLE
        }

        //for sorting
        /*Collections.sort(patternData,
            Comparator<MyLibraryData?> { lhs, rhs -> lhs!!.id.compareTo(rhs!!.id) })*/

        patternData?.let { adapter.setListData(it) }
    }

    private fun handleEvent(event: AllPatternsViewModel.Event) =
        when (event) {

            is AllPatternsViewModel.Event.OnItemClick -> {
                if (findNavController().currentDestination?.id == R.id.myLibraryFragment) {
                    val bundle = bundleOf("clickedID" to viewModel.clickedId.get())
                    findNavController().navigate(
                        R.id.action_allPatternsFragment_to_patternDescriptionFragment,
                        bundle
                    )
                } else {
                    logger.d("OnClickPatternDesc failed")
                }
            }

            is AllPatternsViewModel.Event.OnDataUpdated -> {
                setPatternAdapter()
            }

            is AllPatternsViewModel.Event.OnAddProjectClick -> {
                val frag: MyLibraryFragment? = this.parentFragment as MyLibraryFragment?
                frag?.view_pager?.setCurrentItem(2)
            }

            is AllPatternsViewModel.Event.OnOptionsClicked ->
                showPopupMenu(event.view, event.patternId)

        }

    private fun showPopupMenu(view: View, patternId: Int) {
        this.patternId = patternId
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.actions, popup.menu)
        popup.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_remove -> {
                viewModel.removePattern(patternId)
            }
            R.id.menu_complete -> {
                viewModel.updateProjectComplete(patternId)
            }
            R.id.menu_rename -> {
                renameProject()
            }
            R.id.menu_details -> {

            }
        }
        return true
    }

    private fun renameProject() {
        val layout =
            activity?.layoutInflater?.inflate(R.layout.library_save_and_exit_dialog, null)
        layout?.let {
            getAlertDialogSaveAndExit(
                requireActivity(),
                resources.getString(R.string.save_and_exit_dialog_title),
                "viewModel.data.value?.patternName"
                    ?: resources.getString(R.string.save_and_exit_dialog_message),
                it,
                resources.getString(R.string.exit),
                resources.getString(R.string.save)
            )
        }
    }

    private fun getAlertDialogSaveAndExit(
        context: Context,
        title: String,
        hintName: String,
        view: View,
        negativeButton: String,
        positiveButton: String
    ) {
        val edittext = view.findViewById(R.id.project_name) as EditText
        edittext.setText(hintName)
        val dpi: Float = context.resources.displayMetrics.density
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder
            .setCancelable(false)
            .setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
                //callback.onSaveButtonClicked(edittext.text.toString())
            })
            .setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
                //callback.onExitButtonClicked()
            })

        val alert = dialogBuilder.create()
        alert.setTitle(title)
        alert.setView(
            view,
            ((19 * dpi).toInt()),
            ((0 * dpi).toInt()),
            ((14 * dpi).toInt()),
            ((0 * dpi).toInt())
        );
        alert.show()
    }

}