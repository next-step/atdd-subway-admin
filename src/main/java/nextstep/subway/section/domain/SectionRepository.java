package nextstep.subway.section.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByLineId(Long lineId);
    Optional<Section> findTop1ByLineIdOrderBySectionNumberDesc(Long lineId);
}
