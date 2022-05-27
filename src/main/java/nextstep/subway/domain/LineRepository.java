package nextstep.subway.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query(value = "select l from Line l "
        + "join fetch l.upStation "
        + "join fetch l.downStation ")
    List<Line> findLineAndStations();
}
