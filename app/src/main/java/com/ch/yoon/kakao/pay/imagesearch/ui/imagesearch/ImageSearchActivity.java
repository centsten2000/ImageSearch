package com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ch.yoon.kakao.pay.imagesearch.R;
import com.ch.yoon.kakao.pay.imagesearch.databinding.ActivityImageSearchBinding;
import com.ch.yoon.kakao.pay.imagesearch.repository.ImageRepository;
import com.ch.yoon.kakao.pay.imagesearch.repository.ImageRepositoryImpl;
import com.ch.yoon.kakao.pay.imagesearch.repository.local.room.ImageDatabase;
import com.ch.yoon.kakao.pay.imagesearch.repository.local.room.ImageLocalDataSource;
import com.ch.yoon.kakao.pay.imagesearch.repository.local.room.dao.ImageSearchDao;
import com.ch.yoon.kakao.pay.imagesearch.repository.remote.kakao.ImageRemoteDataSource;
import com.ch.yoon.kakao.pay.imagesearch.ui.base.BaseActivity;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagedetail.ImageDetailActivity;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch.imagelist.ImageListViewModel;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch.imagelist.ImageListViewModelFactory;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch.imagelist.adapter.ImageListAdapter;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch.searchbox.adapter.SearchLogAdapter;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch.imagelist.helper.ImageSearchInspector;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch.searchbox.SearchBoxViewModel;
import com.ch.yoon.kakao.pay.imagesearch.ui.imagesearch.searchbox.SearchBoxViewModelFactory;

public class ImageSearchActivity extends BaseActivity<ActivityImageSearchBinding> {

    private ActivityImageSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = binding(R.layout.activity_image_search);

        initSearchBoxViewModel();
        initSearchKeywordEditText();
        observeSearchBoxViewModel();

        initImageListViewModel();
        observeImageListViewModel();

        initSearchLogRecyclerView();
        initImageRecyclerView();
    }

    private void initSearchBoxViewModel() {
        final ImageSearchDao imageSearchDao = ImageDatabase.getInstance(getApplicationContext()).imageDocumentDao();
        final ImageLocalDataSource localDataSource = ImageLocalDataSource.getInstance(imageSearchDao);
        final ImageRemoteDataSource remoteDataSource = ImageRemoteDataSource.getInstance();
        final ImageRepository repository = ImageRepositoryImpl.getInstance(localDataSource, remoteDataSource);

        final SearchBoxViewModel viewModel = ViewModelProviders.of(this,
            new SearchBoxViewModelFactory(getApplication(), repository)
        ).get(SearchBoxViewModel.class);

        binding.setSearchBoxViewModel(viewModel);
    }

    private void initSearchKeywordEditText() {
        binding.keywordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == KeyEvent.KEYCODE_ENDCALL) {
                String text = v.getText().toString();
                binding.getSearchBoxViewModel().clickSearchButton(text);
                return true;
            }

            return false;
        });
    }

    private void observeSearchBoxViewModel() {
        binding.getSearchBoxViewModel()
            .observeSearchKeyword()
            .observe(this, keyword -> {
                binding.getImageListViewModel().loadImageList(keyword);
                binding.keywordEditText.setText(keyword);
            });

        binding.getSearchBoxViewModel()
            .observeSearchBoxFinish()
            .observe(this, voidEvent ->
                finish()
            );

        binding.getSearchBoxViewModel()
            .observeShowMessage()
            .observe(this, this::showToast);
    }

    private void initSearchLogRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.searchLogRecyclerView.setLayoutManager(linearLayoutManager);

        final SearchLogAdapter adapter = new SearchLogAdapter();
        adapter.setOnSearchLogClickListener((searchLog, position) ->
            binding.getSearchBoxViewModel().clickSearchButton(searchLog.getKeyword())
        );

        adapter.setOnLogDeleteClickListener((searchLog, position) ->
            binding.getSearchBoxViewModel().clickKeywordDeleteButton(searchLog.getKeyword())
        );

        binding.searchLogRecyclerView.setAdapter(adapter);
    }

    private void initImageListViewModel() {
        final ImageSearchDao imageSearchDao = ImageDatabase.getInstance(getApplicationContext()).imageDocumentDao();
        final ImageLocalDataSource localDataSource = ImageLocalDataSource.getInstance(imageSearchDao);
        final ImageRemoteDataSource remoteDataSource = ImageRemoteDataSource.getInstance();
        final ImageRepository repository = ImageRepositoryImpl.getInstance(localDataSource, remoteDataSource);

        final ImageSearchInspector imageSearchInspector = new ImageSearchInspector(1, 50, 80, 20);

        final ImageListViewModel viewModel = ViewModelProviders.of(this, new ImageListViewModelFactory(
            getApplication(), repository, imageSearchInspector
        )).get(ImageListViewModel.class);

        binding.setImageListViewModel(viewModel);
    }

    private void observeImageListViewModel() {
        binding.getImageListViewModel()
            .observeShowMessage()
            .observe(this, this::showToast);
    }

    private void initImageRecyclerView() {
        final ImageListAdapter imageListAdapter = new ImageListAdapter();

        imageListAdapter.setOnBindPositionListener(position ->
            binding.getImageListViewModel().loadMoreImageListIfPossible(position)
        );

        imageListAdapter.setOnListItemClickListener((imageDocument, position) -> {
            Intent imageDetailIntent = ImageDetailActivity.getImageDetailActivityIntent(this, imageDocument);
            startActivity(imageDetailIntent);
        });

        imageListAdapter.setOnFooterItemClickListener(() ->
            binding.getImageListViewModel().retryLoadMoreImageList()
        );

        GridLayoutManager gridLayoutManager = (GridLayoutManager)binding.imageRecyclerView.getLayoutManager();
        if(gridLayoutManager != null) {
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(imageListAdapter.isFooterViewPosition(position)) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }

        binding.imageRecyclerView.setLayoutManager(gridLayoutManager);
        binding.imageRecyclerView.setAdapter(imageListAdapter);
    }

    @Override
    public void onBackPressed() {
        binding.getSearchBoxViewModel().clickBackPress();
    }

}