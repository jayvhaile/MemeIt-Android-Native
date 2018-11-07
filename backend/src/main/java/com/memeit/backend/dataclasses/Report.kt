package com.memeit.backend.dataclasses

class Report {
    enum class ReportTypes(val message: String) {
        PORNOGRAPHY("Contains Pornographic Content"),
        VIOLENCE("Contains Violence"),
        ABUSE("Contains Abuse"),
        INAPPROPRIATE("Inappropriate content");
    }

    data class UserReport(val uid: String, val message: String)
    data class MemeReport(val mid: String, val message: String)
}