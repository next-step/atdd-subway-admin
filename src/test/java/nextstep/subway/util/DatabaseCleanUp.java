package nextstep.subway.util;

import com.google.common.base.CaseFormat;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class DatabaseCleanUp implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        tableNames = entityManager.getMetamodel()
                .getEntities()
                .stream()
                .filter(entityType -> entityType.getJavaType().isAnnotationPresent(Entity.class))
                .map(entityType -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityType.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void cleanUp() {
        entityManager.flush();
        entityManager.createNativeQuery("set referential_integrity false").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("truncate table " + tableName).executeUpdate();
            entityManager.createNativeQuery("alter table " + tableName + " alter column id restart with 1")
                    .executeUpdate();
        }

        entityManager.createNativeQuery("SET referential_integrity TRUE").executeUpdate();
    }
}
