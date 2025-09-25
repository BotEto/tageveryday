package com.jew.tageveryday.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.ZonedDateTime

@Entity
data class TelegramUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var telegramId: Long? = null,

    var nextNotification: ZonedDateTime? = null,
    )
