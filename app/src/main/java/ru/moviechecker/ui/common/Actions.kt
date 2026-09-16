package ru.moviechecker.ui.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import ru.moviechecker.ui.movie.MovieCardModel

fun openInBrowser(context: Context, uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)

    startActivity(context, intent)
}

fun openInKinopoisk(context: Context, card: MovieCardModel) {
    val intent = Intent(Intent.ACTION_VIEW)
    val packageName = "ru.kinopoisk" // Пакетное имя приложения «Кинопоиск»

    // Проверяем, установлено ли приложение
    try {
        val packageManager = context.packageManager
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))

        // Приложение установлено — указываем его явно
        intent.setPackage(packageName)
        // TODO: разобраться с открытием приложения -
        //  открытие через hd.kinopoisk.ru не происходит
        intent.data = "https://kinopoisk.ru/film/${card.kinopoiskId}".toUri()
    } catch (e: PackageManager.NameNotFoundException) {
        // Приложение не установлено — оставляем intent без setPackage, откроется браузер
        intent.data =
            "https://hd.kinopoisk.ru/film/${card.kinopoiskId}?content_tab=series&season=${card.season.number}&episode=${card.episode.number}&watch=".toUri()
    }

    startActivity(context, intent)
}

private fun startActivity(context: Context, intent: Intent) {

    // Добавляем флаги для корректной работы
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        // Если не удалось запустить ни приложение, ни браузер
        Toast.makeText(context, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show()
    }
}