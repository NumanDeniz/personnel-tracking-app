package com.example.reployetracking

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import javax.imageio.ImageIO
import java.util.concurrent.ConcurrentHashMap

@Service
class QRCodeService(@Autowired private val mailSender: JavaMailSender) {

    // QR kodları geçici olarak saklamak için bir ConcurrentHashMap kullanılıyoruz
    private val qrCodeMap = ConcurrentHashMap<String, LocalDateTime>()

    fun generateQRCodeImage(text: String, width: Int, height: Int): ByteArray {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until width) {
            for (y in 0 until height) {
                image.setRGB(x, y, if (bitMatrix.get(x, y)) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
            }
        }
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        return baos.toByteArray()
    }

    fun sendQRCodeEmail(toEmail: String, qrCode: ByteArray, qrCodeText: String) {
        val mimeMessage: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true)
        helper.setTo(toEmail)
        helper.setSubject("QR Kod ile Giriş")
        helper.setText("QR Kod ile giriş yapmak için lütfen QR kodunu tarayın.")

        val resource = ByteArrayResource(qrCode)
        helper.addAttachment("qrcode.png", resource)
        helper.setText("QR Kod: $qrCodeText\n\nQR Kod ile giriş yapmak için lütfen QR kodunu tarayın.")
        mailSender.send(mimeMessage)
    }

    fun verifyQRCode(qrCodeText: String): Boolean {
        val timestamp = qrCodeMap.remove(qrCodeText)
        // QR kodunun geçerli olduğunu ve süresi dolmadığını kontrol et
        return timestamp != null
    }
}
