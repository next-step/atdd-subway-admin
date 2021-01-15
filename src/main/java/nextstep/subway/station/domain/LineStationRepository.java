package nextstep.subway.station.domain;


import nextstep.subway.line.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
    LineStation findByStation(Station station);
    LineStation findByLine(Line line);
}
