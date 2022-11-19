package nextstep.subway.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByLineIdAndUpStationId(Long lineId, Long stationId);
    Optional<Section> findByLineIdAndDownStationId(Long lineId, Long stationId);
}
