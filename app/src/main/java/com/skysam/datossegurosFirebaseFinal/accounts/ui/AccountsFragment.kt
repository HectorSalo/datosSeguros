package com.skysam.datossegurosFirebaseFinal.accounts.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.AccountModel
import com.skysam.datossegurosFirebaseFinal.databinding.AccountFragmentBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddActivity
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainViewModel
import java.util.*

class AccountsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: AccountFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fab: FloatingActionButton
    private val viewModel: MainViewModel by activityViewModels()
    private val accounts: MutableList<AccountModel> = mutableListOf()
    private val listSearch: MutableList<AccountModel> = mutableListOf()
    private lateinit var adapter: AccountAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = AccountFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = AccountAdapter(accounts.toList())
        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)
        viewModel.accountsFirestore.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                accounts.clear()
                accounts.addAll(it)
                adapter.updateList(accounts.toList())
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
        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.AGREGAR, 1)
            val intent = Intent(requireContext(), AddActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fab.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            fab.setImageResource(R.drawable.ic_add_banco)
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
        },150)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (accounts.isNotEmpty()) {
            val userInput = newText!!.toLowerCase(Locale.ROOT)
            listSearch.clear()

            for (account in accounts) {
                if (account.banco.toLowerCase(Locale.ROOT).contains(userInput) ||
                        account.titular.toLowerCase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(account)
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
}