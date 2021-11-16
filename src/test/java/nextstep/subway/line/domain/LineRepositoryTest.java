package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class LineRepositoryTest {

	@Autowired
	private LineRepository lineRepository;

	@Autowired
	private StationRepository stationRepository;

	@Test
	void save() {
		final Station upStation = stationRepository.save(new Station("강남역"));
		final Station downStation = stationRepository.save(new Station("광교역"));
		final Line line = new Line("신분당선", "bg-red-600");
		line.add(new Section(upStation, downStation, 10));

		final Line savedLine = lineRepository.save(line);
		assertThat(line).isSameAs(savedLine);
		assertAll(
			() -> assertThat(savedLine.getId()).isNotNull(),
			() -> assertThat(savedLine.getCreatedDate()).isNotNull(),
			() -> assertThat(savedLine.getModifiedDate()).isNotNull(),
			() -> assertThat(savedLine.getStationsFromUpTerminal()).containsExactly(upStation, downStation)
		);
	}
}
