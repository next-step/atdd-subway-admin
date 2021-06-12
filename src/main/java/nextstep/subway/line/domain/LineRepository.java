package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {

    @EntityGraph(attributePaths = {"sections.sections"})
    Optional<Line> findById(@Param("id") Long id);
}
