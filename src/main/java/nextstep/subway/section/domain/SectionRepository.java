package nextstep.subway.section.domain;

import java.util.Optional;
import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByUpStationAndDownStation(Station upStation, Station downStation);
}
