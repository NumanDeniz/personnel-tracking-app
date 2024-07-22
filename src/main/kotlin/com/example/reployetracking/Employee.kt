package com.example.reployetracking

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "employee")
data class Employee(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,

        @Column(name = "email")
        var email: String,

        @Column(name = "ad")
        var ad: String,

        @Column(name = "soyad")
        var soyad: String,

        @Column(name = "sifre")
        var sifre: String,

        @Column(name = "flag")
        var flag: Boolean = false,

        @Column(name = "uuid")
        val uuid: String = UUID.randomUUID().toString(),

        @Column(name = "dogrulama")
        var dogrulama: Boolean = false,

        @Column(name = "giris_saati")
        var giris: LocalDateTime? = null,

        @Column(name = "cikis_saati")
        var cikis: LocalDateTime? = null


) {
        fun toDTO(): EmployeDTO {
                return EmployeDTO(id,email, ad, soyad, sifre, flag, uuid, dogrulama, giris, cikis)
        }
}