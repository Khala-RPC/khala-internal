package khala.internal.zmq


//TODO Make proper priority queue implementation through binary heap
internal class PriorityQueue<T : Comparable<T>> {

    private val naiveList = ArrayList<T>()

    fun isNotEmpty(): Boolean {
        return naiveList.isNotEmpty()
    }

    fun poll(): T {
        return naiveList.removeFirst()
    }

    fun peek(): T {
        return naiveList.first()
    }

    fun add(element: T) {
        var idx = naiveList.binarySearch(element)
        if (idx < 0) idx = -idx - 1
        naiveList.add(idx, element)
    }

}