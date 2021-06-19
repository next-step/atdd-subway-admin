package nextstep.subway.section.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import nextstep.subway.line.domain.Line;

public interface SectionRepository extends JpaRepository<Section, Long> {
	List<Section> findAllByLine(Line line);

	void deleteByLine(Line line);
}
