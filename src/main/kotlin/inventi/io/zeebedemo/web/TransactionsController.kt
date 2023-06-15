package inventi.io.zeebedemo.web

import inventi.io.zeebedemo.worker.model.Transaction
import io.camunda.zeebe.client.ZeebeClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transaction")
class TransactionsController(
        private val zeebeClient: ZeebeClient
) {

    @PostMapping
    fun importTransaction(
            @RequestBody transaction: Transaction
    ) {
        zeebeClient.newPublishMessageCommand()
                .messageName("TransactionReceived")
                .correlationKey(transaction.id)
                .messageId(transaction.id)
                .variables(TransactionReceivedOutput(transaction))
                .send()
                .join()
    }
}

data class TransactionReceivedOutput(
        val transaction: Transaction
)
