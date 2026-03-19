package com.example.choronopoets.utils

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) {

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var pendingText: String? = null
    private var _moodHintText: String = ""

    private val progressListener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            _isSpeaking.value = true
            _error.value = null
        }

        override fun onDone(utteranceId: String?) {
            _isSpeaking.value = false
        }

        override fun onError(utteranceId: String?, errorCode: Int) {
            _isSpeaking.value = false
            _error.value = "Ошибка воспроизведения (код $errorCode)"
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
            _isSpeaking.value = false
        }
    }

    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.setOnUtteranceProgressListener(progressListener)
                val langOk = trySetLanguage()
                if (langOk) {
                    _isReady.value = true
                    pendingText?.let {
                        pendingText = null
                        internalSpeak(it, _moodHintText)
                    }
                }
            } else {
                _error.value = "TTS движок недоступен на этом устройстве"
            }
        }
    }

    private fun trySetLanguage(): Boolean {
        val candidates = listOf(
            Locale("ru", "RU"),
            Locale("ru"),
            Locale.ENGLISH,
            Locale.getDefault(),
        )
        for (locale in candidates) {
            val result = tts.setLanguage(locale)
            if (result >= TextToSpeech.LANG_AVAILABLE) return true
        }
        _error.value = "Голосовой пакет не установлен. Перейдите: Настройки → Язык и ввод → Синтез речи → установите русский голос."
        return false
    }

    /** [title] используется только для анализа настроения, не озвучивается */
    fun speak(text: String, title: String = "") {
        val clean = cleanText(text)
        if (clean.isBlank()) return
        if (!_isReady.value) {
            pendingText = clean
            _moodHintText = title + " " + clean
            return
        }
        _error.value = null
        internalSpeak(clean, title + " " + clean)
    }

    fun stop() {
        tts.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
        _isSpeaking.value = false
        _isReady.value = false
    }

    /** [moodContext] — полный текст (название + содержание) для анализа настроения */
    private fun internalSpeak(text: String, moodContext: String = text) {
        if (text.isBlank()) return
        val (pitch, rate) = analyzeTextMood(moodContext)
        tts.setPitch(pitch)
        tts.setSpeechRate(rate)
        val result = tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
        if (result == TextToSpeech.ERROR) {
            _error.value = "Не удалось воспроизвести. Проверьте: Настройки → Язык и ввод → Синтез речи → установите русский голос."
            _isSpeaking.value = false
        }
    }

    private fun cleanText(text: String): String =
        text
            .replace(Regex("[*_#`~>]"), "")
            .replace("—", ",")
            .replace("–", ",")
            .replace(Regex("[\u00AB\u00BB\u201C\u201D\u201E]"), "")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

    /**
     * Basic mood detection → adjusts pitch and speech rate.
     * Sad/lyrical → lower & slower. Epic → higher & faster. Romantic → soft & gentle.
     */
    private fun analyzeTextMood(text: String): Pair<Float, Float> {
        val lower = text.lowercase()
        val sad = listOf("тоска", "печаль", "слёзы", "грусть", "смерть", "умер", "могила", "ночь", "одинок")
        val epic = listOf("герой", "битва", "победа", "слава", "великий", "народ", "воин", "богатырь")
        val romantic = listOf("любовь", "сердце", "нежн", "ласк", "красот", "роза", "весна", "поцелу")

        val sadScore = sad.count { lower.contains(it) }
        val epicScore = epic.count { lower.contains(it) }
        val romanticScore = romantic.count { lower.contains(it) }

        return when {
            sadScore >= 2     -> Pair(0.85f, 0.80f)
            epicScore >= 2    -> Pair(1.10f, 1.05f)
            romanticScore >= 2 -> Pair(0.95f, 0.88f)
            else              -> Pair(1.0f, 0.90f)
        }
    }

    companion object {
        private const val UTTERANCE_ID = "poem_tts"
    }
}
