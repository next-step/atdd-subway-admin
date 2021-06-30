package nextstep.subway.section.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nextstep.subway.line.domain.Line;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
	List<Section> findAllByLine(Line line);
}
