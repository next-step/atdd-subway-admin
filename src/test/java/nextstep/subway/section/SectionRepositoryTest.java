package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class SectionRepositoryTest {

	@Autowired
	LineRepository lines;

	@Autowired
	SectionRepository sections;

	@Autowired
	StationRepository stations;

	Line twiceLine;
	Station sungSuStation;
	Station gunDaeStation;
	Section section;

	@BeforeEach
	void 구간_저장() {
		sungSuStation = new Station("성수역");
		gunDaeStation = new Station("건대입구역");
		twiceLine = new Line("2호선", "Green");
		section = new Section(twiceLine, sungSuStation, gunDaeStation, 10);
		stations.save(sungSuStation);
		stations.save(gunDaeStation);
		lines.save(twiceLine);
		sections.save(section);
	}

	@Test
	void 구간_조회() {
		Section findSection = sections.findById(section.getId()).get();
		assertThat(findSection).isSameAs(section);
	}

	@Test
	void 구간_삭제() {
		sections.deleteById(section.getId());
		assertThatThrownBy(
			() -> sections.findById(section.getId()).orElseThrow(EntityNotFoundException::new)
		).isInstanceOf(EntityNotFoundException.class);
	}
}
