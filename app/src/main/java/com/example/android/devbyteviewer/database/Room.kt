/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// create an interface and
@Dao
interface VideoDao {
    // gets all videos from the cache
    // annotate with select all query
    @Query("select * from databasevideo")
    //when we return a live data, room will do the database query in the background
    //it will update the live data anytime new data is written to the table
    fun getVideos(): LiveData<List<DatabaseVideo>>

    // we need a way to store values in the cache
    // vararg means variable arguments - function takes an unknown number of arguments
    // set conflict strategy to replace
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: DatabaseVideo)
}