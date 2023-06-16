package inventi.io.zeebedemo.worker

import inventi.io.zeebedemo.worker.model.Customer
import inventi.io.zeebedemo.worker.model.Incident
import inventi.io.zeebedemo.worker.model.Transaction
import io.camunda.zeebe.spring.client.annotation.JobWorker
import io.camunda.zeebe.spring.client.annotation.VariablesAsType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CreateIncidentWorker {

    private val logger: Logger = LoggerFactory.getLogger(CreateIncidentWorker::class.java)

    @JobWorker(type = "create-incident", name = "Create incident worker")
    fun handle(@VariablesAsType input: CreateIncidentInput): CreateIncidentOutput {
        val transactionId = input.transaction.id
        val customerId = input.customer.id
        logger.info("Creating incident for customer $customerId for transaction $transactionId")
        return CreateIncidentOutput(incident = Incident(transactionId = transactionId, customerId = customerId))
    }
}

data class CreateIncidentOutput(
        val incident: Incident
)

data class CreateIncidentInput(
        val transaction: Transaction,
        val customer: Customer,
)
