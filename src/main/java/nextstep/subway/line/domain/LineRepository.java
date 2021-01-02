package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @EntityGraph(attributePaths = {"sections", "sections.items.downStation", "sections.items.upStation"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Line> findWithSectionsById(Long id);
}
