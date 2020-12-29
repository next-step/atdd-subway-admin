package nextstep.subway.route.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
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
class RouteTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private LineRepository lineRepository;
	@Autowired
	private StationRepository stationRepository;
	@Autowired
	private RouteRepository routeRepository;

	private Line line;
	private List<Station> stations;

	@BeforeEach
	void setUp() {
		this.line = lineRepository.save(new Line("green", "2호선"));
		this.stations = stationRepository.saveAll(Arrays.asList(
			  new Station("강남역"), new Station("역삼역")
		));
	}

	@DisplayName("지하철_경로_등록")
	@Test
	void create() {
		//when
		Route route = new Route(line, stations.get(0), stations.get(1), 2);
		line.addByRoute(route);
		Route actual = routeRepository.save(route);

		//then
		entityManager.clear();
		entityManager.flush();

		Optional<Route> expectedRoute = routeRepository.findById(actual.getId());
		Optional<Line> expectedLine = lineRepository.findById(line.getId());
		assertThat(expectedRoute.isPresent()).isTrue();
		assertThat(expectedLine.get().getRoutes()).hasSize(1);
	}
}
