package nextstep.subway.test;

import com.google.common.base.CaseFormat;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseClean implements InitializingBean {

    @Autowired
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet(){
        tableNames = entityManager.getMetamodel().getEntities().parallelStream()
                .filter(entity -> entity.getJavaType().getAnnotation(Entity.class) != null)
                .map(entity -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,entity.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void trancate(String tableName){
        entityManager.flush();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE "+tableName).executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE "+tableName+" ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    @Transactional
    public void trancateAll(){
        for (String tableName : tableNames){
            trancate(tableName);
        }
    }
}
