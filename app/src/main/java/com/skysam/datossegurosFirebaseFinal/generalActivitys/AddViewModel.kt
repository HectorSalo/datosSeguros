package com.skysam.datossegurosFirebaseFinal.generalActivitys

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.skysam.datossegurosFirebaseFinal.database.repositories.LabelRepository

/**
 * Created by Hector Chirinos on 27/10/2021.
 */
class AddViewModel: ViewModel() {
    val labels: LiveData<MutableList<String>> = LabelRepository.getLabels().asLiveData()
}