package nextstep.subway.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {
    Optional<Section> findAllByUpStationAndLine(Station upStation, Line line);
    Optional<Section> findAllByDownStationAndLine(Station downStation, Line line);

    Optional<Section> findAllByUpStationAndDownStationAndLine(Station upStation, Station downStation, Line line);
}
