package nextstep.subway.line.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
    LineStation findLineStationByStationId(Long stationId);
}
