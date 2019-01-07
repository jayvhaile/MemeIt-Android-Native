package com.memeit.backend.models

data class Tag(val tag: String, val count: Int, val date: Long, var followed: Boolean = false) : Comparable<Tag> {
    override fun compareTo(other: Tag): Int = other.count.compareTo(count)

    override fun equals(other: Any?): Boolean {
        return (other as? Tag)?.tag == tag
    }

    override fun hashCode(): Int {
        return tag.hashCode()
    }
}