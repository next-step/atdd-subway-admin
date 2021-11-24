package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

public class SectionTest extends AcceptanceTest {

	@Autowired
	StationRepository stationRepository;
	@Autowired
	LineRepository lineRepository;
	@Autowired
	SectionRepository sectionRepository;

	@Test
	@DisplayName("구간 생성 테스트")
	public void SectionTest() {
		//given
		Station station = stationRepository.save(new Station("판교역"));
		Station otherStation = stationRepository.save(new Station("양재역"));
		Line line = lineRepository.save(new Line("신분당선", "pink"));
		Section section = Section.create(line, station, otherStation, 10);

		//when

		Section savedSection = sectionRepository.save(section);
		Section findSection = sectionRepository.findById(savedSection.getId()).get();
		//then
		assertThat(savedSection).isEqualTo(findSection);
	}
}
