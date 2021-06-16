package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Section findByUpStationOrDownStationAndLine_Id(Station station, Station station2, Long lineId);
}
