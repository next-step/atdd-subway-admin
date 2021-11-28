package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class SectionRepositoryTest {

	@Autowired
	private StationRepository stationRepository;

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private SectionRepository sectionRepository;

	@Test
	@DisplayName("구간 생성 테스트")
	public void CreateSectionTest() {
		//given
		Section section = Section.create(saveStation("판교역"), saveStation("양재역"));

		//when
		Section savedSection = sectionRepository.save(section);
		Section findSection = sectionRepository.findById(savedSection.getId()).get();

		//then
		assertThat(savedSection).isEqualTo(findSection);
	}

	@Test
	@DisplayName("구간 삭제 테스트")
	public void DeleteSectionTest() {
		//given
		Section section = Section.create(saveStation("판교역"), saveStation("양재역"));
		Section otherSection = Section.create(saveStation("분당역"), saveStation("야탑역"));
		Section savedSection = sectionRepository.save(section);
		Section savedOtherSection = sectionRepository.save(otherSection);

		//when
		sectionRepository.deleteById(savedSection.getId());
		List<Section> sections = sectionRepository.findAll();
		//then
		assertThat(sections).hasSize(1);
	}

	private Station saveStation(String name) {
		return stationRepository.save(new Station(name));
	}

	private Line saveLine(String lineName, String lineColor) {
		return lineRepository.save(new Line(lineName, lineColor));
	}

}
