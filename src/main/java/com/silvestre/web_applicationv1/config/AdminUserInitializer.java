package com.silvestre.web_applicationv1.config;

import com.silvestre.web_applicationv1.entity.User;
import com.silvestre.web_applicationv1.entity.Venue;
import com.silvestre.web_applicationv1.enums.Role;
import com.silvestre.web_applicationv1.repository.UserRepository;
import com.silvestre.web_applicationv1.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private VenueRepository venueRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin already exists
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .firstname("Admin")
                    .lastname("User")
                    .role(Role.ADMIN)
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .verifiedEmail(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Admin user created");
        } else {
            System.out.println("Admin user already exists");
        }
        if (userRepository.findByEmail("planner1@example.com").isEmpty()) {
            User planner1 = User.builder()
                    .email("planner1@example.com")
                    .password(passwordEncoder.encode("planner123"))
                    .firstname("Planner")
                    .lastname("One")
                    .role(Role.PLANNER)
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .verifiedEmail(true)
                    .build();
            userRepository.save(planner1);
            System.out.println("Planner 1 user created");
        } else {
            System.out.println("Planner 1 user already exists");
        }

// Planner 2
        if (userRepository.findByEmail("planner2@example.com").isEmpty()) {
            User planner2 = User.builder()
                    .email("planner2@example.com")
                    .password(passwordEncoder.encode("planner123"))
                    .firstname("Planner")
                    .lastname("Two")
                    .role(Role.PLANNER)
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .verifiedEmail(true)
                    .build();
            userRepository.save(planner2);
            System.out.println("Planner 2 user created");
        } else {
            System.out.println("Planner 2 user already exists");
        }

// Customer user
        if (userRepository.findByEmail("customer@example.com").isEmpty()) {
            User customer = User.builder()
                    .email("customer@example.com")
                    .password(passwordEncoder.encode("customer123"))
                    .firstname("Customer")
                    .lastname("User")
                    .role(Role.CUSTOMER)
                    .enabled(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .verifiedEmail(true)
                    .build();
            userRepository.save(customer);
            System.out.println("Customer user created");
        } else {
            System.out.println("Customer user already exists");
        }

        if(venueRepository.findAll().isEmpty()){

            Venue venue = new Venue();
            venue.setAddress("333 Pulilan - Calumpit Regional Road, Sto. Nino, Calumpit, 3003 Bulacan");
            venue.setName("Leticia's Garden Resort & Events Place");

            Venue venue2= new Venue();
            venue2.setName("The Chapters, MDSF Social Hall");
            venue2.setAddress("Tangos, Baliwag, Bulacan");

            venueRepository.saveAll(List.of(venue,venue2));
        }
    }
}
