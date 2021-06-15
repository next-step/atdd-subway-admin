package nextstep.subway.section.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Optional<Section> findByUpStationId(Long upStationId);
}

