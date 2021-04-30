package com.skysam.datossegurosFirebaseFinal.notes.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.NoteModel
import com.skysam.datossegurosFirebaseFinal.databinding.NotesFragmentBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddActivity
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainViewModel
import java.util.*

class NotesFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: NotesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fab: FloatingActionButton
    private val viewModel: MainViewModel by activityViewModels()
    private val notes: MutableList<NoteModel> = mutableListOf()
    private val listSearch: MutableList<NoteModel> = mutableListOf()
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = NotesFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = NoteAdapter(notes.toList())
        binding.recycler.adapter = adapter
        binding.recycler.setHasFixedSize(true)
        viewModel.notesFirestore.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                notes.clear()
                notes.addAll(it)
                adapter.updateList(notes.toList())
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
            bundle.putInt(Constants.AGREGAR, 3)
            val intent = Intent(requireContext(), AddActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fab.hide()
        Handler(Looper.getMainLooper()).postDelayed({
            fab.setImageResource(R.drawable.ic_add_nota)
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
        if (notes.isNotEmpty()) {
            val userInput = newText!!.toLowerCase(Locale.ROOT)
            listSearch.clear()

            for (note in notes) {
                if (note.titulo.toLowerCase(Locale.ROOT).contains(userInput)) {
                    listSearch.add(note)
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