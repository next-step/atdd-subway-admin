package nextstep.subway.repository;

import java.util.Optional;
import nextstep.subway.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
    Optional<Line> findByName(String name);
}
