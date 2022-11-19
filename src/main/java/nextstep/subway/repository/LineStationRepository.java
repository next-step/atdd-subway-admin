package nextstep.subway.repository;

import nextstep.subway.domain.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineStationRepository extends JpaRepository<Section, Long> {
}