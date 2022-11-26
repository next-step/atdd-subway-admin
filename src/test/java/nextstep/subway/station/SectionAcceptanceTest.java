package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.DataBaseCleaner;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.SectionCreateRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.fixture.LineTestFixture;
import nextstep.subway.fixture.StationTestFixture;
import nextstep.subway.message.SectionMessage;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.StationRepository;
import nextstep.subway.rest.SectionRestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Line 이호선; // 강남역 -(5)- 역삼역 -(5)- 선릉역

    @BeforeEach
    void setUp() {
        if(RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        dataBaseCleaner.clear();
        강남역 = stationRepository.save(StationTestFixture.강남역);
        역삼역 = stationRepository.save(StationTestFixture.역삼역);
        선릉역 = stationRepository.save(StationTestFixture.선릉역);
        이호선 = lineRepository.save(LineTestFixture.이호선);

        SectionCreateRequest request = new SectionCreateRequest(강남역.getId(), 역삼역.getId(), 5);
        SectionRestAssured.지하철_구간_추가(이호선.getId(), request);

        request = new SectionCreateRequest(역삼역.getId(), 선릉역.getId(), 5);
        SectionRestAssured.지하철_구간_추가(이호선.getId(), request);
    }

    /**
     * Given 상행 종점 지하철 구간이 주어진 경우
     * When 구간을 추가하면
     * Then 상행 종점 구간이 추가된다
     */
    @DisplayName("지하철 구간 추가 - 추가되는 지하철역이 상행 종점인 경우")
    @Test
    void add_up_terminal_station_to_line_test() {
        // given
        Station 교대역 = stationRepository.save(StationTestFixture.교대역);
        SectionCreateRequest createRequest = new SectionCreateRequest(교대역.getId(), 강남역.getId(), 3);

        // when
        ExtractableResponse<Response> response = SectionRestAssured.지하철_구간_추가(이호선.getId(), createRequest);

        // then
        SectionResponse sectionResponse = response.as(SectionResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(sectionResponse.getUpStation().getName()).isEqualTo(교대역.getName().value()),
                () -> assertThat(sectionResponse.getDownStation().getName()).isEqualTo(강남역.getName().value()),
                () -> assertThat(sectionResponse.getDistance()).isEqualTo(3)
        );
    }

    /**
     * Given 하행 종점 지하철 구간이 주어진 경우
     * When 구간을 추가하면
     * Then 하행 종점 구간이 추가된다
     */
    @DisplayName("지하철 구간 추가 - 추가되는 지하철역이 하행 종점인 경우")
    @Test
    void add_down_terminal_station_to_line_test() {
        // given
        Station 삼성역 = stationRepository.save(StationTestFixture.삼성역);
        SectionCreateRequest createRequest = new SectionCreateRequest(선릉역.getId(), 삼성역.getId(), 3);

        // when
        ExtractableResponse<Response> response = SectionRestAssured.지하철_구간_추가(이호선.getId(), createRequest);

        // then
        SectionResponse sectionResponse = response.as(SectionResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(sectionResponse.getUpStation().getName()).isEqualTo(선릉역.getName().value()),
                () -> assertThat(sectionResponse.getDownStation().getName()).isEqualTo(삼성역.getName().value()),
                () -> assertThat(sectionResponse.getDistance()).isEqualTo(3)
        );
    }

    /**
     * Given 지하철 역 사이에 추가 되는 새로운 역이 하행역인 경우
     * When 구간을 추가하면
     * Then 기존 역의 거리는 변동되고 새로운 역이 추가 된다
     */
    @DisplayName("지하철 구간 추가 - 지하철역 사이에 추가 되는 새로운 역이 하행역인 경우")
    @Test
    void add_between_down_station_to_line_test() {
        // given
        Station 삼성역 = stationRepository.save(StationTestFixture.삼성역);
        SectionCreateRequest createRequest = new SectionCreateRequest(역삼역.getId(), 삼성역.getId(), 3);

        // when
        ExtractableResponse<Response> response = SectionRestAssured.지하철_구간_추가(이호선.getId(), createRequest);

        // then
        SectionResponse sectionResponse = response.as(SectionResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(sectionResponse.getUpStation().getName()).isEqualTo(역삼역.getName().value()),
                () -> assertThat(sectionResponse.getDownStation().getName()).isEqualTo(삼성역.getName().value()),
                () -> assertThat(sectionResponse.getDistance()).isEqualTo(3)
        );
    }

    /**
     * Given 지하철 역 사이에 추가 되는 새로운 역이 상행역인 경우
     * When 구간을 추가하면
     * Then 기존 역의 거리는 변동되고 새로운 역이 추가 된다
     */
    @DisplayName("지하철 구간 추가 - 지하철역 사이에 추가 되는 새로운 역이 상행역인 경우")
    @Test
    void add_between_up_station_to_line_test() {
        // given
        Station 삼성역 = stationRepository.save(StationTestFixture.삼성역);
        SectionCreateRequest createRequest = new SectionCreateRequest(삼성역.getId(), 선릉역.getId(), 3);

        // when
        ExtractableResponse<Response> response = SectionRestAssured.지하철_구간_추가(이호선.getId(), createRequest);

        // then
        // 강남역 -(5)- 역삼역 -(2)- 삼성역 -(3)- 선릉역
        SectionResponse sectionResponse = response.as(SectionResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(sectionResponse.getUpStation().getName()).isEqualTo(삼성역.getName().value()),
                () -> assertThat(sectionResponse.getDownStation().getName()).isEqualTo(선릉역.getName().value()),
                () -> assertThat(sectionResponse.getDistance()).isEqualTo(3)
        );
    }

    /**
     * Given 기존역 사이 길이보다 새로운 역의 길이가 큰 경우
     * When 구간을 등록하면
     * Then 등록에 실패한다
     */
    @DisplayName("지하철 구간 등록 예외 처리 - 기존역 사이 길이보다 새로운 역의 길이가 큰 경우")
    @Test
    void add_section_with_longer_distance_throw_exception() {
        // given
        Station 삼성역 = stationRepository.save(StationTestFixture.삼성역);
        SectionCreateRequest createRequest = new SectionCreateRequest(삼성역.getId(), 선릉역.getId(), 7);

        // when
        ExtractableResponse<Response> response = SectionRestAssured.지하철_구간_추가(이호선.getId(), createRequest);

        // then
        // 강남역 -(5)- 역삼역 -(5)- [+삼성역 -(7)-] 선릉역
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(SectionMessage.ERROR_NEW_SECTION_DISTANCE_MORE_THAN_ORIGIN_SECTION.message())
        );
    }

    /**
     * Given 상행역과 하행역이 이미 노선에 모두 등록되어 있는 경우
     * When 구간을 등록하면
     * Then 등록에 실패한다
     */
    @DisplayName("지하철 구간 등록 예외 처리 - 상행역과 하행역이 이미 노선에 모두 등록되어 있는 경우")
    @Test
    void add_section_with_enrolled_stations_throw_exception() {
        // given
        Station 삼성역 = stationRepository.save(StationTestFixture.삼성역);
        SectionCreateRequest createRequest = new SectionCreateRequest(역삼역.getId(), 선릉역.getId(), 3);

        // when
        ExtractableResponse<Response> response = SectionRestAssured.지하철_구간_추가(이호선.getId(), createRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(SectionMessage.ERROR_UP_AND_DOWN_STATIONS_ARE_ALREADY_ENROLLED.message())
        );
    }

    /**
     * 상행역과 하행역 둘 중 하나도 포함되어있지 않는 경우
     * When 구간을 등록하면
     * Then 등록에 실패한다
     */
    @DisplayName("지하철 구간 등록 예외 처리 - 상행역과 하행역 둘 중 하나도 포함되어있지 않는 경우")
    @Test
    void add_section_without_up_stations_throw_exception() {
        // given
        Station 삼성역 = stationRepository.save(StationTestFixture.삼성역);
        Station 종합운동장 = stationRepository.save(StationTestFixture.종합운동장);
        SectionCreateRequest createRequest = new SectionCreateRequest(삼성역.getId(), 종합운동장.getId(), 3);

        // when
        ExtractableResponse<Response> response = SectionRestAssured.지하철_구간_추가(이호선.getId(), createRequest);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response.jsonPath().getString("message")).isEqualTo(SectionMessage.ERROR_UP_AND_DOWN_STATIONS_ARE_NOT_ENROLLED.message())
        );
    }
}
