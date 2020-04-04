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
 */

package com.example.android.trackmysleepquality
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SleepDatabaseTest {

    private lateinit var sleepDao: SleepDatabaseDao
    private lateinit var db: SleepDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, SleepDatabase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        sleepDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetNight() {
        val night = SleepNight()
        sleepDao.insert(night)
        val tonight = sleepDao.getTonight()
        assertEquals(tonight?.sleepQuality, -1)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGet() {
        val nightFirst = SleepNight()
        val nightSecond = SleepNight()
        val newNightSecond = SleepNight(sleepQuality = 4, nightId = 2L)

        sleepDao.insert(nightFirst)
        sleepDao.insert(nightSecond)

        var tonight = sleepDao.get(2L)
        assertEquals(tonight?.sleepQuality, -1)

        sleepDao.update(newNightSecond)

        tonight = sleepDao.get(2L)
        assertEquals(tonight?.sleepQuality, 4)
    }

    @Test
    @Throws(Exception::class)
    fun clear() {
        val nightFirst = SleepNight()
        val nightSecond = SleepNight(sleepQuality = 4)

        sleepDao.insert(nightFirst)
        sleepDao.insert(nightSecond)

        var tonight = sleepDao.getTonight()

        assertEquals(tonight?.sleepQuality, 4)

        sleepDao.clear()

        tonight = sleepDao.getTonight()

        assertEquals(tonight, null)
    }
}

