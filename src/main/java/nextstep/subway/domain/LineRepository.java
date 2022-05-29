package nextstep.subway.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query(value = "select l from Line l "
        + "join fetch l.sections s "
        + "join fetch s.upStation "
        + "join fetch s.downStation ")
    List<Line> findLineAndStations();
}
