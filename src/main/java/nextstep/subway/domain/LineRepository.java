package nextstep.subway.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {

    @EntityGraph(attributePaths = {"upStation", "downStation"})
    Optional<Line> findById(Long id);
}
