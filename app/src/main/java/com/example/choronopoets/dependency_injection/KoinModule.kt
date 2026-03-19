package com.example.choronopoets.dependency_injection

import com.example.choronopoets.PoetryRepository
import com.example.choronopoets.data.AppPreferences
import com.example.choronopoets.data.PoetryDatabase
import com.example.choronopoets.data.remote.AiRepository
import com.example.choronopoets.data.remote.RetrofitAiService
import com.example.choronopoets.data.remote.gemini.GeminiApi
import com.example.choronopoets.data.remote.gemini.GeminiRepository
import com.example.choronopoets.data.tajik.TajikPoetsRepositoryImpl
import com.example.choronopoets.domain.ai.AiService
import com.example.choronopoets.domain.ai.ChatMode
import com.example.choronopoets.domain.repositories.TajikPoetsRepository
import com.example.choronopoets.viewmodel.AiSessionViewerViewModel
import com.example.choronopoets.viewmodel.ChatHistoryViewModel
import com.example.choronopoets.viewmodel.FavoritesViewModel
import com.example.choronopoets.viewmodel.HomeViewModel
import com.example.choronopoets.viewmodel.PoemDetailViewModel
import com.example.choronopoets.viewmodel.PoemGeneratorViewModel
import com.example.choronopoets.viewmodel.PoetChatViewModel
import com.example.choronopoets.viewmodel.PoetDetailsViewModel
import com.example.choronopoets.viewmodel.PoetPoemGenerateViewModel
import com.example.choronopoets.viewmodel.PoetryViewModel
import com.example.choronopoets.viewmodel.SettingsViewModel
import com.example.choronopoets.viewmodel.TajikPoetDetailViewModel
import com.example.choronopoets.viewmodel.TajikPoemsViewModel
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    // ── Room ──────────────────────────────────────────────────────────────────
    single { PoetryDatabase.getInstance(get()) }
    single { get<PoetryDatabase>().centuryDao() }
    single { get<PoetryDatabase>().poetDao() }
    single { get<PoetryDatabase>().poemDao() }
    single { get<PoetryDatabase>().favoritesDao() }
    single { get<PoetryDatabase>().generatedFavoritesDao() }
    single { get<PoetryDatabase>().chatHistoryDao() }
    single { PoetryRepository(get(), get(), get()) }
    viewModel { PoetryViewModel(get()) }

    // ── Preferences ───────────────────────────────────────────────────────────
    single { AppPreferences(androidContext()) }

    // ── Tajik static data ─────────────────────────────────────────────────────
    single<TajikPoetsRepository> { TajikPoetsRepositoryImpl() }

    // ── OkHttpClient ──────────────────────────────────────────────────────────
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    // ── Gemini Retrofit ───────────────────────────────────────────────────────
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    single<GeminiApi> { get<Retrofit>().create(GeminiApi::class.java) }
    single { GeminiRepository(get()) }
    single { AiRepository(get()) }
    single<AiService> { RetrofitAiService(get()) }

    // ── ViewModels ────────────────────────────────────────────────────────────
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ChatHistoryViewModel(get(), get(), get()) }
    viewModel { (sessionKey: String) -> AiSessionViewerViewModel(sessionKey, get()) }
    viewModel { PoemGeneratorViewModel(get(), get(), get()) }
    viewModel { FavoritesViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }

    viewModel { (poetId: Int) ->
        PoetDetailsViewModel(
            poetId = poetId,
            poetryRepository = get(),
            aiRepository = get(),
            chatHistoryDao = get(),
        )
    }

    viewModel { (poetKey: String) ->
        TajikPoetDetailViewModel(
            poetKey = poetKey,
            tajikPoetsRepository = get(),
            aiRepository = get(),
        )
    }

    viewModel { (poetSource: String, poetId: Int?, poetKey: String?, mode: ChatMode) ->
        PoetChatViewModel(
            poetSource = poetSource,
            poetId = poetId,
            poetKey = poetKey,
            mode = mode,
            aiService = get(),
            poetryRepository = get(),
            tajikPoetsRepository = get(),
            chatHistoryDao = get(),
        )
    }

    viewModel { (poetId: Int) ->
        PoetPoemGenerateViewModel(
            poetId = poetId,
            aiRepository = get(),
            poetryRepository = get(),
            generatedFavoritesDao = get(),
            chatHistoryDao = get(),
        )
    }

    viewModel { (roomPoetId: Int) ->
        TajikPoemsViewModel(roomPoetId = roomPoetId, poemDao = get())
    }

    viewModel { (poemId: Int) ->
        PoemDetailViewModel(
            poemId = poemId,
            poemDao = get(),
            favoritesDao = get(),
            aiRepository = get(),
            chatHistoryDao = get(),
        )
    }
}
