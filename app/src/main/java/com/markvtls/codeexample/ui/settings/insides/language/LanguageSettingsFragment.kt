package com.wallpaperscraft.keby.app.ui.settings.insides.language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.wallpaperscraft.keby.app.ui.models.LanguageOption
import com.wallpaperscraft.keby.app.ui.settings.SettingsViewModel
import com.wallpaperscraft.keby.app.ui.shared.fragments.ActionDialogFragment
import com.wallpaperscraft.keby.app.ui.shared.fragments.ActionDialogFragment.Companion.DIALOG_TYPE
import com.wallpaperscraft.keby.app.ui.shared.fragments.ActionDialogFragment.Companion.TAG
import com.wallpaperscraft.keby.app.utils.getDownloadedLanguagesDebugList
import com.wallpaperscraft.keby.app.utils.getLanguageOptionsList
import com.wallpaperscraft.keby.databinding.FragmentSettingsLanguageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageSettingsFragment: Fragment() {

    private var _binding: FragmentSettingsLanguageBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels()

    private var downloadedAdapter: DownloadedLanguageAdapter? = null
    private var downloadAdapter: LanguageOptionAdapter? = null

    lateinit var mItemTouchHelperSelected: ItemTouchHelper

    private val debugList: ArrayList<LanguageOption> = getLanguageOptionsList()
    private val downloadedDebugList: ArrayList<String> = getDownloadedLanguagesDebugList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDownloadAdapter()
        downloadAdapter?.submitList(debugList)

        initDownloadedAdapter()
        downloadedAdapter?.setData(downloadedDebugList)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.apply {
            cancelMessageButton.setOnClickListener {
                binding.errorMessage.visibility = View.GONE
            }
            binding.root.setOnClickListener {
                binding.errorMessage.visibility = View.GONE
            }
        }
    }

    private fun initDownloadAdapter() {
        val downloadList = binding.downloadList
        downloadAdapter = LanguageOptionAdapter()
        downloadAdapter?.apply {
            setOnDownloadListener { url ->
                downloadLanguage(url)
            }
        }
        downloadList.adapter = downloadAdapter
        downloadList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initDownloadedAdapter() {
        downloadedAdapter = DownloadedLanguageAdapter()
        val languageList = binding.languageList
        downloadedAdapter?.apply {
            setOnRemovalListener { position -> downloadedDebugList.removeAt(position) }
            setRemovalDialogListener { onApprove, onDismiss ->
                callLanguageDeleteMessageDialog(onApprove, onDismiss)
            }
            val callback: ItemTouchHelper.Callback = ReorderHelperCallback(downloadedAdapter!!)
            mItemTouchHelperSelected = ItemTouchHelper(callback)
            mItemTouchHelperSelected.attachToRecyclerView(languageList)
        }
        languageList.adapter = downloadedAdapter
        languageList.layoutManager = LinearLayoutManager(requireContext())
    }

        //TODO: Переписать под новую имплементацию
//    private fun checkIfTheAtLeastOneLanguageIsActive(newValue: Boolean): Boolean {
//        var numberOfActiveLanguages = 0
//
//        val availableLanguages = arrayOf(SettingsKeys.ENGLISH_LANGUAGE, SettingsKeys.RUSSIAN_LANGUAGE)
//
//        for (language in availableLanguages) {
//            if (settingsViewModel.isEnabled(language)) {
//                numberOfActiveLanguages += 1
//            }
//        }
//
//        if (!newValue) {
//            if (numberOfActiveLanguages - 1 == 0) {
//                return false
//            }
//        }
//        return true
//    }

    private fun downloadLanguage(url: String) {
        //TODO: Скачивание языков
        val isDownloadSuccessful = true
        val newLanguage = "Gibberish"
        if (isDownloadSuccessful) {
            downloadedDebugList.add(newLanguage) //Наверное нужно делать это в VM
            downloadedAdapter?.setData(arrayListOf(newLanguage))
        } else {
            //TODO: Обработка ошибок
        }
    }

    private fun callLanguageDeleteMessageDialog(onApprove: () -> Unit, onDismiss: () -> Unit) {
        val dialog = ActionDialogFragment()
        val args = Bundle()
        dialog.setOnDialogActionListener {
            onApprove.invoke()
            dialog.dismiss()
        }
        dialog.setOnDialogDismissListener {
            onDismiss.invoke()
        }
        args.putSerializable(DIALOG_TYPE, ActionDialogFragment.Companion.ActionType.REMOVAL)
        dialog.arguments = args
        dialog.show(requireActivity().supportFragmentManager, TAG)
    }
}