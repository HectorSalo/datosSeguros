package com.skysam.datossegurosFirebaseFinal.notes.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref
import com.skysam.datossegurosFirebaseFinal.databinding.NotesFragmentBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddActivity
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainViewModel
import java.util.*

class NotesFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: NotesFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var fab: FloatingActionButton
    private val viewModel: MainViewModel by activityViewModels()
    private val notesFirestore: MutableList<Note> = mutableListOf()
    private val notesRoom: MutableList<Note> = mutableListOf()
    private val notes: MutableList<Note> = mutableListOf()
    private val listSearch: MutableList<Note> = mutableListOf()
    private val labels = mutableListOf<String>()
    private val labelsToFilter = mutableListOf<String>()
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

        fab = requireActivity().findViewById(R.id.fab)
        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.AGREGAR, 3)
            val intent = Intent(requireContext(), AddActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        loadViewModel()
    }

    override fun onResume() {
        super.onResume()
        loadPasswords()
        if (SharedPref.getShowData() == Constants.PREFERENCE_SHOW_ALL ||
            SharedPref.getShowData() == Constants.PREFERENCE_SHOW_CLOUD) {
            loadLabels()
        }
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
            labelsToFilter.clear()
            if (SharedPref.getShowData() == Constants.PREFERENCE_SHOW_ALL ||
                SharedPref.getShowData() == Constants.PREFERENCE_SHOW_CLOUD) {
                loadLabels()
            }
            false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadViewModel() {
        viewModel.labels.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (SharedPref.getShowData() == Constants.PREFERENCE_SHOW_ALL ||
                    SharedPref.getShowData() == Constants.PREFERENCE_SHOW_CLOUD) {
                    labels.clear()
                    labels.addAll(it)
                    loadLabels()
                }
            }
        })
        viewModel.notesFirestore.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                notesFirestore.clear()
                notesFirestore.addAll(it)
                loadPasswords()
            }
        })

        viewModel.notesRoom.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                notesRoom.clear()
                notesRoom.addAll(it)
                loadPasswords()
            }
        })
    }

    private fun loadPasswords() {
        notes.clear()
        when (SharedPref.getShowData()) {
            Constants.PREFERENCE_SHOW_ALL -> {
                notes.addAll(notesFirestore)
                notes.addAll(notesRoom)
            }
            Constants.PREFERENCE_SHOW_CLOUD -> {
                notes.addAll(notesFirestore)
            }
            Constants.PREFERENCE_SHOW_DEVICE -> {
                notes.addAll(notesRoom)
            }
        }
        if (notes.isNotEmpty()) {
            adapter.updateList(notes.sortedWith(compareBy { it.title }).toList())
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
        if (notes.isNotEmpty()) {
            binding.chipGroup.removeAllViews()
            val userInput = newText!!.lowercase(Locale.ROOT)
            listSearch.clear()

            for (note in notes) {
                if (note.title.lowercase(Locale.ROOT).contains(userInput)) {
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
        val itemsToShow = mutableListOf<Note>()
        for (label in labelsToFilter) {
            for (item in notesFirestore) {
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

}