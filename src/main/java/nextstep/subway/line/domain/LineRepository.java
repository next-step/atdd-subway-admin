package nextstep.subway.line.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
    List<Line> findAll();

    Line findLineById(Long id);
}
