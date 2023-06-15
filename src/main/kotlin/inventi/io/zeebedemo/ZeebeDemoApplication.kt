package inventi.io.zeebedemo

import io.camunda.zeebe.spring.client.annotation.Deployment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@Deployment(resources = ["classpath:/workflows/*.bpmn", "classpath:/decisions/*.dmn"])
class ZeebeDemoApplication

fun main(args: Array<String>) {
	runApplication<ZeebeDemoApplication>(*args)
}
