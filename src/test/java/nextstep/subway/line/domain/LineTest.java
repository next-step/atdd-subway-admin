package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private SectionRepository sections;

	private Station station1;
	private Station station2;

	@BeforeEach
	void setUp() {
		station1 = new Station("강남역");
		station2 = new Station("서초역");
		em.persist(station1);
		em.persist(station2);
		em.flush();
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	@DisplayName("생성자 사용시 cascade 를 통해 section 또한 생성되는지 확인")
	void constructor_cascade(boolean clear) {
		// when
		Line line = new Line("2호선", "green", station1, station2, 50);
		em.persist(line);
		em.flush();
		if (clear) em.clear();

		// then
		Line actual = em.find(Line.class, line.getId());
		assertThat(sections.findAllByLine(actual)).hasSize(3).anySatisfy(section -> {
			assertSection(section, line, null, station1, 0);
		}).anySatisfy(section -> {
			assertSection(section, line, station1, station2, 50);
		}).anySatisfy(section -> {
			assertSection(section, line, station2, null, 0);
		});
	}

	private void assertSection(Section section, Line line, Station front, Station back, int distance) {
		assertThat(section.getFront()).isEqualTo(front);
		assertThat(section.getBack()).isEqualTo(back);
		assertThat(section.getDistance()).isEqualTo(new Distance(distance));
		assertThat(section.getLine()).isEqualTo(line);
	}

	@Test
	@DisplayName("정렬된 station 갖고오는지 확인")
	void getSortedStation() {
		// given
		Line line = new Line("2호선", "green", station2, station1, 50);
		em.persist(line);
		em.flush();
		em.clear();

		// when & then
		assertThat(em.find(Line.class, line.getId()))
				.extracting(Line::getSortedStations)
				.asList()
				.hasSize(2)
				.containsSequence(station2, station1);
	}
}
