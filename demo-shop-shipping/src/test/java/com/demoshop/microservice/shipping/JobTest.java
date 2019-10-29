package com.demoshop.microservice.shipping;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.demoshop.microservice.shipping.ShipmentRepository;
import com.demoshop.microservice.shipping.job.ShippingJob;

import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;

@ExtendWith(SpringExtension.class)
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(classes = ShippingTestApp.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@PactTestFor(providerName = "OrderProvider", port = "8081")
@ActiveProfiles("test")
public class JobTest {

	@Autowired
	private ShipmentRepository shipmentRepository;

	@Autowired
	private ShippingJob shippingJob;

	private DslPart feedBody(Date now) {
		return new PactDslJsonBody().date("updated", "yyyy-MM-dd'T'kk:mm:ss.SSS+0000", now)
									.eachLike("orders")
									.numberType("id", 1)
									.stringType("link", "http://localhost:8081/order/1")
									.date("updated", "yyyy-MM-dd'T'kk:mm:ss.SSS+0000", now)
									.closeArray();
	}

	public DslPart order(Date now) {
		return new PactDslJsonBody().numberType("id", 1)
									.numberType("numberOfLines", 1)
									.stringType("deliveryService", "DHL")
									.object("customer")
									.numberType("customerId", 1)
									.stringType("name", "Jane")
									.stringType("firstname", "Mary")
									.stringType("email", "mary@mymail.net")
									.closeObject()
									.object("shippingAddress")
									.stringType("street", "Newtown Street")
									.stringType("zip", "23499")
									.stringType("city", "London")
									.closeObject()
									.array("orderLine")
									.object()
									.numberType("count", 42)
									.object("item")
									.numberType("itemId", 1)
									.stringType("name", "Dell Latitude")
									.closeObject()
									.closeArray();
	}

	@Pact(consumer = "Shipping")
	public RequestResponsePact createFragment(PactDslWithProvider builder) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		Date now = new Date();
		return builder	.uponReceiving("Request for order feed")
						.method("GET")
						.path("/feed")
						.willRespondWith()
						.status(200)
						.headers(headers)
						.body(feedBody(now))
						.uponReceiving("Request for an order")
						.method("GET")
						.path("/order/1")
						.willRespondWith()
						.status(200)
						.headers(headers)
						.body(order(now))
						.toPact();
	}

	@Test
	public void orderArePolled() {
		long countBeforePoll = shipmentRepository.count();
		shippingJob.pollInternal();
		assertThat(shipmentRepository.count(), is(greaterThan(countBeforePoll)));
	}

}
