package com.example.foodhygienescores.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favourite favourite);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFavourites(List<Favourite> favourites);

    @Query("SELECT * FROM favourites")
    LiveData<List<Favourite>> getAllFavourites();

    @Delete
    void deleteFavourite(Favourite favourite);

    @Query("DELETE FROM favourites WHERE fhrsid = :fhrsid")
    void deleteByFhrsid(int fhrsid);

    @Query("DELETE FROM favourites")
    void deleteAllFavourites();
}
