package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("SELECT l FROM Line l"
        + " JOIN FETCH l.sections.values s"
        + " JOIN FETCH s.upStation"
        + " JOIN FETCH s.downStation")
    List<Line> findAllWithSections();

    @Query("SELECT l FROM Line l"
        + " JOIN FETCH l.sections.values s"
        + " JOIN FETCH s.upStation"
        + " JOIN FETCH s.downStation"
        + " WHERE l.id = :id")
    Optional<Line> findWithSectionsById(@Param("id") Long id);
}
