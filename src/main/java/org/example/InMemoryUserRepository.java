package src.main.java.org.example;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    @Override
    public User findByMsisdn(String msisdn) {
        return userStore.get(msisdn);
    }

    @Override
    public void updateUserByMsisdn(String msisdn, User user) {
        userStore.put(msisdn, user);
    }
}