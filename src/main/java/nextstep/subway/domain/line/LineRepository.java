package nextstep.subway.domain.line;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineRepository extends JpaRepository<Line, Long> {

    @EntityGraph(attributePaths = {"sections.sections", "sections.sections.upStation", "sections.sections.downStation"})
    Optional<Line> findById(Long id);
}
