package org.microarchitecturovisco.userservice;

import org.microarchitecturovisco.userservice.model.User;
import org.microarchitecturovisco.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {
	@Autowired
	private UserRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception
	{
		List<User> allCustomers = this.repository.findAll();
		System.out.println("Number of customers: " + allCustomers.size());

		User newCustomer = new User(3,
				"example1@example.com",
				"password123",
				"John",
				"Doe");
		System.out.println("Saving new customer...");
		this.repository.save(newCustomer);

		allCustomers = this.repository.findAll();
		System.out.println("Number of customers: " + allCustomers.size());
	}

}
