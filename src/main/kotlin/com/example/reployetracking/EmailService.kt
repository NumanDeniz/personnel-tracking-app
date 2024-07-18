package com.example.reployetracking

import org.slf4j.LoggerFactory
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(private val mailSender: JavaMailSender) {
    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    fun sendVerificationEmail(to: String, verificationLink: String) {
        val subject = "Account Verification"
        val text = "Please click the link to verify your account: $verificationLink"

        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject(subject)
        message.setText(text)

        mailSender.send(message)

        // Log success message
        logger.info("Verification email sent successfully to: $to")
    }
}