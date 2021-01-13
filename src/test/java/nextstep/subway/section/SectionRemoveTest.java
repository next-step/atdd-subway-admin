package nextstep.subway.section;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

public class SectionRemoveTest extends AcceptanceTest {
	@Autowired
	LineRepository lineRepository;

	@Autowired
	StationRepository stationRepository;

	Line line;
	Station 교대역;
	Station 강남역;
	Station 선릉역;
	Station 삼성역;

	@BeforeEach
	public void setUp() {
		super.setUp();
		line = new Line("2호선", "green");
		교대역 = stationRepository.save(new Station("교대역"));
		강남역 = stationRepository.save(new Station("강남역"));
		선릉역 = stationRepository.save(new Station("선릉역"));
		삼성역 = stationRepository.save(new Station("삼성역"));
		line.addInitSection(
			new Section.SectionBuilder()
				.line(line)
				.upStation(교대역)
				.downStation(강남역)
				.distance(new Distance(4))
				.build()
		);
		line.addInitSection(
			new Section.SectionBuilder()
				.line(line)
				.upStation(강남역)
				.downStation(선릉역)
				.distance(new Distance(6))
				.build()
		);
		lineRepository.save(line);
	}

	@DisplayName("상행종점 제거")
	@Test
	void removeUpStation() {
		ExtractableResponse<Response> response = deleteStation(교대역.getId());
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("하행종점 제거")
	@Test
	void removeDownStation() {
		ExtractableResponse<Response> response = deleteStation(선릉역.getId());
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("중간역 제거")
	@Test
	void removeInTheMiddleOfStation() {
		ExtractableResponse<Response> response = deleteStation(강남역.getId());
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
	}

	@DisplayName("노선에 등록되어있지 않은 역을 제거할 수 없음")
	@Test
	void removeStationNotExistException() {
		ExtractableResponse<Response> response = deleteStation(삼성역.getId());
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	@DisplayName("구간이 하나인 노선에서 제거할 수 없음")
	@Test
	void removeOnlyOneSectionException() {
		deleteStation(강남역.getId());
		ExtractableResponse<Response> response = deleteStation(교대역.getId());
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	private ExtractableResponse<Response> deleteStation(Long stationId) {
		return RestAssured.given().log().all()
			.when()
			.delete("/lines/" + line.getId() + "/sections?stationId=" + stationId)
			.then().log().all()
			.extract();
	}
}
