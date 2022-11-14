package nextstep.subway.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select distinct l from Line l"
        + " join fetch l.lineStations.lineStations ls")
    List<Line> findAllLines();

}
