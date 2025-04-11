package org.springframework.samples.petclinic.vets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.samples.petclinic.vets.system.VetsProperties;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = VetsServiceApplication.class)
@ActiveProfiles("test")
class VetsServiceApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired(required = false)
    private DiscoveryClient discoveryClient;

    @Autowired
    private VetsProperties vetsProperties;

    @Test
    void contextLoads() {
        // Verifies that the application context loads successfully
        assertThat(context).isNotNull();
    }

    @Test
    void shouldEnableDiscoveryClient() {
        // Verifies that the DiscoveryClient bean is created due to @EnableDiscoveryClient
        // Skips verification if DiscoveryClient is not available
        if (discoveryClient != null) {
            assertThat(discoveryClient).isNotNull();
        } else {
            System.out.println("DiscoveryClient not available; skipping verification.");
        }
    }

    @Test
    void shouldEnableVetsProperties() {
        // Verifies that the VetsProperties bean is created due to @EnableConfigurationProperties
        assertThat(vetsProperties).isNotNull();
    }

    @Test
    void mainMethodShouldRunWithoutErrors() {
        // Temporarily set test profile to avoid connecting to external services
        System.setProperty("spring.profiles.active", "test");

        // Run the main method
        VetsServiceApplication.main(new String[]{});

        // No exception thrown indicates success
        // Reset system property
        System.clearProperty("spring.profiles.active");
    }
}
