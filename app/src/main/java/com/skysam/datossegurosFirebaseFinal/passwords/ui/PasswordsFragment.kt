package com.skysam.datossegurosFirebaseFinal.passwords.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.model.PasswordsModel
import com.skysam.datossegurosFirebaseFinal.databinding.PasswordsFragmentBinding
import java.util.*

class PasswordsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var fab: FloatingActionButton
    private var _binding: PasswordsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val passwords: MutableList<PasswordsModel> = mutableListOf()
    private val listSearch: MutableList<PasswordsModel> = mutableListOf()
    private lateinit var adapter: PasswordsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = PasswordsFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = PasswordsAdapter(passwords)
        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)
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
        viewModel.passwords.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                passwords.addAll(it)
                adapter.updateList(passwords.toMutableList())
                binding.recycler.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.tvSinLista.visibility = View.GONE
            } else {
                binding.recycler.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.tvSinLista.visibility = View.VISIBLE
            }
        })

        fab = requireActivity().findViewById(R.id.fab)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val itemSearch = menu.findItem(R.id.menu_buscar)
        val search = itemSearch.actionView as SearchView
        search.setOnQueryTextListener(this)
        search.setOnCloseListener {
            binding.lottieAnimationView.visibility = View.GONE
            false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onResume() {
        super.onResume()
        fab.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            fab.setImageResource(R.drawable.ic_add_contrasena)
            fab.show()
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
        if (passwords.isEmpty()) {
            Toast.makeText(requireContext(), "No hay lista cargada", Toast.LENGTH_SHORT).show()
        } else {
            val userInput = newText!!.toLowerCase(Locale.ROOT)
            listSearch.clear()

            for (password in passwords) {
                if (password.servicio.toLowerCase(Locale.ROOT).contains(userInput) ||
                        password.usuario.toLowerCase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(password)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            }
            adapter.updateList(listSearch.toList())
        }
        return false
    }
}