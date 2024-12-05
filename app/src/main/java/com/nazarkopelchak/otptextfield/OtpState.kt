package com.nazarkopelchak.otptextfield

data class OtpState(
    val passcode: List<Int?> = (1..4).map { null },
    val focusedIndex: Int? = null,
    val isValid: Boolean? = null
)
