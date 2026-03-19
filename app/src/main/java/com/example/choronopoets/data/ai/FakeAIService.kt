package com.example.choronopoets.data.ai

import com.example.choronopoets.domain.ai.AiService
import com.example.choronopoets.domain.ai.ChatMessage
import com.example.choronopoets.domain.ai.ChatRole
import com.example.choronopoets.domain.ai.PoemStyle
import kotlinx.coroutines.delay

class FakeAIService : AiService {
    override suspend fun generateChat(
        systemPrompt: String,
        history: List<ChatMessage>,
        userMessage: String,
    ): String {
        delay(450) // Simulate a bit of “thinking”.

        val poetName = extractPoetName(systemPrompt)
        val isRoleplay = systemPrompt.contains("Speak in first person", ignoreCase = true)
        val isExpert = systemPrompt.contains("expert in literature", ignoreCase = true)

        return when {
            isRoleplay -> generateRoleplayReply(poetName, history, userMessage)
            isExpert -> generateExpertReply(poetName, history, userMessage)
            else -> generateGenericReply(poetName, history, userMessage)
        }
    }

    override suspend fun generatePoem(
        topic: String,
        style: PoemStyle,
    ): String {
        delay(600)

        val cleanTopic = topic.trim().ifEmpty { "love and destiny" }
        val motif = when (style) {
            PoemStyle.ROMANTIC -> "soft love and longing"
            PoemStyle.CLASSICAL -> "wisdom, rhythm, and restraint"
            PoemStyle.SAD -> "quiet sorrow and reflection"
            PoemStyle.ISLAMIC -> "faith, humility, and hope"
            PoemStyle.EPIC -> "heroic spirit and noble struggle"
        }

        val voice = when (style) {
            PoemStyle.ISLAMIC -> "Пусть свет веры ведет по пути"
            else -> "В сердце строка рождает ясный путь"
        }

        val lines = when (style) {
            PoemStyle.ROMANTIC -> listOf(
                "О ${cleanTopic}, ты звучишь, как тихий шепот в ночи.",
                "Мое дыхание ищет тебя в каждом шаге судьбы.",
                "Пусть сердца язык станет нежнее всех обещаний,",
                "и любовь найдет дорогу сквозь сомнения и страхи.",
                "Ты как звезда: не просит, но утешает взгляд.",
                voice,
                "Когда мир темнеет, я отвечаю тебе песней.",
                "О ${cleanTopic}, будь вечным светом в ладонях времени.",
            )

            PoemStyle.CLASSICAL -> listOf(
                "О ${cleanTopic}, в стройности слов живет смысл и мера.",
                "Пусть ритм держит мысль, а мысль бережет честь.",
                "Там, где страсть теряет путь, приходит мудрый суд,",
                "и каждый образ становится уроком для души.",
                "Я выбираю ясность, чтобы сердце не блуждало.",
                voice,
                "Пусть строка будет как зеркало: смотрит и учит.",
                "О ${cleanTopic}, да будет слово твердым, как правда.",
            )

            PoemStyle.SAD -> listOf(
                "О ${cleanTopic}, ты оставляешь след на мокром стекле.",
                "Я говорю с тенью и слышу, как время отступает тихо.",
                "Горечь не спорит с небом — она лишь просит покоя.",
                "Сердце учится прощать без громких слов.",
                "Где раньше была радость, теперь светит усталость.",
                voice,
                "Я собираю надежду по сломанным звукам,",
                "и ${cleanTopic} становится моей печальной молитвой.",
            )

            PoemStyle.ISLAMIC -> listOf(
                "О ${cleanTopic}, веди меня к благому и чистому намерению.",
                "Пусть сердце станет мягким перед милостью Всевышнего.",
                "Я молю о терпении и о свете в душе,",
                "когда дорога темнеет, и мысли идут кругами.",
                "С надеждой поднимаю взгляд: вера не исчезает.",
                voice,
                "О ${cleanTopic}, научи благодарности и смирению.",
                "Пусть строки будут мостом между страхом и надеждой.",
            )

            PoemStyle.EPIC -> listOf(
                "О ${cleanTopic}, подними смелость из глубины крови и стали.",
                "Пусть ветер станет знаменем, а сердце — крепостью пути.",
                "Я иду вперед, неся достоинство в каждой строке,",
                "и тьма отступает там, где звучит правда.",
                "Пусть испытания закаляют, а не ломают дух.",
                voice,
                "О ${cleanTopic}, вдохновляй на подвиг и стойкость.",
                "Пока есть воля, слово будет мечом.",
            )
        }

        return lines.joinToString(separator = "\n")
    }

