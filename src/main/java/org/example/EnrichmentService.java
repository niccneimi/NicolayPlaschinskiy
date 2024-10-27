package src.main.java.org.example;

import java.util.Map;

public class EnrichmentService {
    private final UserRepository userRepository;

    public EnrichmentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Message enrich(Message message) {
        if (message.getEnrichmentType() == Message.EnrichmentType.MSISDN) {
            Map<String, String> content = message.getContent();
            String msisdn = content.get("msisdn");

            if (msisdn != null) {
                User user = userRepository.findByMsisdn(msisdn);
                if (user != null) {
                    content.put("firstName", user.getFirstName());
                    content.put("lastName", user.getLastName());
                }
            }
        }
        return message;
    }
}