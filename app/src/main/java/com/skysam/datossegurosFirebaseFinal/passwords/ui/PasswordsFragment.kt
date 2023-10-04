package com.skysam.datossegurosFirebaseFinal.passwords.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.Password
import com.skysam.datossegurosFirebaseFinal.databinding.PasswordsFragmentBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddActivity
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainViewModel
import java.util.Locale

class PasswordsFragment : Fragment(), SearchView.OnQueryTextListener, MenuProvider {

    private lateinit var fab: FloatingActionButton
    private var _binding: PasswordsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val passwordsFirestore: MutableList<Password> = mutableListOf()
    private val passwords: MutableList<Password> = mutableListOf()
    private val listSearch: MutableList<Password> = mutableListOf()
    private val labels = mutableListOf<String>()
    private val labelsToFilter = mutableListOf<String>()
    private lateinit var adapter: PasswordsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = PasswordsFragmentBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PasswordsAdapter(passwords.toList())
        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)

        fab = requireActivity().findViewById(R.id.fab)
        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.AGREGAR, 0)
            val intent = Intent(requireContext(), AddActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        loadViewModel()
    }


    override fun onResume() {
        super.onResume()
        loadPasswords()
        loadLabels()
        fab.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            fab.setImageResource(R.drawable.ic_add_contrasena)
            fab.show()
            binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }, 150)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (passwordsFirestore.isNotEmpty()) {
            binding.chipGroup.removeAllViews()
            val userInput = newText!!.lowercase(Locale.ROOT)
            listSearch.clear()

            for (password in passwordsFirestore) {
                if (password.service.lowercase(Locale.ROOT).contains(userInput) ||
                    password.user.lowercase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(password)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapter.updateList(listSearch.toList())
        }
        return false
    }

    private fun loadPasswords() {
        passwords.clear()
        passwords.addAll(passwordsFirestore)
        if (passwords.isNotEmpty()) {
            adapter.updateList(passwords.sortedWith(compareBy { it.service }).toList())
            binding.recycler.visibility = View.VISIBLE
            binding.tvSinLista.visibility = View.GONE
        } else {
            binding.recycler.visibility = View.GONE
            binding.tvSinLista.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun loadViewModel() {
        viewModel.labels.observe(viewLifecycleOwner) {
            if (_binding != null) {
                labels.clear()
                labels.addAll(it)
                loadLabels()
            }
        }
        viewModel.passwordsFirestore.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                passwordsFirestore.clear()
                passwordsFirestore.addAll(it)
                loadPasswords()
            }
        }
    }

    private fun loadLabels() {
        binding.chipGroup.removeAllViews()
        for (label in labels) {
            val chip = Chip(requireContext())
            chip.text = label
            chip.isCheckable = true
            chip.isClickable = true
            chip.setChipBackgroundColorResource(getColorPrimary())
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_text_white))
            chip.setOnClickListener {
                if (chip.isChecked) {
                    labelsToFilter.add(label)
                } else {
                    labelsToFilter.remove(label)
                }
                filterLabel()
            }

            binding.chipGroup.addView(chip)
        }
    }

    private fun filterLabel() {
        val itemsToShow = mutableListOf<Password>()
        for (label in labelsToFilter) {
            for (item in passwordsFirestore) {
                if (item.labels.contains(label) && !itemsToShow.contains(item)) itemsToShow.add(item)
            }
        }
        if (labelsToFilter.isEmpty()) loadPasswords() else adapter.updateList(itemsToShow)
    }

    private fun getColorPrimary(): Int {
        val typedValue = TypedValue()
        requireActivity().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        return typedValue.resourceId
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        val itemSearch = menu.findItem(R.id.menu_buscar)
        val search = itemSearch.actionView as SearchView
        search.setOnQueryTextListener(this)
        search.setOnCloseListener {
            binding.lottieAnimationView.visibility = View.GONE
            labelsToFilter.clear()
            loadLabels()
            false
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return true
    }
}