package com.gw.fitt.presentation.coach

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val isFromUser: Boolean
)

data class CoachState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            content = "안녕하세요! 운동 AI 코치입니다.\n궁금한 점을 물어보거나 아래 빠른 질문을 눌러 보세요!",
            isFromUser = false
        )
    ),
    val inputText: String = "",
    val isTyping: Boolean = false
)
