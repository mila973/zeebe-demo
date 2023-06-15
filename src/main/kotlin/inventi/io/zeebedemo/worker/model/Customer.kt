package inventi.io.zeebedemo.worker.model

import java.time.LocalDate

data class Customer(
        val id: String,
        val name: String,
        val surname: String,
        val birthDate: LocalDate,
        val email: String,
        val phone: String,
        val personalCode: String,
        val accounts: List<String>
)
