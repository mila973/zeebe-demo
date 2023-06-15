package inventi.io.zeebedemo.worker.model

import java.math.BigDecimal

data class Transaction(
        val id: String,
        val amount: BigDecimal,
        val currency: String,
        val sender: String,
        val senderIban: String,
        val receiver: String,
        val receiverIban: String,
        val description: String
)
