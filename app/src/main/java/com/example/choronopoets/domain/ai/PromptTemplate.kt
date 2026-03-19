package com.example.choronopoets.domain.ai

/**
 * A quick-fill template the user can tap to pre-populate the chat input.
 *
 * @param label  Short chip label shown in the UI (e.g. "Стиль письма")
 * @param prompt Full question text placed into the input field when selected
 */
data class PromptTemplate(
    val label: String,
    val prompt: String,
)

/** Returns context-sensitive quick-fill chips for the given [ChatMode]. */
fun promptTemplatesFor(mode: ChatMode): List<PromptTemplate> = when (mode) {
    ChatMode.ASK -> listOf(
        PromptTemplate(
            label = "Кто такой этот поэт?",
            prompt = "Кто такой этот поэт и почему он важен в истории литературы?",
        ),
        PromptTemplate(
            label = "Стиль письма",
            prompt = "Опиши стиль письма этого поэта, его поэтические формы и основные темы.",
        ),
        PromptTemplate(
            label = "Известные произведения",
            prompt = "Какие самые известные произведения этого поэта и чем они прославились?",
        ),
        PromptTemplate(
            label = "Влияние на литературу",
            prompt = "Как этот поэт повлиял на литературу и последующие поколения поэтов?",
        ),
        PromptTemplate(
            label = "Интересный факт",
            prompt = "Расскажи интересный или малоизвестный факт из жизни этого поэта.",
        ),
        PromptTemplate(
            label = "Исторический контекст",
            prompt = "В каком историческом и культурном контексте жил и творил этот поэт?",
        ),
    )

    ChatMode.ROLEPLAY -> listOf(
        PromptTemplate(
            label = "Представьтесь",
            prompt = "Кто вы? Расскажите о себе и о том, что вы пишете.",
        ),
        PromptTemplate(
            label = "Ваша эпоха",
            prompt = "Как жилось в ваше время? С какими трудностями вы сталкивались?",
        ),
        PromptTemplate(
            label = "Лучшее стихотворение",
            prompt = "Каким из своих стихотворений вы больше всего гордитесь, и почему написали его?",
        ),
        PromptTemplate(
            label = "О любви",
            prompt = "Как вы относитесь к любви? Отражается ли она в вашей поэзии?",
        ),
        PromptTemplate(
            label = "Мудрость",
            prompt = "Какой самый важный совет вы дали бы человеку, живущему в наши дни?",
        ),
        PromptTemplate(
            label = "Источник вдохновения",
            prompt = "Что или кто вдохновлял вас больше всего, когда вы писали свои стихи?",
        ),
    )
}
