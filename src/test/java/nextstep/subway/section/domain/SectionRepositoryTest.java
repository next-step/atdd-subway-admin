package nextstep.subway.section.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("섹션 레포지토리 테스트")
@DataJpaTest
public class SectionRepositoryTest {

	private static final double 거리_기본값 = 123.45D;

	@Autowired
	private StationRepository stations;
	@Autowired
	private LineRepository lines;
	@Autowired
	private SectionRepository sections;

	private Station 영속화된_홍대입구역;
	private Station 영속화된_구로디지털단지역;
	private Station 영속화된_강남역;
	private Line 영속화된_이호선;
	private Section 이호선_홍대입구역_구로디지털단지역_섹션;
	private Section 이호선_구로디지털단지역_강남역_섹션;

	@BeforeEach
	void 초기화() {
		영속화된_홍대입구역 = stations.save(new Station("홍대입구역"));
		영속화된_구로디지털단지역 = stations.save(new Station("구로디지털단지역"));
		영속화된_강남역 = stations.save(new Station("강남역"));
		영속화된_이호선 = lines.save(new Line("2호선", "#FFFFFF"));

		이호선_홍대입구역_구로디지털단지역_섹션 = new Section(영속화된_이호선, 영속화된_홍대입구역, 영속화된_구로디지털단지역, 거리_기본값);
		이호선_구로디지털단지역_강남역_섹션 = new Section(영속화된_이호선, 영속화된_구로디지털단지역, 영속화된_강남역, 거리_기본값);
	}

	@Test
	void 저장_영속성() {
		//given

		//when
		Section 영속화된_섹션 = sections.save(이호선_홍대입구역_구로디지털단지역_섹션);

		//then
		assertThat(영속화된_섹션).isNotNull();
	}

	@Test
	void 노선과_연관된_섹션_목록_조회() {
		//given
		Section 영속화된_홍대입구_구로디지털단지_섹션 = sections.save(이호선_홍대입구역_구로디지털단지역_섹션);
		Section 영속화된_구로디지털단지_강남_섹션 = sections.save(이호선_구로디지털단지역_강남역_섹션);

		//when
		List<Section> 조회된_목록 = sections.findAllByLine(영속화된_이호선);

		//then
		assertThat(조회된_목록).contains(영속화된_홍대입구_구로디지털단지_섹션);
		assertThat(조회된_목록).contains(영속화된_구로디지털단지_강남_섹션);
	}

	@Test
	void 조회() {
		//given
		Section 영속화된_섹션 = sections.save(이호선_홍대입구역_구로디지털단지역_섹션);

		//when
		Optional<Section> 조회한_섹션 = sections.findById(영속화된_섹션.id());

		//then
		assertThat(조회한_섹션.isPresent()).isTrue();
		assertThat(영속화된_섹션).isEqualTo(조회한_섹션.get());
	}

	@Test
	void 삭제() {
		//given
		Section 영속화된_섹션 = sections.save(이호선_홍대입구역_구로디지털단지역_섹션);

		//when
		sections.delete(영속화된_섹션);
		Optional<Section> 삭제후_조회한_섹션 = sections.findById(영속화된_섹션.id());

		//then
		assertThat(삭제후_조회한_섹션.isPresent()).isFalse();
	}
}
