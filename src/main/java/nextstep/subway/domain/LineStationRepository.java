package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation,Long> {
    int deleteAllByLine(Line line);

    int deleteAllByStation(Station station);
}
