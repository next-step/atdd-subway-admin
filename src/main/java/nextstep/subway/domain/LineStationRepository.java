package nextstep.subway.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
    List<LineStation> findByLineId(long lineId);
}
