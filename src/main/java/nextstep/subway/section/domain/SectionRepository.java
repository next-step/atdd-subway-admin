package nextstep.subway.section.domain;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByLineId(Long lineId, Sort sort);
    List<Section> findByLineId(Long lineId);
    Optional<Section> findByLineIdAndDownStationId(Long lineId, Long downStationId);
    Optional<Section> findByLineIdAndUpStationId(Long lineId, Long upStationId);
    Optional<Section> findByLineIdAndUpStationIdAndDownStationId(Long lineId, Long upStationId, Long downStationId);
    List<Section> findByLineIdAndSectionNumberGreaterThanEqual(Long lineId, Integer sectionNumber);
}
