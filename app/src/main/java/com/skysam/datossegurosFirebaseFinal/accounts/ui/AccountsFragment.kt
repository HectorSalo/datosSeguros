package com.skysam.datossegurosFirebaseFinal.accounts.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.Account
import com.skysam.datossegurosFirebaseFinal.databinding.AccountFragmentBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddActivity
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainViewModel
import java.util.Locale

class AccountsFragment : Fragment() {

    private var _binding: AccountFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fab: FloatingActionButton
    private val viewModel: MainViewModel by activityViewModels()
    private val accountsFirestore: MutableList<Account> = mutableListOf()
    private val accounts: MutableList<Account> = mutableListOf()
    private val listSearch: MutableList<Account> = mutableListOf()
    private lateinit var adapter: AccountAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = AccountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider, SearchView.OnQueryTextListener {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                val itemSearch = menu.findItem(R.id.menu_buscar)
                val search = itemSearch.actionView as SearchView
                search.setOnQueryTextListener(this)
                search.setOnCloseListener {
                    binding.lottieAnimationView.visibility = View.GONE
                    false
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (accountsFirestore.isNotEmpty()) {
                    val userInput = newText!!.lowercase(Locale.ROOT)
                    listSearch.clear()

                    for (account in accountsFirestore) {
                        if (account.bank.lowercase(Locale.ROOT).contains(userInput) ||
                            account.user.lowercase(Locale.ROOT).contains(userInput)) {
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
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        adapter = AccountAdapter(accountsFirestore.toList())
        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)

        fab = requireActivity().findViewById(R.id.fab)
        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.AGREGAR, 1)
            val intent = Intent(requireContext(), AddActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        loadViewModel()
    }

    override fun onResume() {
        super.onResume()
        loadPasswords()
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

    private fun loadViewModel() {
        viewModel.accountsFirestore.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                accountsFirestore.clear()
                accountsFirestore.addAll(it)
                loadPasswords()
            }
        }
    }

    private fun loadPasswords() {
        accounts.clear()
        accounts.addAll(accountsFirestore)
        if (accounts.isNotEmpty()) {
            adapter.updateList(accounts.sortedWith(compareBy { it.bank }).toList())
            binding.recycler.visibility = View.VISIBLE
            binding.tvSinLista.visibility = View.GONE
        } else {
            binding.recycler.visibility = View.GONE
            binding.tvSinLista.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
    }
}