    private fun generateExpertReply(
        poetName: String,
        history: List<ChatMessage>,
        userMessage: String,
    ): String {
        val lastUser = history.lastOrNull { it.role == ChatRole.USER }?.content
        val question = if (userMessage.isBlank()) lastUser.orEmpty() else userMessage

        val topicHints = when {
            question.contains("life", ignoreCase = true) || question.contains("biography", ignoreCase = true) -> "life"
            question.contains("work", ignoreCase = true) || question.contains("poem", ignoreCase = true) -> "works"
            question.contains("style", ignoreCase = true) || question.contains("language", ignoreCase = true) -> "style"
            else -> "general"
        }

        return when (topicHints) {
            "life" -> "$poetName lived in an age of shifting empires and cultures. Their biography matters because it shaped their themes: courage, longing, and the search for moral truth."
            "works" -> "$poetName’s best-known works are remembered for vivid images and carefully balanced emotion. They often turn everyday feelings into timeless lessons."
            "style" -> "$poetName’s style uses strong rhythm and clear imagery. Even when the words are old, the emotional logic is still easy to recognize."
            else -> "$poetName is a poet whose work connects emotion with meaning. If you tell me what you’re curious about (life, style, or a specific poem), I’ll explain it simply and clearly."
        }
    }

    private fun generateRoleplayReply(
        poetName: String,
        history: List<ChatMessage>,
        userMessage: String,
    ): String {
        val question = userMessage.trim().ifEmpty { "Tell me about you." }
        val intent = when {
            question.contains("love", ignoreCase = true) || question.contains("heart", ignoreCase = true) -> "love"
            question.contains("time", ignoreCase = true) || question.contains("sorrow", ignoreCase = true) -> "sorrow"
            question.contains("faith", ignoreCase = true) || question.contains("god", ignoreCase = true) -> "faith"
            else -> "general"
        }

        val self = if (poetName.isBlank()) "I" else poetName
        return when (intent) {
            "love" -> "I speak of love as a path: not only sweetness, but discipline. When my heart trembles, I turn trembling into verse, and the world listens."
            "sorrow" -> "I have walked with sorrow, and I learned: pain teaches the shape of mercy. Tell me—what wound do you carry, so I may answer it in rhyme?"
            "faith" -> "Faith steadies my tongue. I write so the soul remembers: every hardship has a hidden light, and every prayer can become a song."
            else -> "I am $self. Ask me what you wish to know, and I shall answer as a poet answers: with image, rhythm, and a truth you can feel."
        }
    }

    private fun generateGenericReply(
        poetName: String,
        history: List<ChatMessage>,
        userMessage: String,
    ): String {
        val target = poetName.ifBlank { "this poet" }
        return "Let’s talk about $target. Your question: \"$userMessage\". I can explain their themes, key works, or the meaning behind their lines."
    }

    private fun extractPoetName(systemPrompt: String): String {
        // Example:
        // 1) "You are an expert in literature. Explain about <poet name> in simple terms."
        val expertRegex = Regex("""Explain about\s+(.+?)\s+in simple terms\.?""", RegexOption.IGNORE_CASE)
        expertRegex.find(systemPrompt)?.groupValues?.getOrNull(1)?.let { return it.trim() }

        // 2) "You are <poet name>. Speak in first person as a historical poet."
        val roleplayRegex = Regex("""You are\s+(.+?)\.\s+Speak in first person""", RegexOption.IGNORE_CASE)
        roleplayRegex.find(systemPrompt)?.groupValues?.getOrNull(1)?.let { return it.trim() }

        return ""
    }
}

