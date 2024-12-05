package com.nazarkopelchak.otptextfield

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val VALID_PASSCODE = "3333"

class OptViewModel: ViewModel() {

    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()

    fun onAction(action: OptAction) {
        when(action) {
            is OptAction.OnChangeFieldFocus -> {
                _state.update { it.copy(focusedIndex = action.index) }
            }
            is OptAction.OnEnterNumber -> {
                enterNumber(action.number, action.index)
            }
            OptAction.OnKeyboardBackPress -> {
                val previousIndex = getPreviousFocusedIndex(state.value.focusedIndex)
                _state.update { it.copy(
                    passcode = it.passcode.mapIndexed { index, number ->
                        if (index == previousIndex) {
                            null
                        } else {
                            number
                        }
                    },
                    focusedIndex = previousIndex
                ) }
            }
        }
    }

    private fun enterNumber(number: Int?, index: Int) {
        val newPasscode = state.value.passcode.mapIndexed { currentIndex, currentNumber ->
            if (currentIndex == index) {
                number
            }
            else {
                currentNumber
            }
        }

        val wasNumberRemoved = number == null
        _state.update { it.copy(
            passcode = newPasscode,
            focusedIndex = if (wasNumberRemoved || it.passcode.getOrNull(index) != null) {
                println("INDEX: ${it.passcode.getOrNull(index)}")
                it.focusedIndex
            } else {
                getNextFocusedTextFieldIndex(
                    currentPasscode = it.passcode,
                    currentFocusedIndex = it.focusedIndex
                )
            },
            isValid = if (newPasscode.none { it == null }) {
                newPasscode.joinToString("") == VALID_PASSCODE
            } else null
        ) }
    }

    private fun getPreviousFocusedIndex(currentIndex: Int?): Int? {
        return currentIndex?.minus(1)?.coerceAtLeast(0)
    }

    private fun getNextFocusedTextFieldIndex(
        currentPasscode: List<Int?>,
        currentFocusedIndex: Int?
    ): Int? {
        if (currentFocusedIndex == null) {
            return null
        }
        if (currentFocusedIndex == 3) {
            return currentFocusedIndex
        }

        return getFirstEmptyFieldIndexAfterFocusedIndex(
            currentPasscode,
            currentFocusedIndex
        )
    }

    private fun getFirstEmptyFieldIndexAfterFocusedIndex(
        passcode: List<Int?>,
        currentFocusedIndex: Int
    ): Int {
        passcode.forEachIndexed { index, number ->
            if (index <= currentFocusedIndex) {
                return@forEachIndexed
            }
            if (number == null) {
                return index
            }
        }
        return currentFocusedIndex
    }
}