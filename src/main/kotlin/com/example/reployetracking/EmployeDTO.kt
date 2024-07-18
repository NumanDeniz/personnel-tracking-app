package com.example.reployetracking

import java.time.LocalDateTime
import java.util.*

data class EmployeDTO(
        val id: Long,
        val email: String,
        val ad: String,
        val soyad: String,
        val sifre: String,
        val flag: Boolean = false,
        val uuid: String,
        val dogrulama: Boolean = false,
        val giris: LocalDateTime?,
        val cikis: LocalDateTime?
)