package com.example.reployetracking

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["http://localhost:3000"], allowCredentials = "true")

class EmployeController(
        private val employeeService: EmployeService,
        private val emailService: EmailService,
        private val qrCodeService: QRCodeService
) {

    @GetMapping("/employees")
    fun getAllEmployees(): ResponseEntity<List<EmployeDTO>> {
        val employees = employeeService.getAllEmployees()
        return ResponseEntity.ok(employees)
    }

    @PostMapping("/employees")
    fun registerEmployee(@RequestBody employeeDTO: EmployeDTO): ResponseEntity<EmployeDTO> {
        val savedEmployee = employeeService.saveEmployee(employeeDTO)
        val verificationLink = "http://localhost:3000/set-password/${savedEmployee.uuid}/${savedEmployee.ad}/${savedEmployee.soyad}"
        emailService.sendVerificationEmail(savedEmployee.email, verificationLink)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee)
    }

    @PostMapping("/employees/set-password/{uuid}/{ad}/{soyad}")
    fun setPasswordByUUID(
            @PathVariable uuid: String,
            @PathVariable ad: String,
            @PathVariable soyad: String,
            @RequestBody body: Map<String, String>
    ): ResponseEntity<Any> {
        val sifre = body["password"]
        if (sifre == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is required")
        }

        return try {
            val updatedEmployee = employeeService.setPasswordByUUID(uuid, sifre)
            if (updatedEmployee != null) {
                ResponseEntity.ok(updatedEmployee)
            } else {
                ResponseEntity.status(HttpStatus.FORBIDDEN).body("Link has already been used.")
            }
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }

//    @PostMapping("/employees/set-password")
//    fun setPasswordByUUID(@RequestBody createUserPassword: CreateUserPassword): ResponseEntity<Any> {
//        return try {
//            val updatedEmployee = employeeService.setPasswordByUUID(createUserPassword.uuid, createUserPassword.sifre)
//            if (updatedEmployee != null) {
//                ResponseEntity.ok(updatedEmployee)
//            } else {
//                ResponseEntity.status(HttpStatus.FORBIDDEN).body("Link has already been used.")
//            }
//        } catch (e: RuntimeException) {
//            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
//        }
//    }

    @PostMapping("/login")
    fun login(@RequestBody loginDTO: LoginDTO): ResponseEntity<Any> {
        val employee = employeeService.findByEmail(loginDTO.email)
        return if (employee != null) {
            if (loginDTO.password == employee.sifre) {
                // Send QR code
                val qrCode = employeeService.generateQrCodeAndSendEmail(employee.email)
                ResponseEntity.ok(mapOf("message" to "QR code sent", "qrCode" to qrCode))
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
            }
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
        }
    }

    @PostMapping("/verify-qr")
    fun verifyQrCode(@RequestBody qrCodeDTO: QrCodeDTO): ResponseEntity<Any> {
        return if (employeeService.verifyQrCode(qrCodeDTO.email, qrCodeDTO.qrCode)) {
            ResponseEntity.ok("Login successful")
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid QR code")
        }
    }

    @GetMapping("/current-user")
    fun getCurrentUser(@RequestHeader("email") email: String): ResponseEntity<EmployeDTO?> {
        val employee = employeeService.findByEmail(email)
        return if (employee != null) {
            ResponseEntity.ok(employee.toDTO())
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }


    @GetMapping("/employees/verify-link/{uuid}")
    fun verifyLink(@PathVariable uuid: String): ResponseEntity<Boolean> {
        return try {
            val linkUsed = employeeService.verifyLink(uuid)
            ResponseEntity.ok(linkUsed)
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(false)
        }
    }


    @PostMapping("/employees/verify/{uuid}")
    fun verifyEmployeeUUID(@PathVariable uuid: String): ResponseEntity<String> {
        return try {
            employeeService.verifyEmployeeUUID(uuid)
            ResponseEntity.ok("Employee verified successfully.")
        } catch (e: RuntimeException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.")
        }
    }

    @GetMapping("/employees/{id}")
    fun getEmployeeById(@PathVariable id: Long): ResponseEntity<EmployeDTO?> {
        val employee = employeeService.getEmployeeById(id)
        return if (employee != null) {
            ResponseEntity.ok(employee)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/employees/{id}")
    fun updateEmployee(@PathVariable id: Long, @RequestBody employeeDTO: EmployeDTO): ResponseEntity<EmployeDTO> {
        val updatedEmployee = employeeService.updateEmployee(id, employeeDTO)
        return ResponseEntity.ok(updatedEmployee)
    }

    @DeleteMapping("/employees/{id}")
    fun deleteEmployee(@PathVariable id: Long): ResponseEntity<Void> {
        employeeService.deleteEmployee(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/employees/invite")
    fun inviteEmployee(@RequestBody employeeDTO: EmployeDTO): ResponseEntity<EmployeDTO> {
        val savedEmployee = employeeService.saveEmployee(employeeDTO)
        val verificationLink = "http://localhost:3000/verify?email=${savedEmployee.email}"
        emailService.sendVerificationEmail(savedEmployee.email, verificationLink)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee)
    }

    @GetMapping("/employees/verify")
    fun verifyEmployee(@RequestParam email: String): ResponseEntity<String> {
        val employee = employeeService.findByEmail(email)
        return if (employee != null) {
            employee.flag = true
            employeeService.saveEmployee(employee.toDTO())
            ResponseEntity.ok("Employee verified successfully.")
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found.")
        }
    }
}
data class LoginDTO(
        val email: String,
        val password: String
)
data class QrCodeDTO(
        val email: String,
        val qrCode: String)