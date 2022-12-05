package nextstep.subway.domain.line;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<LineStation, Long> {
    List<LineStation> findByLine(Line line);
}
