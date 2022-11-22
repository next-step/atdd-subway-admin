package nextstep.subway;

import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DataBaseCleaner implements InitializingBean {

    @PersistenceContext
    private EntityManager em;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.tableNames = em.getMetamodel()
                .getEntities().stream()
                .filter(this::hasEntityAnnotation)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

    private boolean hasEntityAnnotation(EntityType<?> entityType) {
        return Objects.nonNull(entityType.getJavaType().getAnnotation(Entity.class));
    }

    @Transactional
    public void clear() {
        em.flush();
        em.createNativeQuery("SET referential_integrity FALSE").executeUpdate();

        for (String tableName : tableNames) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            em.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN id RESTART WITH 1")
                    .executeUpdate();
        }

        em.createNativeQuery("SET referential_integrity TRUE").executeUpdate();
    }
}
