package hairinne.utils

object Overlooks {
    fun<T> list(list: List<T>, start: String = "[", end: String = "]"): String {
        if (list.isEmpty()) {
            return start + end
        }
        var result = start
        var last = list[0]
        var cnt = 1
        for (i in 1 until list.size) {
            if (list[i] == last) {
                cnt++
            } else {
                result += "$last*$cnt"
                cnt = 1
            }
            if ((cnt == 1) and (i != list.size - 1)) {
                result += ", "
            }
            last = list[i]
        }
        if (result != start) {
            result += ", "
        }
        result += "$last*$cnt"
        return result + end
    }
}
