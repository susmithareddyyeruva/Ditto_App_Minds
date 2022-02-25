package com.ditto.home.ui

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }



    @Test
    fun letsMockListSizeMethod() {
        val list = Mockito.mock(List::class.java)
        Mockito.`when`(list.size).thenReturn(2)
        assertEquals(2, list.size)
    }

    @Test
    fun letsMockListSizeMethodReturnMultipleValue() {
        val list = Mockito.mock(List::class.java)
        Mockito.`when`(list.size).thenReturn(2).thenReturn(3)
        assertEquals(2, list.size)
        assertEquals(3, list.size)
    }

    @Test
    fun letsMockListGet() {
        val list = Mockito.mock(List::class.java)
        Mockito.`when`(list[0]).thenReturn("Ditto")
        assertEquals("Ditto", list [0])
        assertEquals(null, list[1])
    }

    @Test
    fun `argument matcher`() {
        val list = Mockito.mock(List::class.java)
        // argument matcher >> for any interger value it will pass ditto
        //anyInt() is argument matcher
        Mockito.`when`(list[Mockito.anyInt()]).thenReturn("Ditto")
        assertEquals("Ditto", list [0])
        assertEquals("Ditto", list [5])
    }

    @Test
    fun testVerify() {
        val mockList: MutableList<String> = Mockito.mock(MutableList::class.java) as MutableList<String>
        mockList.add("Pankaj")
        mockList.size
        Mockito.verify(mockList).add("Pankaj")
    }


    @Test
    fun testSpy() {
        val list: List<String> = ArrayList()
        val spyOnList: MutableList<String> = Mockito.spy(list) as MutableList<String>
        spyOnList.add("A")
        assertEquals(1, spyOnList.size)
        assertEquals("A", spyOnList[0])
        spyOnList.add("E")
        assertEquals(2, spyOnList.size)
        assertEquals("E", spyOnList[1])
        Mockito.`when`(spyOnList.size).thenReturn(10)
        assertEquals(10, spyOnList.size)
    }


    @Test
    fun creatingASpyOnArrayList_overridingSpecificMethods() {
        val listSpy: MutableList<String> = Mockito.spy(
            java.util.ArrayList::class.java
        )as MutableList<String>
        listSpy.add("Ranga")
        listSpy.add("in28Minutes")
        //when (listSpy.size).thenReturn(-1)
                Mockito.`when`(listSpy.size).thenReturn(-1)
        assertEquals(-1, listSpy.size.toLong())
        assertEquals("Ranga", listSpy[0])

        // @Spy Annotation
    }

    @Test
    fun creatingASpyOnArrayList() {
        val listSpy: MutableList<String> = spy(
            java.util.ArrayList::class.java
        ) as MutableList<String>
        listSpy.add("Ranga")
        listSpy.add("in28Minutes")
        verify(listSpy).add("Ranga")
        verify(listSpy).add("in28Minutes")
        assertEquals(2, listSpy.size.toLong())
        assertEquals("Ranga", listSpy[0])
    }
}
