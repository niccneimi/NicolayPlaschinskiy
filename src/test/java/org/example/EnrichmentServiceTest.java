import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.main.java.org.example.EnrichmentService;
import src.main.java.org.example.InMemoryUserRepository;
import src.main.java.org.example.Message;
import src.main.java.org.example.User;
import src.main.java.org.example.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class EnrichmentServiceTest {
    private UserRepository userRepository;
    private EnrichmentService enrichmentService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        enrichmentService = new EnrichmentService(userRepository);
    }

    @Test
    void shouldEnrichMessageWithUserDetails() {
        User user = new User("Vasya", "Ivanov");
        userRepository.updateUserByMsisdn("88005553535", user);

        Map<String, String> content = new HashMap<>();
        content.put("action", "button_click");
        content.put("page", "book_card");
        content.put("msisdn", "88005553535");
        Message message = new Message(content, Message.EnrichmentType.MSISDN);

        Message enrichedMessage = enrichmentService.enrich(message);

        assertEquals("Vasya", enrichedMessage.getContent().get("firstName"));
        assertEquals("Ivanov", enrichedMessage.getContent().get("lastName"));
    }
}