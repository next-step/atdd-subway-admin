package nextstep.subway.db;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Service
@ActiveProfiles(value = "test")
public class DataInitializer {
    private static final String TRUNCATE_COMMAND = "truncate table ";

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void execute(String... names) {
        executeQuery(names);
        entityManager.flush();
        entityManager.clear();
    }

    private void executeQuery(String... names) {
        for (String name : names) {
            entityManager.createNativeQuery(TRUNCATE_COMMAND + name).executeUpdate();
        }
    }
}
