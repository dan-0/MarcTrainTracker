package android.util

class Log {
    companion object {
        @JvmStatic
        fun println(priority: Int, tag: String, msg: String): Int {
            val priorityStr = when(priority) {
                7 -> "ASSERT"
                3 -> "DEBUG"
                2 -> "VERBOSE"
                5 -> "WARN"
                6 -> "ERROR"
                4 -> "INFO"
                else -> "NONE"
            }
            System.out.println("$priorityStr: $tag: $msg")
            return 0
        }

        @JvmStatic
        fun println(tag: String, msg: String): Int {
            System.out.println("$tag: $msg")
            return 0
        }
    }
}