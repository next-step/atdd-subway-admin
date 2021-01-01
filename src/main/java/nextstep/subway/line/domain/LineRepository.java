package nextstep.subway.line.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineRepository extends JpaRepository<Line, Long> {
    Optional<Line> findLineByName(String name);

    @Query("select distinct l from Line l"
        + " join fetch l.lineStations ls"
        + " join fetch ls.station s"
        + " left join fetch ls.previousStation pre"
        + " left join fetch ls.nextStation next"
        + " where l.id = :id")
    Line findLineByFetchJoin(@Param("id") Long id);
}
