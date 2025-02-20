package com.wallpaperscraft.keby.app.ui.home

import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.adapters.SnapPaginator
import com.wallpaperscraft.keby.app.ui.shared.adapters.ThemesFeedAdapter
import com.wallpaperscraft.keby.app.ui.views.BottomSheetDialogExtended
import com.wallpaperscraft.keby.app.ui.views.BottomSheetDialogExtended.Companion.DialogState
import com.wallpaperscraft.keby.app.ui.views.BottomSheetDialogExtended.Companion.FULL_NAME
import com.wallpaperscraft.keby.app.ui.views.BottomSheetDialogExtended.Companion.PACKAGE_NAME
import com.wallpaperscraft.keby.app.ui.views.BottomSheetDialogExtended.Companion.WELCOME_DIALOG_TYPE
import com.wallpaperscraft.keby.app.utils.getThemesDebugList
import com.wallpaperscraft.keby.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var feed: RecyclerView

    private lateinit var themesFeedAdapter: ThemesFeedAdapter
    private var snapPaginator: SnapPaginator? = null

    private var dialogState: DialogState = DialogState.HIDDEN

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        when {
            homeViewModel.checkIfFirstLaunch() -> {
                showWelcomeDialog(BottomSheetDialogExtended.Companion.DialogType.WELCOME)
                homeViewModel.saveFirstLaunch()
            }

            !checkIfKebyIsInList() -> {
                showWelcomeDialog(BottomSheetDialogExtended.Companion.DialogType.RESET_LIST)
            }

            !checkIfChosenKeyboardIsKeby() -> {
                showWelcomeDialog(BottomSheetDialogExtended.Companion.DialogType.RESET_KEYBOARD)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feed = binding.themesList
        val debugList = getThemesDebugList()
        initAdapter()
        themesFeedAdapter.submitList(debugList)

        with(binding) {
            swiperefresh.apply {
                setProgressBackgroundColorSchemeColor(resources.getColor(R.color.green))
                setColorSchemeColors(resources.getColor(R.color.black_2))
            }

            swiperefresh.setOnRefreshListener {
                // swiperefresh.isRefreshing = false
                //rebindSnapPaginate()
                // snapPaginator?.showLoading(true)
                snapPaginator?.showError(true, R.string.error_retry_message)
                //TODO
            }

            errorView.setErrorMessageText(R.string.error_retry_message)
            errorView.visibility = View.VISIBLE


        }
        rebindSnapPaginate()
    }

    private fun rebindSnapPaginate() {
        snapPaginator?.unbind()
        snapPaginator =
            createSnapPaginate(feed, { loadMoreThemes() }, { retryOnError() }, { checkForItems() })
    }

    private fun createSnapPaginate(
        recyclerView: RecyclerView,
        loadMore: () -> Unit,
        errorRetry: () -> Unit,
        noMoreItems: () -> Boolean
    ): SnapPaginator = SnapPaginator(
        recyclerView,
        loadMore,
        noMoreItems,
        LOADING_TRIGGER_THRESHOLD,
        R.layout.item_feed_loading,
        R.layout.item_feed_error,
        R.id.button_error_retry,
        R.id.error_message,
        errorRetry
    )

    private fun loadMoreThemes() {
        //TODO
    }

    private fun retryOnError() {
        //TODO
    }

    private fun checkForItems(): Boolean {
        //TODO
        return true
    }

    private fun initAdapter() {
        val recyclerView = binding.themesList
        themesFeedAdapter = ThemesFeedAdapter(
            onItemClicked = { theme ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(theme.themeId)
                findNavController().navigate(action)
            }, context = requireContext()
        )
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = themesFeedAdapter
    }

    private fun showWelcomeDialog(type: BottomSheetDialogExtended.Companion.DialogType) {
        if (dialogState == DialogState.HIDDEN) {
            val dialog = BottomSheetDialogExtended()
            val args = Bundle()
            args.putSerializable(WELCOME_DIALOG_TYPE, type)
            dialog.arguments = args
            dialog.setDialogStateListener {
                dialogState = DialogState.HIDDEN
            }
            dialog.show(requireActivity().supportFragmentManager, "")
            dialogState = DialogState.EXPANDED
        }
    }

    private fun checkIfChosenKeyboardIsKeby(): Boolean {
        val currentInputMethod = Settings.Secure.getString(
            context?.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD
        )
        return currentInputMethod == FULL_NAME
    }

    private fun checkIfKebyIsInList(): Boolean {
        val inputManager = getSystemService(requireContext(), InputMethodManager::class.java)
        val isKebySelected =
            inputManager!!.enabledInputMethodList.find { it.packageName == PACKAGE_NAME } != null
        return isKebySelected
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val LOADING_TRIGGER_THRESHOLD = 2
    }
}