package com.skysam.datossegurosFirebaseFinal.common.classView

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Keyboard
import com.skysam.datossegurosFirebaseFinal.databinding.DialogAddLabelBinding
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddViewModel

/**
 * Created by Hector Chirinos on 27/10/2021.
 */
class AddLabelDialog: DialogFragment() {
    private var _binding: DialogAddLabelBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private val viewModel: AddViewModel by activityViewModels()
    private val labels = mutableListOf<String>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddLabelBinding.inflate(layoutInflater)

        viewModel.labels.observe(this.requireActivity(), {
            labels.clear()
            labels.addAll(it)
        })

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_dialog_add_label))
            .setView(binding.root)
            .setPositiveButton(R.string.buttonGuardar, null)
            .setNegativeButton(R.string.buttonCancelar, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateLocation() }

        return dialog
    }

    private fun validateLocation() {
        binding.tfLabel.error = null

        val label = binding.etLabel.text.toString()
        if (label.isEmpty()) {
            binding.tfLabel.error = getString(R.string.error_campo_vacio)
            binding.etLabel.requestFocus()
            return
        }
        var locationExists = false
        for (lab in labels) {
            if (lab == label) {
                binding.tfLabel.error = getString(R.string.error_label_exists)
                binding.etLabel.requestFocus()
                locationExists = true
                break
            }
        }
        if (locationExists) return

        Keyboard.close(binding.root)
        viewModel.addLabel(label)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}