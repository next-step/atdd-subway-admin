package nextstep.subway.domain.line;

import java.util.List;
import nextstep.subway.domain.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
    List<LineStation> findByLine(Line line);

    List<LineStation> findByStation(Station station);

    List<LineStation> findByPreStation(Station station);

    Long countByLine(Line line);

    @Query("select ls from LineStation as ls where (ls.preStation=:station or ls.station=:station) and ls.line=:line")
    List<LineStation> findByStationAndLine(Station station, Line line);
}
