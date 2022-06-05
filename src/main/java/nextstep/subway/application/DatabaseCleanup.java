package nextstep.subway.application;

import com.google.common.base.CaseFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("acceptance")
public class DatabaseCleanup implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(e -> Objects.nonNull(e.getJavaType().getAnnotation(Entity.class)))
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        tableNames.stream().forEach(this::reset);

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

    private void reset(String tableName) {
        truncate(tableName);
        resetId(tableName);
    }

    private void truncate(String tableName) {
        entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
    }

    private void resetId(String tableName) {
        entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1").executeUpdate();
    }
}
