package ru.thetrapnest.security.database

import android.content.Context
import kotlinx.coroutines.flow.Flow
import ru.thetrapnest.security.data.Vulnerability
import ru.thetrapnest.security.data.UserProgress
import ru.thetrapnest.security.data.VulnerabilityType

class SecurityRepository(context: Context) {
    private val database = SecurityDatabase.getDatabase(context)
    private val vulnerabilityDao = database.vulnerabilityDao()
    private val userProgressDao = database.userProgressDao()

    fun getAllVulnerabilities(): Flow<List<VulnerabilityEntity>> {
        return vulnerabilityDao.getAllVulnerabilities()
    }

    fun getVulnerabilityById(id: Int): Flow<VulnerabilityEntity?> {
        return vulnerabilityDao.getVulnerabilityById(id)
    }

    fun getAllProgress(): Flow<List<UserProgressEntity>> {
        return userProgressDao.getAllProgress()
    }

    suspend fun getProgressByVulnerabilityId(vulnerabilityId: Int): UserProgressEntity? {
        return userProgressDao.getProgressByVulnerabilityId(vulnerabilityId)
    }

    suspend fun insertVulnerabilities(vulnerabilities: List<VulnerabilityEntity>) {
        vulnerabilityDao.insertVulnerabilities(vulnerabilities)
    }

    suspend fun insertProgress(progress: UserProgressEntity) {
        userProgressDao.insertProgress(progress)
    }

    suspend fun updateProgress(progress: UserProgressEntity) {
        userProgressDao.updateProgress(progress)
    }

    suspend fun getCompletedCount(): Int {
        return userProgressDao.getCompletedCount()
    }

    suspend fun initializeDefaultData() {
        // Check if we already have data
        val existingVulnerabilities = vulnerabilityDao.getVulnerabilitiesCount()
        if (existingVulnerabilities == 0) {
            // Insert default vulnerabilities
            val defaultVulnerabilities = listOf(
                VulnerabilityEntity(
                    id = 1,
                    title = "Cross-Site Scripting (XSS)",
                    description = "A type of security vulnerability that allows attackers to inject malicious scripts into web pages viewed by other users.",
                    type = VulnerabilityType.XSS,
                    theory = "XSS occurs when a web application incorporates untrusted data into web pages without proper validation or escaping. This allows attackers to execute malicious scripts in the victim's browser.",
                    example = "Example: <script>alert('XSS Attack!')</script>",
                    hint = "Try injecting a script tag into the input field."
                ),
                VulnerabilityEntity(
                    id = 2,
                    title = "SQL Injection (SQLi)",
                    description = "A code injection technique that exploits vulnerabilities in an application's software by inserting malicious SQL statements into an entry field.",
                    type = VulnerabilityType.SQLI,
                    theory = "SQL injection occurs when user input is not properly sanitized before being included in SQL queries. This allows attackers to manipulate the query structure and potentially access, modify, or delete data.",
                    example = "Example: ' OR '1'='1",
                    hint = "Try entering a condition that is always true."
                )
            )
            vulnerabilityDao.insertVulnerabilities(defaultVulnerabilities)
        }
    }
}