package nextstep.subway.line;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class LineJpaTest {

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private StationRepository stationRepository;

	@Test
	public void testSave() throws Exception {
		Station station1 = new Station("강남역");
		Station station2 = new Station("강변역");
		stationRepository.save(station1);
		stationRepository.save(station2);

		Line line = new Line("테스트노선", "green", station1, station2, 10);
		lineRepository.save(line);
		System.out.println(line);
	}
}
