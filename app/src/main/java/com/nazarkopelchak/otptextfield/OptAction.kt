package com.nazarkopelchak.otptextfield

sealed interface OptAction {
    data class OnEnterNumber(val number: Int?, val index: Int): OptAction
    data class OnChangeFieldFocus(val index: Int): OptAction
    data object OnKeyboardBackPress: OptAction
}