package com.skysam.datossegurosFirebaseFinal.cards.ui

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
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.Card
import com.skysam.datossegurosFirebaseFinal.databinding.CardsFragmentBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddActivity
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainViewModel
import java.util.Locale

class CardsFragment : Fragment(), SearchView.OnQueryTextListener, MenuProvider {

    private var _binding: CardsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fab: FloatingActionButton
    private val viewModel: MainViewModel by activityViewModels()
    private val cardsFirestore: MutableList<Card> = mutableListOf()
    private val cards: MutableList<Card> = mutableListOf()
    private val listSearch: MutableList<Card> = mutableListOf()
    private lateinit var adapter: CardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = CardsFragmentBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CardAdapter(cards.toList())
        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)

        fab = requireActivity().findViewById(R.id.fab)
        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.AGREGAR, 2)
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
            fab.setImageResource(R.drawable.ic_add_card)
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
        viewModel.cardsFirestore.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                cardsFirestore.clear()
                cardsFirestore.addAll(it)
                loadPasswords()
            }
        }
    }

    private fun loadPasswords() {
        cards.clear()
        cards.addAll(cardsFirestore)
        if (cards.isNotEmpty()) {
            adapter.updateList(cards.sortedWith(compareBy { it.bank }).toList())
            binding.recycler.visibility = View.VISIBLE
            binding.tvSinLista.visibility = View.GONE
        } else {
            binding.recycler.visibility = View.GONE
            binding.tvSinLista.visibility = View.VISIBLE
        }
        binding.progressBar.visibility = View.GONE
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (cards.isNotEmpty()) {
            val userInput = newText!!.lowercase(Locale.ROOT)
            listSearch.clear()

            for (card in cards) {
                if (card.bank.lowercase(Locale.ROOT).contains(userInput) ||
                        card.user.lowercase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(card)
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
        return true
    }

}