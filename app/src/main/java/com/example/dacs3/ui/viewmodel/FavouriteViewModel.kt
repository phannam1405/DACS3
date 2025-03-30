package com.example.dacs3.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dacs3.R
import com.example.dacs3.data.model.OutdataFav

class FavouriteViewModel : ViewModel() {
    private val _favourites = MutableLiveData<List<OutdataFav>>()
    val favourites: LiveData<List<OutdataFav>> = _favourites

    fun loadFavourites() {
        val sampleList = listOf(
            OutdataFav(R.drawable.singer_minh_gay, "Bạc Phận", "Minh Gay", "4:32"),
            OutdataFav(R.drawable.singer_minh_gay, "Lạc Trôi", "Sơn Tùng M-TP", "3:55")
        )
        _favourites.value = sampleList
    }
}
