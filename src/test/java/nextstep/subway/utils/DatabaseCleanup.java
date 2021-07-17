package nextstep.subway.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Service
@ActiveProfiles("test")
public class DatabaseCleanup {

    @Autowired
    private List<JpaRepository<?, ?>> repositories;

    @Transactional
    public void execute() {
        for (JpaRepository<?, ?> repository : repositories) {
            repository.deleteAll();
        }
    }
}