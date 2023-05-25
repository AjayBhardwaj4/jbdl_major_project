package com.bhardwaj.services.epay;

import com.bhardwaj.services.epay.models.User;
import com.bhardwaj.services.epay.models.UserIdentifier;
import com.bhardwaj.services.epay.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.bhardwaj.services.epay.constants.UserConstants.SERVICE_AUTHORITY;

@SpringBootApplication
public class UserApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User txnServiceUser = User.builder()
				.phoneNumber("txn_service")
				.password(passwordEncoder.encode("txn123"))
				.authorities(SERVICE_AUTHORITY)
				.email("txn@gmail.com")
				.userIdentifier(UserIdentifier.SERVIC_ID)
				.identifierValue("txn123")
				.build();

		userRepository.save(txnServiceUser);
	}
}
