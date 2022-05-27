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
    private static final String FOREIGN_KEY_CHECKS_UNLOCK = "SET FOREIGN_KEY_CHECKS = 0";
    private static final String FOREIGN_KEY_CHECKS_LOCK = "SET FOREIGN_KEY_CHECKS = 1";

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public void execute(String... names) {
        executeQuery(names);
        entityManager.flush();
        entityManager.clear();
    }

    private void executeQuery(String... names) {
        entityManager.createNativeQuery(FOREIGN_KEY_CHECKS_UNLOCK).executeUpdate();

        for (String name : names) {
            entityManager.createNativeQuery(TRUNCATE_COMMAND + name).executeUpdate();
        }

        entityManager.createNativeQuery(FOREIGN_KEY_CHECKS_LOCK).executeUpdate();
    }
}
