package com.demoshop.microservice.invoicing.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.demoshop.microservice.invoicing.job.InvoiceJob;

@Controller
public class JobController {

	private InvoiceJob poller;

	@Autowired
	public JobController(InvoiceJob poller) {
		this.poller = poller;
	}

	@RequestMapping(value = "/poll", method = RequestMethod.POST)
	public String poll() {
		poller.poll();
		return "redirect:/";
	}

}
