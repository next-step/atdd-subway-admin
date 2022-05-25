package nextstep.subway.code;

import com.google.common.base.CaseFormat;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Service
@ActiveProfiles("test")
public class DatabaseCleanup implements InitializingBean {

    private static final String REFERENTIAL_INTEGRITY_SET_FALSE = "SET REFERENTIAL_INTEGRITY FALSE";
    private static final String REFERENTIAL_INTEGRITY_SET_TRUE = "SET REFERENTIAL_INTEGRITY TRUE";
    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE %s";
    private static final String RESTART_ID_COLUMN = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1";

    @PersistenceContext
    private EntityManager em;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = em.getMetamodel().getEntities().stream()
            .filter(isExistEntityAnnotation())
            .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
            .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        em.flush();
        em.createNativeQuery(REFERENTIAL_INTEGRITY_SET_FALSE).executeUpdate();

        for (String tableName : tableNames) {
            em.createNativeQuery(String.format(TRUNCATE_TABLE, tableName)).executeUpdate();
            em.createNativeQuery(String.format(RESTART_ID_COLUMN, tableName)).executeUpdate();
        }

        em.createNativeQuery(REFERENTIAL_INTEGRITY_SET_TRUE).executeUpdate();
    }


    private Predicate<EntityType<?>> isExistEntityAnnotation() {
        return e -> e.getJavaType().getAnnotation(Entity.class) != null;
    }
}
