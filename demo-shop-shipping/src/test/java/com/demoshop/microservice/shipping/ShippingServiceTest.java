package com.demoshop.microservice.shipping;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.demoshop.microservice.shipping.Address;
import com.demoshop.microservice.shipping.Customer;
import com.demoshop.microservice.shipping.Shipment;
import com.demoshop.microservice.shipping.ShipmentLine;
import com.demoshop.microservice.shipping.ShipmentRepository;
import com.demoshop.microservice.shipping.ShipmentService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ShippingTestApp.class, webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class ShippingServiceTest {

	@Autowired
	private ShipmentRepository shipmentRepository;

	@Autowired
	private ShipmentService shipmentService;

	@Test
	public void ensureIdempotencySecondCallIgnored() {
		long countBefore = shipmentRepository.count();
		Shipment shipment = new Shipment(42L,
				new Customer(23L, "Mary", "Jane"),
				new Date(0L), new Address("Newtown Street", "23499", "London"),
				new ArrayList<ShipmentLine>(),"DHL");
		shipmentService.ship(shipment);
		assertThat(shipmentRepository.count(), is(countBefore + 1));
		assertThat(shipmentRepository.findById(42L).get().getUpdated().getTime(), equalTo(0L));
		shipment = new Shipment(42,
				new Customer(23L, "Mary", "Jane"),
				new Date(), new Address("Newtown Street", "23499", "London"), new ArrayList<ShipmentLine>(),"DHL");
		shipmentService.ship(shipment);
		assertThat(shipmentRepository.count(), is(countBefore + 1));
		assertThat(shipmentRepository.findById(42L).get().getUpdated().getTime(), equalTo(0L));
	}


	@Test
	public void ensureShipmentRateCalculted() {
		Shipment shipment = new Shipment(43L,
				new Customer(23L, "Mary", "Jane"),
				new Date(0L), new Address("Newtown Street", "23499", "London"),
				new ArrayList<ShipmentLine>(),"DHL");
		shipmentService.ship(shipment);
		assertThat(shipment.getCost(), is(1));
	}

	@Test(expected=IllegalArgumentException.class)
	public void ensureUnkownShipmentError() {
		Shipment shipment = new Shipment(44L,
				new Customer(23L, "Mary", "Jane"),
				new Date(0L), new Address("Newtown Street", "23499", "London"),
				new ArrayList<ShipmentLine>(),"Unkown Service");
		shipmentService.ship(shipment);
	}

	
	
}
