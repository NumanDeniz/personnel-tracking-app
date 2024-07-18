package com.example.reployetracking

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class EmployeService(private val employeeRepository: EmployeRepository) {

    fun getAllEmployees(): List<EmployeDTO> {
        return employeeRepository.findAll().map { it.toDTO() }
    }

    fun getEmployeeById(id: Long): EmployeDTO? {
        val employee = employeeRepository.findById(id)
        return employee.orElse(null)?.toDTO()
    }

    fun saveEmployee(employeeDTO: EmployeDTO): EmployeDTO {
        val employee = Employee(
                email = employeeDTO.email,
                ad = employeeDTO.ad,
                soyad = employeeDTO.soyad,
                sifre = "", // Password left blank, can be added as needed.
                flag = employeeDTO.flag,
                uuid = employeeDTO.uuid
        )
        val savedEmployee = employeeRepository.save(employee)
        return savedEmployee.toDTO()
    }

    fun updateEmployee(id: Long, employeeDTO: EmployeDTO): EmployeDTO {
        val existingEmployee = employeeRepository.findById(id)
                .orElseThrow { RuntimeException("Employee with ID $id not found.") }

        existingEmployee.ad = employeeDTO.ad
        existingEmployee.soyad = employeeDTO.soyad
        existingEmployee.email = employeeDTO.email
        existingEmployee.flag = employeeDTO.flag

        val updatedEmployee = employeeRepository.save(existingEmployee)
        return updatedEmployee.toDTO()
    }

    fun verifyEmployeeUUID(uuid: String) {
        val employee = employeeRepository.findByUuid(uuid)
                ?: throw RuntimeException("Employee with UUID $uuid not found.")
        employee.dogrulama = true
        employeeRepository.save(employee)
    }

    fun deleteEmployee(id: Long) {
        employeeRepository.deleteById(id)
    }

    fun findByEmail(email: String): Employee? {
        return employeeRepository.findByEmail(email)
    }

    fun verifyLink(uuid: String): Boolean {
        val employee = employeeRepository.findByUuid(uuid)
                ?: throw RuntimeException("Invalid link")

        // Check if the link has already been used
        return employee.dogrulama
    }

    fun setPasswordByUUID(uuid: String, sifre: String): EmployeDTO? {
        try {
            val employee = employeeRepository.findByUuid(uuid)
                    ?: throw RuntimeException("Invalid link")

            if (employee.dogrulama) {
                return null
            }

            employee.sifre = sifre
            employee.flag = true
            employee.dogrulama = true // Mark link as used
            val updatedEmployee = employeeRepository.save(employee)
            return updatedEmployee.toDTO()
        } catch (e: Exception) {
            println("Error setting password for UUID $uuid: ${e.message}")
            throw e
        }
    }
    fun logLogin(userId: Long) {
        val user = employeeRepository.findById(userId).orElseThrow { Exception("User not found") }
        user.giris = LocalDateTime.now()
        employeeRepository.save(user)
    }

    fun logLogout(userId: Long) {
        val user = employeeRepository.findById(userId).orElseThrow { Exception("User not found") }
        user.cikis = LocalDateTime.now()
        employeeRepository.save(user)
    }


    fun getLogs(): List<Employee> {
        return employeeRepository.findAll()
    }


    private fun Employee.toDTO(): EmployeDTO {
        return EmployeDTO(id,email, ad, soyad, sifre, flag, uuid, giris =this.giris , cikis =this.cikis )
    }
}
