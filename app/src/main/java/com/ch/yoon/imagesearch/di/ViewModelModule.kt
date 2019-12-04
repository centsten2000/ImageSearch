package com.ch.yoon.imagesearch.di

import com.ch.yoon.imagesearch.presentation.favorite.FavoriteListViewModel
import com.ch.yoon.imagesearch.presentation.detail.ImageDetailViewModel
import com.ch.yoon.imagesearch.presentation.search.imagesearch.ImageSearchViewModel
import com.ch.yoon.imagesearch.presentation.search.searchbox.SearchBoxViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

/**
 * Creator : ch-yoon
 * Date : 2019-10-28
 **/
val viewModelModule = module {

    viewModel {
        SearchBoxViewModel(get(), get())
    }

    viewModel {
        ImageSearchViewModel(get(), get(), get())
    }

    viewModel {
        ImageDetailViewModel(get(), get())
    }

    viewModel {
        FavoriteListViewModel(get(), get())
    }
}