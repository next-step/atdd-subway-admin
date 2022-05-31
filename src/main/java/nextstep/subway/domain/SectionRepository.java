package nextstep.subway.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByLine(Line line);
    Optional<Section> findByUpStationAndLine(Station station, Line line);
    Optional<Section> findByDownStationAndLine(Station station, Line line);
}
