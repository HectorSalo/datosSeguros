package com.skysam.datossegurosFirebaseFinal.passwords.ui

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
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
import com.skysam.datossegurosFirebaseFinal.common.ConexionSQLite
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.PasswordsModel
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref
import com.skysam.datossegurosFirebaseFinal.databinding.PasswordsFragmentBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddActivity
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PasswordsFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var fab: FloatingActionButton
    private var _binding: PasswordsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private val passwordsFirestore: MutableList<PasswordsModel> = mutableListOf()
    private val passwordsSQLite: MutableList<PasswordsModel> = mutableListOf()
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
        adapter = PasswordsAdapter(passwordsFirestore.toList())
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

        loadPasswords()
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
            val userInput = newText!!.toLowerCase(Locale.ROOT)
            listSearch.clear()

            for (password in passwordsFirestore) {
                if (password.servicio.toLowerCase(Locale.ROOT).contains(userInput) ||
                        password.usuario.toLowerCase(Locale.ROOT).contains(userInput)) {
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
        when (SharedPref.getShowData()) {
            Constants.PREFERENCE_SHOW_ALL -> {

            }
            Constants.PREFERENCE_SHOW_CLOUD -> {
                if (passwordsFirestore.isNotEmpty()) {
                    adapter.updateList(passwordsFirestore.toList())
                    binding.recycler.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.tvSinLista.visibility = View.GONE
                } else {
                    binding.recycler.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvSinLista.visibility = View.VISIBLE
                }
            }
            Constants.PREFERENCE_SHOW_DEVICE -> {
                if (passwordsSQLite.isNotEmpty()) {
                    adapter.updateList(passwordsSQLite.toList())
                    binding.recycler.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    binding.tvSinLista.visibility = View.GONE
                } else {
                    binding.recycler.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvSinLista.visibility = View.VISIBLE
                }
            }
        }
    }

    fun loadViewModel() {
        viewModel.passwords.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                passwordsFirestore.clear()
                passwordsFirestore.addAll(it)
            }
        })
    }

    fun loadPasswordsSQLite() {
        val conect =  ConexionSQLite(requireContext(), Auth.getCurrenUser()!!.uid, null, Constants.VERSION_SQLITE)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val db: SQLiteDatabase = conect.readableDatabase
        val cursor = db.rawQuery("SELECT * from " + Constants.BD_CONTRASENAS, null)
        while (cursor.moveToNext()) {
            val pass = PasswordsModel()
            pass.idContrasena = cursor.getInt(0).toString()
            pass.servicio = cursor.getString(1)
            pass.usuario = cursor.getString(2)
            pass.contrasena = cursor.getString(3)
            val fechaCreacionS = cursor.getString(10)
            try {
                val fechaCreacion = sdf.parse(fechaCreacionS)
                val fechaCreacionL = fechaCreacion.time
                val fechaMomentoL: Long = fechaMomento.getTime()
                val diasRestantes = fechaCreacionL - fechaMomentoL
                val segundos = diasRestantes / 1000
                val minutos = segundos / 60
                val horas = minutos / 60
                val dias = horas / 24
                val diasTranscurridos = dias.toInt()
                if (cursor.getString(4) == "0") {
                    pass.vencimiento = 0
                } else {
                    val vencimiento = cursor.getString(4).toInt()
                    val faltante = vencimiento - diasTranscurridos
                    pass.vencimiento = faltante
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            passwordsSQLite.add(pass)
        }
    }
}