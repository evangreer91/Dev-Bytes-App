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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.Video
import com.example.android.devbyteviewer.network.Network
import com.example.android.devbyteviewer.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// repositories are regular classes and are responsible for providing a simple API to our data sources
// By taking a database object as a constructor parameter, we don't need to keep a reference to Android context in our repository
// this is called dependency injection
class VideosRepository(private val database: VideosDatabase) {

    // we want a live data with a list of videos
    // we call getVideos on the database to return a live data with a list of database video objects
    // convert the list of DatabaseVideo to a list of Video using asDomainModel
    val videos: LiveData<List<Video>> = Transformations.map(database.videoDao.getVideos()) {
       it.asDomainModel()
    }

    // refreshVideos is responsible for updating the offline cache
    // database call to save new videos to the database
    // reading and writing from a disk is very slow
    suspend fun refreshVideos() {
        withContext(Dispatchers.IO) {
            // make a network to get the playlist and use the await function to tell the coroutine to suspend until its available
            // we could put this network fetch outside of our withContext since it doesn't block a thread or do disk I/O
            val playlist = Network.devbytes.getPlaylist().await()
            // make the database call by using insertAll on videoDao
            // map network results to database objects
            database.videoDao.insertAll(*playlist.asDatabaseModel())
        }
    }
}