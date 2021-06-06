package nextstep.subway.section.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findByUpStationId(Long id);

    Optional<Section> findByDownStationId(Long id);

    List<Section> findByLineId(Long id);
}
