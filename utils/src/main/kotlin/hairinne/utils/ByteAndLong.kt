package hairinne.utils

object ByteAndLong {
    object BigEndian {
        /**
         * Convert ByteArray to Long
         *
         * Big Endian:
         * 11, 22, 33, 44 -> 11223344L
         * @receiver ByteArray
         * @return Long
         */
        fun ByteArray.toLong(): Long {
            var res: Long = 0
            for (i in this) {
                res += (res shl 8) + i.toLong()
            }
            return res
        }

        /**
         * Convert Long to ByteArray
         *
         * Big Endian:
         * 11223344L -> 11, 22, 33, 44
         * @receiver Long
         * @return ByteArray
         */
        fun Long.toByteArray(): ByteArray {
            val res: MutableList<Byte> = mutableListOf()
            for (i in 0 until 8) {
                res.add((this shr (i * 8)).toByte())
            }
            return res.toByteArray()
        }
    }
    object LittleEndian {
        /**
         * Convert ByteArray to Long
         *
         * Little Endian:
         * 11, 22, 33, 44 -> 11223344L
         * @receiver ByteArray
         * @return Long
         */
        fun ByteArray.toLong(): Long {
            var res: Long = 0
            for (i in this.reversed()) {
                res += (res shl 8) + i.toLong()
            }
            return res
        }

        /**
         * Convert Long to ByteArray
         *
         * Little Endian:
         * 11223344L -> 11, 22, 33, 44
         * @receiver Long
         * @return ByteArray
         */
        fun Long.toByteArray(): ByteArray {
            val res: MutableList<Byte> = mutableListOf()
            for (i in (0 until 8).reversed()) {
                res.add((this shr (i * 8)).toByte())
            }
            return res.toByteArray()
        }
    }
}
