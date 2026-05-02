package com.gw.fitt.domain.usecase.coach

import javax.inject.Inject

class GetCoachTipsUseCase @Inject constructor() {

    fun getResponse(input: String): String {
        val lower = input.lowercase()
        return responses.entries.firstOrNull { (keywords, _) ->
            keywords.any { keyword -> lower.contains(keyword) }
        }?.value ?: defaultResponse
    }

    fun getQuickQuestions(): List<String> = listOf(
        "상체 운동 추천해줘",
        "하체 운동 추천해줘",
        "코어 운동 알려줘",
        "체중 감량 방법이 뭐야?",
        "단백질 섭취는 얼마나 해야 해?",
        "초보자는 어떻게 시작해야 해?"
    )

    private val responses = mapOf(
        listOf("상체", "팔", "가슴", "어깨", "등", "벤치") to
            "상체 운동 추천:\n• 푸시업 3세트 × 15회\n• 덤벨 숄더 프레스 3세트 × 12회\n• 바벨 벤치 프레스 4세트 × 10회\n\n근육 회복을 위해 운동 후 48시간 휴식을 권장합니다.",

        listOf("하체", "다리", "허벅지", "종아리", "스쿼트", "런지") to
            "하체 운동 추천:\n• 스쿼트 4세트 × 12회\n• 런지 3세트 × 12회\n• 레그 프레스 3세트 × 15회\n\n하체는 체내 최대 근육군으로 꾸준한 운동이 기초대사량 향상에 효과적입니다.",

        listOf("코어", "복근", "플랭크", "복부", "허리") to
            "코어 운동 추천:\n• 플랭크 3세트 × 60초\n• 크런치 3세트 × 20회\n• 레그레이즈 3세트 × 15회\n\n코어 강화는 모든 운동의 자세 안정에 도움이 됩니다.",

        listOf("살", "체중", "감량", "다이어트", "칼로리", "살빼") to
            "체중 감량 팁:\n• 주 4-5회 유산소 운동 (30분 이상)\n• 근력 운동 병행으로 기초대사량 증가\n• 일일 500kcal 적자 유지\n\n주당 0.5kg 감량이 안전하고 지속 가능한 목표입니다.",

        listOf("단백질", "영양", "식단", "음식", "식사", "먹") to
            "운동 영양 가이드:\n• 운동 후 30분 이내 단백질 섭취 권장\n• 체중 1kg당 1.5–2g 단백질 섭취\n• 탄수화물은 운동 전 에너지원으로 활용\n• 하루 2L 이상 수분 섭취",

        listOf("초보", "처음", "시작", "입문", "모르") to
            "운동 입문자 가이드:\n• 주 3회, 격일 운동으로 시작\n• 유산소 20분 + 기본 근력 운동 조합\n• 올바른 자세 습득이 최우선\n• 가벼운 중량으로 자세 연습 후 점진적 증가",

        listOf("쉬는", "휴식", "회복", "몸살", "피로") to
            "회복 & 휴식 가이드:\n• 같은 근육군은 48–72시간 휴식 필요\n• 수면 7–9시간이 근육 성장에 필수\n• 가벼운 스트레칭으로 적극적 회복 가능\n• 통증이 있을 때는 무리하지 마세요"
    )

    private val defaultResponse =
        "안녕하세요! 운동 관련 질문을 입력해 주세요 😊\n\n아래 빠른 질문을 눌러도 됩니다."
}
