package com.warriorsdev.brastlewark.datasource.network

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GnomeApiServiceTest {
    private val testSubject = GnomeApiService

    @Test
    fun getJSONResource() {
        testSubject.getJSONResource({
            assert(it.isNotEmpty())
        }, {
            assert(true)
        })
    }

    @Test
    fun getBitmapFromURL() {
        val testURL =
            "https://www.autodeal.com.ph/custom/blog-post/header/mazda3-sportback-polymetal-gray-philippines-5de6210b35c6e.jpg"
        testSubject.getBitmapFromURL(testURL, {
            assert(!it.isMutable)
        }, {
            assert(true)
        })
        Thread.sleep(1000)
    }
}