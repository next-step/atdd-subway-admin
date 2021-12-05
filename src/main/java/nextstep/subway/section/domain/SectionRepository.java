package nextstep.subway.section.domain;

import java.util.List;
import nextstep.subway.line.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByLineId(Long lineId);
}
