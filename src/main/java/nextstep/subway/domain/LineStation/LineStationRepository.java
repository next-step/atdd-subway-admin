package nextstep.subway.domain.LineStation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {

    List<LineStation> findByStationId(Long stationId);
}