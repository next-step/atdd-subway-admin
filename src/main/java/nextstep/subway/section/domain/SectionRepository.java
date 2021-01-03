package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

	Section findByUpStationAndDownStation(Station upStation, Station downStation);
}
