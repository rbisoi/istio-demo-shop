package com.demoshop.microservice.shipping.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.demoshop.microservice.shipping.job.ShippingJob;

@Controller
public class JobController {

	private ShippingJob poller;

	@Autowired
	public JobController(ShippingJob poller) {
		this.poller = poller;
	}

	@RequestMapping(value = "/poll", method = RequestMethod.POST)
	public String poll() {
		poller.poll();
		return "redirect:/";
	}

}
