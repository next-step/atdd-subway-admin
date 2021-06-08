package nextstep.subway.station;

import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DataJpaTest
public class StationRepositoryTest {

	@Autowired
	StationRepository stations;

	Station sungSuStation;
	Station gunDaeStation;

	@BeforeEach
	void setUp() {
		sungSuStation = stations.save(new Station("성수역"));
		gunDaeStation = stations.save(new Station("건대입구역"));
	}

	@Test
	void 지하철_역_저장() {
		Station expected = new Station("강남역");
		Station saved = stations.save(expected);
		assertThat(saved.getName()).isEqualTo(expected.getName());
	}

	@Test
	void 지하철_역_조회() {
		Station findSungSuStation = stations.findById(sungSuStation.getId()).get();
		assertThat(findSungSuStation.getName()).isEqualTo(sungSuStation.getName());
	}

	@Test
	void 지하철_역_삭제() {
		stations.delete(sungSuStation);
		assertThatThrownBy(
			() -> stations.findById(sungSuStation.getId()).orElseThrow(EntityNotFoundException::new)
		).isInstanceOf(EntityNotFoundException.class);
	}
}
