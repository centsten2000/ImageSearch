package com.ch.yoon.kakao.pay.imagesearch.repository.local.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ch.yoon.kakao.pay.imagesearch.repository.local.room.entity.LocalImageDocument;
import com.ch.yoon.kakao.pay.imagesearch.repository.local.room.entity.SearchLog;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Creator : ch-yoon
 * Date : 2019-08-05.
 */
@Dao
public interface ImageSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable update(SearchLog searchLog);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LocalImageDocument> documents);

//    @Query("SELECT id, thumbnailUrl " +
//        "FROM documents " +
//        "WHERE keyword == :keyword AND " +
//        "itemNumber >= :startNumber AND " +
//        "itemNumber <= :endNumber AND " +
//        "imageSortType == :imageSortType")
//    Single<List<SimpleImageInfo>> selectSimpleImageInfoList(String keyword,
//                                                            int startNumber,
//                                                            int endNumber,
//                                                            String imageSortType);
//
//    @Query("SELECT imageUrl, displaySiteName, docUrl, dateTime, width, height " +
//            "FROM documents WHERE id = :id")
//    Single<DetailImageInfo> selectDetailImageInfo(String id);

    @Query("SELECT * FROM searchLogs")
    Single<List<SearchLog>> selectAllSearchLog();

    @Query("DELETE FROM searchLogs WHERE keyword = :keyword")
    Completable deleteSearchLog(String keyword);

    @Query("DELETE FROM documents WHERE keyword = :keyword")
    Completable deleteAllDocument(String keyword);

}
