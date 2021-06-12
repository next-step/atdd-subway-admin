package nextstep.subway.linestation;

import nextstep.subway.domain.LineStation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
}
