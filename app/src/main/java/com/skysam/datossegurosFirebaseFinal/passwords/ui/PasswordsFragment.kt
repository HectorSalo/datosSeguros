package com.skysam.datossegurosFirebaseFinal.passwords.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.databinding.PasswordsFragmentBinding

class PasswordsFragment : Fragment() {

    private lateinit var fab: FloatingActionButton
    private var _binding: PasswordsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = PasswordsFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.passwords.observe(viewLifecycleOwner, {
            val test = it.size
            val tes = it.size
        })

        fab = requireActivity().findViewById(R.id.fab)
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
}