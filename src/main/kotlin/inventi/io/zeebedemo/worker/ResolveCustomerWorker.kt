package inventi.io.zeebedemo.worker

import inventi.io.zeebedemo.worker.model.Customer
import inventi.io.zeebedemo.worker.model.Transaction
import io.camunda.zeebe.spring.client.annotation.JobWorker
import io.camunda.zeebe.spring.client.annotation.VariablesAsType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ResolveCustomerWorker {
    private val logger: Logger = LoggerFactory.getLogger(ResolveCustomerWorker::class.java)

    @JobWorker(type = "resolve-customer", name = "Resolve customer worker")
    fun handle(@VariablesAsType input: ResolveCustomerInput): ResolveCustomerOutput {
        logger.info("Resolving customer for transaction: ${input.transaction.id}")
        return ResolveCustomerOutput(
                customer = Customer(
                        id = "customerId",
                        name = "Homer",
                        surname = "Simpsan",
                        birthDate = LocalDate.parse("1987-04-19"),
                        email = "homer@simpsons.com",
                        phone = "+37066830636",
                        personalCode = "38704198702",
                        accounts = listOf("LT801866524947923828", "LT292746585995968944")
                )
        )
    }
}

data class ResolveCustomerInput(
        val transaction: Transaction
)

data class ResolveCustomerOutput(
    val customer: Customer
)
