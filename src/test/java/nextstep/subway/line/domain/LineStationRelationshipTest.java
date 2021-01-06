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

	@BeforeEach
	void saveInitData() {
		Line 이호선 = new Line("2호선", "green");
		Station 강남역 = stationRepository.save(new Station("강남역"));
		Station 역삼역 = stationRepository.save(new Station("역삼역"));
		이호선.addOrUpdateStation(강남역, 역삼역, 10);
		이호선.addOrUpdateStation(역삼역);
		lineRepository.save(이호선);

		Line 오호선 = new Line("5호선", "purple");
		Station 천호역 = stationRepository.save(new Station("천호역"));
		Station 군자역 = stationRepository.save(new Station("군자역"));
		오호선.addOrUpdateStation(천호역, 군자역, 10);
		오호선.addOrUpdateStation(군자역);
		lineRepository.save(오호선);

		System.out.println("\n>> saveBeforeEach 종료\n");
	}

	@DisplayName("Line 목록 조회 시 Station 목록이 올바르게 포함되어 있는지 테스트")
	@Test
	void findAllWithStations() {
		Station 강남역 = stationRepository.findByName("강남역").get();
		Station 역삼역 = stationRepository.findByName("역삼역").get();
		Station 천호역 = stationRepository.findByName("천호역").get();
		Station 군자역 = stationRepository.findByName("군자역").get();

		assertThat(lineRepository.findAll())
			.map(Line::getStations)
			.anySatisfy(stations -> assertThat(stations).contains(강남역, 역삼역))
			.anySatisfy(lineStations -> assertThat(lineStations).contains(천호역, 군자역));
	}

	@DisplayName("Line 단건 조회 시 Station 목록이 올바르게 포함되어 있는지 테스트")
	@Test
	void findOneWithStations() {
		Line 이호선 = lineRepository.findByName("2호선").get();
		Station 강남역 = stationRepository.findByName("강남역").get();
		Station 역삼역 = stationRepository.findByName("역삼역").get();

		assertThat(이호선.getStations()).contains(강남역, 역삼역);
	}

	@DisplayName("Line에 속한 Station들에 대하여 하행 종점 정보 수정이 가능한지 테스트")
	@Test
	void update() {
		Line 이호선 = lineRepository.findByName("2호선").get();
		Station 역삼역 = stationRepository.findByName("역삼역").get();
		Station 잠실역 = stationRepository.save(new Station("잠실역"));

		이호선.addOrUpdateStation(역삼역, 잠실역, 15);

		assertThat(이호선.getDistance(역삼역, 잠실역)).isEqualTo(15);
		assertThat(이호선.getDistance(잠실역, 역삼역)).isEqualTo(15);
	}

	@DisplayName("Line에 속한 Station 삭제 테스트")
	@Test
	void delete() {
		Line 이호선 = lineRepository.findByName("2호선").get();
		Station 강남역 = stationRepository.findByName("강남역").get();

		이호선.removeStation(강남역);

		assertThat(이호선.getStations()).hasSize(1);
	}
}