package nextstep.subway.section.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByUpStationId(Long id);

    Optional<Section> findByDownStationId(Long id);

    List<Section> findByLineId(Long id);

    void deleteAllByLineId(Long lineId);
}
