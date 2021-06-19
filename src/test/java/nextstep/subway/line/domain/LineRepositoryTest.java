package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationGroup;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("노선 레포지토리 테스트")
@DataJpaTest
public class LineRepositoryTest {

	@Autowired
	private StationRepository stations;
	@Autowired
	private LineRepository lines;

	private Line 이호선;
	private Line 이호선_역_그룹_포함;
	private Line 사호선;

	private StationGroup 역_그룹;

	@BeforeEach
	void 초기화() {
		Station 영속화된_홍대입구역 = stations.save(new Station("홍대입구역"));
		Station 영속화된_구로디지털단지역 = stations.save(new Station("구로디지털단지역"));
		역_그룹 = new StationGroup(Arrays.asList(영속화된_구로디지털단지역, 영속화된_홍대입구역));

		이호선 = new Line("2호선", "#FFFFFF");
		이호선_역_그룹_포함 = new Line("2호선", "#FFFFFF", 역_그룹);
		사호선 = new Line("4호선", "#000000");
	}

	@Test
	void 저장_영속화() {
		//given

		//when
		Line 영속화된_이호선 = lines.save(이호선);

		//then
		assertThat(영속화된_이호선).isNotNull();
	}

	@Test
	void 역_그룹_포함_저장_영속화() {
		//given

		//when
		Line 영속화된_이호선_역_그룹_포함 = lines.save(이호선_역_그룹_포함);

		//then
		assertThat(영속화된_이호선_역_그룹_포함).isNotNull();
		assertThat(영속화된_이호선_역_그룹_포함.stations()).containsSequence(역_그룹.stations());
	}

	@Test
	void 목록_조회() {
		//given
		Line 영속화된_이호선 = lines.save(이호선);
		Line 영속화된_사호선 = lines.save(사호선);

		//when
		List<Line> 조회된_목록 = lines.findAll();

		//then
		assertThat(조회된_목록).contains(영속화된_이호선);
		assertThat(조회된_목록).contains(영속화된_사호선);
	}

	@Test
	void 조회() {
		//given
		Line 영속화된_이호선 = lines.save(이호선);

		//when
		Optional<Line> 조회한_이호선 = lines.findById(영속화된_이호선.id());

		//then
		assertThat(조회한_이호선.isPresent()).isTrue();
		assertThat(영속화된_이호선).isEqualTo(조회한_이호선.get());
	}

	@Test
	void 수정() {
		//given
		Line 영속화된_이호선 = lines.save(이호선);

		//when
		영속화된_이호선.update(사호선);
		Optional<Line> 수정후_조회한_이호선 = lines.findById(영속화된_이호선.id());

		//then
		assertThat(수정후_조회한_이호선.isPresent()).isTrue();
		assertThat(수정후_조회한_이호선.get().name()).isEqualTo(사호선.name());
	}

	@Test
	void 삭제() {
		//given
		Line 영속화된_이호선 = lines.save(이호선);

		//when
		lines.delete(영속화된_이호선);
		Optional<Line> 삭제후_조회한_이호선 = lines.findById(영속화된_이호선.id());

		//then
		assertThat(삭제후_조회한_이호선.isPresent()).isFalse();
	}

	@Test
	void 역_그룹_포함_삭제() {
		//given
		Line 영속화된_이호선_역_그룹_포함 = lines.save(이호선_역_그룹_포함);

		//when
		lines.delete(영속화된_이호선_역_그룹_포함);
		Optional<Line> 삭제후_조회한_이호선_역_그룹_포함 = lines.findById(영속화된_이호선_역_그룹_포함.id());

		//then
		assertThat(삭제후_조회한_이호선_역_그룹_포함.isPresent()).isFalse();
	}
}
