package nextstep.subway.section;

import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Station, Long> {
}
