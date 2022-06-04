package nextstep.subway.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation,Long> {
    int deleteAllByLine(Line line);

    int deleteAllByStation(Station station);

    int deleteAllByStationAndLine(Station station, Line line);

    Optional<LineStation> findAllByStationAndLine(Station station, Line line);
}
