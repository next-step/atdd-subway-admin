package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByLineId(Long id);
}
