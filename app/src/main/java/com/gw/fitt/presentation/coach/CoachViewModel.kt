package com.gw.fitt.presentation.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gw.fitt.domain.usecase.coach.GetCoachTipsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoachViewModel @Inject constructor(
    private val getCoachTipsUseCase: GetCoachTipsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CoachState())
    val state: StateFlow<CoachState> = _state.asStateFlow()

    val quickQuestions = getCoachTipsUseCase.getQuickQuestions()

    fun onInputChange(text: String) = _state.update { it.copy(inputText = text) }

    fun sendMessage(text: String = _state.value.inputText) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(content = text.trim(), isFromUser = true)
        _state.update { it.copy(
            messages = it.messages + userMsg,
            inputText = "",
            isTyping = true
        )}
        viewModelScope.launch {
            delay(600) // 타이핑 효과
            val response = getCoachTipsUseCase.getResponse(text)
            val aiMsg = ChatMessage(content = response, isFromUser = false)
            _state.update { it.copy(
                messages = it.messages + aiMsg,
                isTyping = false
            )}
        }
    }
}
