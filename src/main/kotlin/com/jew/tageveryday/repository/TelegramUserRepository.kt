package com.jew.tageveryday.repository

import com.jew.tageveryday.model.TelegramUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TelegramUserRepository: JpaRepository<TelegramUser, Long> {
    fun existsTelegramUserByTelegramId(telegramId: Long): Boolean
    fun findByTelegramId(telegramId: Long): TelegramUser?
}
