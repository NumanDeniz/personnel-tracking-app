package com.example.reployetracking

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeRepository : JpaRepository<Employee, Long> {
    fun findByEmail(email: String): Employee?
    fun findByUuid(uuid: String): Employee?
}