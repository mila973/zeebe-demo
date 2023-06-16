package inventi.io.zeebedemo

import io.camunda.zeebe.client.ZeebeClient
import io.camunda.zeebe.client.api.response.PublishMessageResponse
import io.camunda.zeebe.process.test.api.ZeebeTestEngine
import io.camunda.zeebe.process.test.assertions.BpmnAssert
import io.camunda.zeebe.process.test.extension.ZeebeProcessTest
import io.camunda.zeebe.process.test.inspections.InspectionUtility
import io.camunda.zeebe.spring.test.ZeebeTestThreadSupport
import org.junit.jupiter.api.Test

@ZeebeProcessTest
internal class ProcessTransactionTest {

    private lateinit var client: ZeebeClient

    private lateinit var engine: ZeebeTestEngine

    @Test
    internal fun `does not create incident for Simpson`() {
        // given
        deployWorkflow()
        createResolveCustomersWorker("Simpson")
        createIncidentWorker()

        // when
        val response = createWorkflowInstance("Bomb")

        val processInstance = InspectionUtility.findProcessInstances()
                .withBpmnProcessId("demo_transaction_process")
                .findFirstProcessInstance()
                .get()
        ZeebeTestThreadSupport.setEngineForCurrentThread(engine)
        ZeebeTestThreadSupport.waitForProcessInstanceCompleted(processInstance.processInstanceKey)

        // then
        BpmnAssert.assertThat(response)
                .extractingProcessInstance()
                .hasNoIncidents()
                .isCompleted
                .hasNotPassedElement("create-incident-task")
    }

    @Test
    internal fun `creates incident for transaction with bomb in description`() {
        // given
        deployWorkflow()
        createResolveCustomersWorker("Simpsan")
        createIncidentWorker()

        // when
        val response = createWorkflowInstance("Bomb")

        val processInstance = InspectionUtility.findProcessInstances()
                .withBpmnProcessId("demo_transaction_process")
                .findFirstProcessInstance()
                .get()
        ZeebeTestThreadSupport.setEngineForCurrentThread(engine)
        ZeebeTestThreadSupport.waitForProcessInstanceCompleted(processInstance.processInstanceKey)

        // then
        BpmnAssert.assertThat(response)
                .extractingProcessInstance()
                .hasNoIncidents()
                .isCompleted
                .hasVariableWithValue("incident", mapOf(
                        "transactionId" to "transaction-1",
                        "customerId" to "customerId"
                ))
    }

    private fun createResolveCustomersWorker(customerSurname: String) {
        client.newWorker()
                .jobType("resolve-customer")
                .handler { client, job ->
                    val variables = mapOf(
                        "customer" to mapOf(
                                "id" to "customerId",
                                "name" to "Homer",
                                "surname" to customerSurname,
                                "birthDate" to "1987-04-19",
                                "email" to "homer@simpsons.com",
                                "phone" to "+37066830636",
                                "personalCode" to "38704198702",
                                "accounts" to listOf("LT801866524947923828", "LT292746585995968944")
                        )
                    )
                    client.newCompleteCommand(job.key)
                            .variables(variables)
                            .send()
                            .join()
                }.open()
    }

    private fun createIncidentWorker() {
        client.newWorker()
                .jobType("create-incident")
                .handler { client, job ->
                    val transaction = job.variablesAsMap["transaction"] as Map<*, *>
                    val customer = job.variablesAsMap["customer"] as Map<*, *>
                    val variables = mapOf(
                            "incident" to mapOf(
                                    "transactionId" to transaction["id"],
                                    "customerId" to customer["id"]
                            )
                    )
                    client.newCompleteCommand(job.key)
                            .variables(variables)
                            .send()
                            .join()
                }.open()
    }

    private fun createWorkflowInstance(description: String): PublishMessageResponse {
        return client.newPublishMessageCommand()
                .messageName("TransactionReceived")
                .correlationKey("transaction-1")
                .messageId("transaction-1")
                .variables(
                        mapOf(
                                "transaction" to mapOf(
                                        "id" to "transaction-1",
                                        "amount" to 300.5,
                                        "currency" to "EUR",
                                        "sender" to "Ned Flanders",
                                        "senderIban" to "LT918294275838584539",
                                        "receiver" to "Homer Simpson",
                                        "receiverIban" to "LT292746585995968944",
                                        "description" to description
                                ),
                        )
                )
                .send()
                .join()
    }

    private fun deployWorkflow() {
        client.newDeployResourceCommand()
                .addResourceFromClasspath(BPMN_RESOURCE_PATH)
                .addResourceFromClasspath(DMN_RESOURCE_PATH)
                .send()
                .join()
    }

    companion object {
        const val BPMN_RESOURCE_PATH = "workflows/process-transaction.bpmn"
        const val DMN_RESOURCE_PATH = "decisions/is-fraud.dmn"
        const val BPMN_PROCESS = "demo_transaction_process"
    }
}
