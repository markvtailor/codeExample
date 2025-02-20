package com.wallpaperscraft.keby.app.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.wallpaperscraft.keby.R
import com.wallpaperscraft.keby.app.ui.favorites.adapter.DownloadedThemesAdapter
import com.wallpaperscraft.keby.app.ui.models.DownloadedTheme
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment
import com.wallpaperscraft.keby.app.utils.getDownloadedThemesDebugList
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment.Companion.DIALOG_TYPE
import com.wallpaperscraft.keby.app.ui.shared.fragments.BaseDialogFragment.Companion.TAG
import com.wallpaperscraft.keby.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var themes: RecyclerView
    private lateinit var recyclerViewAdapter: DownloadedThemesAdapter

    private val debugList = getDownloadedThemesDebugList().toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        themes = binding.themesList



        initAdapter()
        recyclerViewAdapter.submitList(debugList)
        //TODO: Check if the list is empty
        binding.buttonStartRemove.setOnClickListener {

            val newList = mutableListOf<DownloadedTheme>()

            debugList.forEach {
                newList.add(
                    DownloadedTheme(
                        it.themeId, it.themeUrl, it.isActive, true
                    )
                )
            }


            recyclerViewAdapter.submitList(newList)
            binding.buttonStartRemove.visibility = View.GONE
            binding.buttonFinishRemove.visibility = View.VISIBLE
        }
        binding.buttonFinishRemove.setOnClickListener {

            recyclerViewAdapter.submitList(debugList)
            binding.buttonStartRemove.visibility = View.VISIBLE
            binding.buttonFinishRemove.visibility = View.GONE
        }

    }

    private fun initAdapter() {
        val recyclerView = binding.themesList
        recyclerViewAdapter = DownloadedThemesAdapter(
            onItemClicked = { theme ->
                onItemClickAction(theme)
            },

            onRemoveItemButtonClicked = { id ->
                deleteFromList(id)
            }, context = requireContext()
        )
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun onItemClickAction(theme: DownloadedTheme) {
        if (binding.buttonFinishRemove.isVisible) {
            if (theme.isActive) {
                callMessageDialog()
            }
        } else {
            navigateToDetails(theme.themeId)
        }
    }

    private fun navigateToDetails(themeId: Int) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(themeId)
        findNavController().navigate(action)
    }

    private fun callMessageDialog() {
        val dialog = BaseDialogFragment()
        val args = Bundle()
        args.putSerializable(DIALOG_TYPE, BaseDialogFragment.Companion.DialogType.REMOVAL_MESSAGE)
        dialog.arguments = args
        dialog.show(requireActivity().supportFragmentManager, TAG)

    }

    private fun deleteFromList(id: Int) {
        debugList.removeAll { it.themeId == id }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}