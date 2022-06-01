package nextstep.subway;

import com.google.common.base.CaseFormat;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseAcceptanceTest {
    public class SpringContainerTest {
        @Autowired
        private DatabaseCleaner databaseCleaner;

        @AfterEach
        protected void clearDatabase() {
            databaseCleaner.tableClear();
        }
    }

    @Service
    public class DatabaseCleaner implements InitializingBean {
        @PersistenceContext
        private EntityManager entityManager;

        private List<String> tableNames;

        @Override
        public void afterPropertiesSet() {
            tableNames = entityManager.getMetamodel().getEntities()
                    .stream()
                    .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
                    .map(entityType -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getName()))
                    .collect(Collectors.toList());
        }

        @Transactional
        public void tableClear() {
            entityManager.flush();
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
            for (String tableName : tableNames) {
                entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
                entityManager
                        .createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1")
                        .executeUpdate();
            }
            entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
        }
    }
}
