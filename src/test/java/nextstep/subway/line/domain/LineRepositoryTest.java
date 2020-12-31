package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class LineRepositoryTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private LineRepository lineRepository;
	@Autowired
	private StationRepository stationRepository;

	@DisplayName("노선정보를 저장할때 구간정보도 저장되는지 확인한다.")
	@Test
	void saveWithSection() {
		//given
		List<Station> stations = stationRepository
			  .saveAll(Arrays.asList(new Station("강남역"), new Station("역삼역")));

		Line line = new Line("2호선", "green");
		Section section = new Section(stations.get(0), stations.get(1), 2);
		line.addSection(section);

		//when
		lineRepository.save(line);

		//then
		entityManager.flush();
		entityManager.clear();

		Optional<Line> actual = lineRepository.findByName("2호선");
		assertThat(actual).isNotNull();
		Sections sections = actual.get().getSections();
		assertThat(sections).isEqualTo(new Sections(Arrays.asList(section)));
	}
}
