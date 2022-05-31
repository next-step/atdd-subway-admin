package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query(value = "select l from Line l join fetch l.sections")
    Optional<Line> findByIdWithSections(Long id);
}
