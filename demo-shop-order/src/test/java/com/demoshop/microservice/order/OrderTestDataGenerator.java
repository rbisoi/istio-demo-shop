package com.demoshop.microservice.order;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.demoshop.microservice.order.customer.CustomerRepository;
import com.demoshop.microservice.order.item.ItemRepository;
import com.demoshop.microservice.order.logic.Address;
import com.demoshop.microservice.order.logic.Order;
import com.demoshop.microservice.order.logic.OrderRepository;

@Component
@Profile("test")
@DependsOn({ "itemTestDataGenerator", "customerTestDataGenerator" })
public class OrderTestDataGenerator {

	private final OrderRepository orderRepository;
	private ItemRepository itemRepository;
	private CustomerRepository customerRepository;

	@Autowired
	public OrderTestDataGenerator(OrderRepository orderRepository, ItemRepository itemRepository,
			CustomerRepository customerRepository) {
		this.orderRepository = orderRepository;
		this.itemRepository = itemRepository;
		this.customerRepository = customerRepository;
	}

	@PostConstruct
	public void generateTestData() {
		Order order = new Order(customerRepository.findAll().iterator().next(),1);
		order.setShippingAddress(new Address("Newtown Street", "23499", "London"));
		order.setBillingAddress(new Address("Westend Rd", "33688", "Paris"));
		order.setDeliveryService("UPS");
		order.addLine(42, itemRepository.findAll().iterator().next());
		order=orderRepository.save(order);
		orderRepository.save(order);
	}

}
