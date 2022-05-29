package nextstep.subway.testutils;

import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("acceptance")
public class DatabaseCleanup implements InitializingBean {
    public static final String SQL_SET_REFERENTIAL_INTEGRITY_FALSE = "SET REFERENTIAL_INTEGRITY FALSE";
    public static final String SQL_TRUNCATE_TABLE = "TRUNCATE TABLE %s";
    public static final String SQL_ALTER_ID_RESTART_WITH_1 = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1";
    public static final String SQL_SET_REFERENTIAL_INTEGRITY_TRUE = "SET REFERENTIAL_INTEGRITY TRUE";

    @PersistenceContext
    private EntityManager entityManager;
    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute(){
        entityManager.flush();
        entityManager.createNativeQuery(SQL_SET_REFERENTIAL_INTEGRITY_FALSE).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(SQL_TRUNCATE_TABLE, tableName)).executeUpdate();
            entityManager.createNativeQuery(String.format(SQL_ALTER_ID_RESTART_WITH_1, tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(SQL_SET_REFERENTIAL_INTEGRITY_TRUE).executeUpdate();
    }
}
