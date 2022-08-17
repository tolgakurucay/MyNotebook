package com.tolgakurucay.mynotebook.services

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.tolgakurucay.mynotebook.MainCoroutineRule
import com.tolgakurucay.mynotebook.models.ImageResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ImageAPITest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var imageResponse:ImageResponse


    @Before
    fun setup() = runTest{
       imageResponse= ImageResponse(listOf(),0,0)
    }

    @Test
    fun getImageInfosTest(){

        assertThat(imageResponse).isNotNull()


    }


}