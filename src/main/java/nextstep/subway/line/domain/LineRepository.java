package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {
    @Query("select distinct l from Line l "
        + "join fetch l.sections.sections sc "
        + "join fetch sc.upStation us "
        + "join fetch sc.downStation ds "
        + "where l.id=:id")
    Optional<Line> findByIdWithSections(@Param("id") Long id);
}
