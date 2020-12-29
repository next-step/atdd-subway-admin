package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class SectionTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private LineRepository lineRepository;
	@Autowired
	private StationRepository stationRepository;
	@Autowired
	private SectionRepository sectionRepository;

	private Line line;
	private List<Station> stations;

	@BeforeEach
	void setUp() {
		this.line = lineRepository.save(new Line("green", "2호선"));
		this.stations = stationRepository.saveAll(Arrays.asList(
			  new Station("강남역"), new Station("역삼역"), new Station("교대역")
		));
	}

	@DisplayName("지하철_경로_등록")
	@Test
	void create() {
		//when
		Section section = new Section(line, stations.get(0), stations.get(1), 2);
		line.addBySection(section);
		Section actual = sectionRepository.save(section);

		//then
		entityManager.clear();
		entityManager.flush();

		Optional<Section> expectedRoute = sectionRepository.findById(actual.getId());
		Optional<Line> expectedLine = lineRepository.findById(line.getId());
		assertThat(expectedRoute.isPresent()).isTrue();
		assertThat(expectedLine.get().getSections()).hasSize(1);
	}

	@DisplayName("상행역 - 하행역 순서대로 정렬")
	@Test
	void sort() {
		//given
		List<Section> sections = Arrays
			  .asList(new Section(line, stations.get(0), stations.get(1), 2),
					new Section(line, stations.get(2), stations.get(0), 2));
		sections = sectionRepository.saveAll(sections);

		//when
		Collections.sort(sections);

		//then
		assertThat(sections.get(0).getUpStation()).isEqualTo(stations.get(2));
		assertThat(sections.get(1).getUpStation()).isEqualTo(stations.get(0));
	}
}
