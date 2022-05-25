package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {
    List<Line> findAllByDeletedFalse();

    Optional<Line> findByIdAndDeletedFalse(long lineId);
}
