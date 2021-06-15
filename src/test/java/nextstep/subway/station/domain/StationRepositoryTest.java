package nextstep.subway.station.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DisplayName("지하철역 레포지토리 테스트")
@DataJpaTest
public class StationRepositoryTest {

	@Autowired
	private StationRepository stations;

	private Station 홍대입구역;
	private Station 구로디지털단지역;

	@BeforeEach
	void 초기화() {
		홍대입구역 = new Station("홍대입구역");
		구로디지털단지역 = new Station("구로디지털단지역");
	}

	@Test
	void 저장_영속화() {
		//given

		//when
		Station 영속화된_홍대입구역 = stations.save(홍대입구역);

		//then
		assertThat(영속화된_홍대입구역).isNotNull();
	}

	@Test
	void 목록_조회() {
		//given
		Station 영속화된_홍대입구역 = stations.save(홍대입구역);
		Station 영속화된_구로디지털단지역 = stations.save(구로디지털단지역);

		//when
		List<Station> 조회된_목록 = stations.findAll();

		//then
		assertThat(조회된_목록).contains(영속화된_홍대입구역);
		assertThat(조회된_목록).contains(영속화된_구로디지털단지역);
	}

	@Test
	void 조회() {
		//given
		Station 영속화된_홍대입구역 = stations.save(홍대입구역);

		//when
		Optional<Station> 조회한_홍대입구역 = stations.findById(영속화된_홍대입구역.id());

		//then
		assertThat(조회한_홍대입구역.isPresent()).isTrue();
		assertThat(영속화된_홍대입구역).isEqualTo(조회한_홍대입구역.get());
	}

	@Test
	void 수정() {
		//given
		Station 영속화된_홍대입구역 = stations.save(홍대입구역);

		//when
		영속화된_홍대입구역.update(구로디지털단지역);
		Optional<Station> 수정후_조회한_홍대입구역 = stations.findById(영속화된_홍대입구역.id());

		//then
		assertThat(수정후_조회한_홍대입구역.isPresent()).isTrue();
		assertThat(수정후_조회한_홍대입구역.get().name()).isEqualTo(구로디지털단지역.name());
	}

	@Test
	void 삭제() {
		//given
		Station 영속화된_홍대입구역 = stations.save(홍대입구역);

		//when
		stations.delete(영속화된_홍대입구역);
		Optional<Station> 삭제후_조회한_홍대입구역 = stations.findById(영속화된_홍대입구역.id());

		//then
		assertThat(삭제후_조회한_홍대입구역.isPresent()).isFalse();
	}
}
