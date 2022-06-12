package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l "
            + "join fetch l.sections.sections sc "
            + "join fetch sc.upStation us "
            + "join fetch sc.downStation ds ")
    List<Line> findAllFetchStation();

    @Query("select l from Line l "
            + "join fetch l.sections.sections sc "
            + "join fetch sc.upStation us "
            + "join fetch sc.downStation ds ")
    Optional<Line> findFetchStationById(final Long id);
}
