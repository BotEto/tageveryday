package com.jew.tageveryday.service

import com.jew.tageveryday.model.TelegramUser
import com.jew.tageveryday.repository.TelegramUserRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.ravel.rtelebots.AnnotatedUpdatesListener
import ru.ravel.rtelebots.TelegramBot
import ru.ravel.rtelebots.annotation.ContentType
import ru.ravel.rtelebots.annotation.MessageHandler
import ru.ravel.rtelebots.builder.SendMessageBuilder
import ru.ravel.rtelebots.model.Message
import ru.ravel.rtelebots.model.Update
import ru.ravel.rtelebots.model.request.InlineKeyboardButton
import ru.ravel.rtelebots.request.GetUpdates
import ru.ravel.rtelebots.request.SendMessage
import ru.ravel.rtelebots.utility.kotlin.extension.request.sendMessage
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@Service
class TelegramService(
    private val telegrambot: TelegramBot = TelegramBot("TOKEN"),
    private val telegramUserRepository: TelegramUserRepository,
) {
    init {
        val listener = object : AnnotatedUpdatesListener(telegramBot = telegrambot) {
            override fun onUpdate(update: Update) {
            }

            override fun onUpdateAfterAnnotatedMethods(update: Update) {
                when (update.callbackQuery()?.data()?.removePrefix("${update.callbackQuery()?.from()?.id().toString()}_")) {
                    "gotovo" -> {
                        val user = telegramUserRepository.findByTelegramId(
                            update?.callbackQuery()?.from()?.id()?.toLong() ?: -1
                        )
                        user?.nextNotification=ZonedDateTime.now().plusHours(24)
                    }
                    "otlojit" -> { val user = telegramUserRepository.findByTelegramId(
                        update?.callbackQuery()?.from()?.id()?.toLong() ?: -1
                    )
                        user?.nextNotification=ZonedDateTime.now().plusMinutes(30)}
                    else -> {}
                }
            }

            @MessageHandler(value = "/start", contentType = ContentType.TEXT)
            fun startHandler(message: Message): SendMessage {
                if (!telegramUserRepository.existsTelegramUserByTelegramId(message.chat().id())) {
                    telegramUserRepository.save(
                        TelegramUser(
                            telegramId = message.chat().id(),
                            nextNotification = ZonedDateTime.now().plusHours(1),
                        )
                    )
                }
                return SendMessage(message.chat().id(), "ДУБАЙСКАЯ АВАНТЮРА")
            }
        }
        telegrambot.setUpdatesListener(listener)
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    @Async
    fun schedudle() {
        telegramUserRepository.findAll().filter {
            it.nextNotification != null && it.nextNotification!!.isAfter(ZonedDateTime.now())
        }
            .onEach {
                var masturbek = 2.0 + 1
                println("masturbek = $masturbek")
                SendMessageBuilder(telegrambot)
                    .telegramId(it.telegramId)
                    .text("masturbek = $masturbek")
                    .buttons(2,
                        InlineKeyboardButton("gotovo", callbackData = "${it.telegramId}_gotovo"),
                        InlineKeyboardButton("otlojit", callbackData = "${it.telegramId}_otlojit"))
                    .execute()
            }

    }
}