package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@DisplayName("Line과 Station의 연관관계에 대한 Domain 단위테스트")
@DataJpaTest
class LineStationRelationshipTest {

	@Autowired
	LineRepository lineRepository;
	@Autowired
	StationRepository stationRepository;

	private Station 교대역;
	private Station 강남역;
	private Station 역삼역;
	private Station 선릉역;
	private Station 삼성역;
	private Station 잠실역;
	private Station 천호역;
	private Station 군자역;
	private Line 이호선;
	private Line 오호선;

	@BeforeEach
	void saveInitData() {
		교대역 = stationRepository.save(new Station("교대역"));
		강남역 = stationRepository.save(new Station("강남역"));
		역삼역 = stationRepository.save(new Station("역삼역"));
		선릉역 = stationRepository.save(new Station("선릉역"));
		삼성역 = stationRepository.save(new Station("삼성역"));
		잠실역 = stationRepository.save(new Station("잠실역"));
		천호역 = stationRepository.save(new Station("천호역"));
		군자역 = stationRepository.save(new Station("군자역"));

		이호선 = lineRepository.save(new Line("2호선", "green"));
		오호선 = lineRepository.save(new Line("5호선", "purple"));

		이호선.addOrUpdateStation(강남역, 삼성역, 10);
		오호선.addOrUpdateStation(천호역, 군자역, 10);

		System.out.println("\n>> saveBeforeEach 종료\n");
	}

	@DisplayName("Line 목록 조회 시 Station 목록이 올바르게 포함되어 있는지 테스트")
	@Test
	void findAllWithStations() {
		assertThat(lineRepository.findAll())
			.map(Line::getStations)
			.anySatisfy(stations -> assertThat(stations).contains(강남역, 삼성역))
			.anySatisfy(stations -> assertThat(stations).contains(천호역, 군자역));
	}

	@DisplayName("Line 단건 조회 시 Station 목록이 올바르게 포함되어 있는지 테스트")
	@Test
	void findOneWithStations() {
		assertThat(이호선.getStations()).contains(강남역, 삼성역);
	}

	@DisplayName("Line에 속한 Station 삭제 테스트")
	@Test
	void delete() {
		이호선.removeStation(강남역);

		assertThat(이호선.getStations()).hasSize(1);
	}
	
	@DisplayName("LineStation 저장 테스트 (happy path)")
	@Test
	void saveLineStation_shouldSuccess1() {
		이호선.addOrUpdateStation(강남역, 역삼역, 4);
		assertThat(이호선.getStations()).contains(강남역, 역삼역, 삼성역);

		이호선.addOrUpdateStation(역삼역, 선릉역, 5);
		assertThat(이호선.getStations()).contains(강남역, 역삼역, 선릉역, 삼성역);
	}

	@DisplayName("LineStation 저장 테스트 (happy path : 상행 확장)")
	@Test
	void saveLineStation_shouldSuccess2() {
		이호선.addOrUpdateStation(교대역, 강남역, 100);
		assertThat(이호선.getStations()).contains(교대역, 강남역, 삼성역);
	}

	@DisplayName("LineStation 저장 테스트 (happy path : 하행 확장)")
	@Test
	void saveLineStation_shouldSuccess3() {
		이호선.addOrUpdateStation(삼성역, 잠실역, 100);
		assertThat(이호선.getStations()).contains(강남역, 삼성역, 잠실역);
	}

	@DisplayName("LineStation 저장 테스트 (예외 케이스 : 기존 역 사이 길이와 같다)")
	@Test
	void saveLineStation_shouldException1() {
		이호선.addOrUpdateStation(강남역, 역삼역, 4);
		assertThat(이호선.getStations()).contains(강남역, 역삼역, 삼성역);

		assertThatThrownBy(() -> 이호선.addOrUpdateStation(역삼역, 선릉역, 6))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("LineStation 저장 테스트 (예외 케이스 : 기존 역 사이 길이보다 크다)")
	@Test
	void saveLineStation_shouldException2() {
		이호선.addOrUpdateStation(강남역, 역삼역, 4);
		assertThat(이호선.getStations()).contains(강남역, 역삼역, 삼성역);

		assertThatThrownBy(() -> 이호선.addOrUpdateStation(역삼역, 선릉역, 7))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("LineStation 저장 테스트 (예외 케이스 : 이미 노선에 모두 등록)")
	@Test
	void saveLineStation_shouldException3() {
		assertThatThrownBy(() -> 이호선.addOrUpdateStation(강남역, 삼성역, 7))
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@DisplayName("LineStation 저장 테스트 (예외 케이스 : 상행역과 하행역 둘 중 하나도 포함되어있지 않음)")
	@Test
	void saveLineStation_shouldException4() {
		assertThatThrownBy(() -> 이호선.addOrUpdateStation(교대역, 잠실역, 7))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("LineStation 저장 테스트 (예외 케이스 : 상행역과 하행역이 같음)")
	@Test
	void saveLineStation_shouldException5() {
		assertThatThrownBy(() -> 이호선.addOrUpdateStation(강남역, 강남역, 7))
			.isInstanceOf(IllegalArgumentException.class);
	}

}