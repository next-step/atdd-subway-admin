package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.common.AcceptanceTest;
import nextstep.subway.line.LineTestFixture;
import nextstep.subway.station.StationResponse;
import nextstep.subway.station.StationTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("구간 인수 테스트")
class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 상현역;

    private long lineId;

    // (공통) Given 이미 생성된 구간이 존재하고
    @BeforeEach
    void init() {
        강남역 = StationTestFixture.create("강남역").extract().as(StationResponse.class);
        상현역 = StationTestFixture.create("상현역").extract().as(StationResponse.class);
        lineId = LineTestFixture.create("신분당선", "bg-red-600", 강남역, 상현역, 100).extract().jsonPath().getLong("id");
    }

    /**
     * When 구간 사이에 등록되지 않은 지하철 역을 중간에 등록하면
     * Then 지하철역이 등록된다.
     */
    @DisplayName("역 사이에 새로운 역 등록")
    @Test
    void sliceSection() {
        StationResponse 양재역 = StationTestFixture.create("양재역").extract().as(StationResponse.class);
        StationResponse 판교역 = StationTestFixture.create("판교역").extract().as(StationResponse.class);
        StationResponse 정자역 = StationTestFixture.create("정자역").extract().as(StationResponse.class);

        int 양재_응답_코드 = LineTestFixture.createSection(lineId, 강남역, 양재역, 30).extract().statusCode();
        int 판교_응답_코드 = LineTestFixture.createSection(lineId, 강남역, 판교역, 50).extract().statusCode();
        int 정자_응답_코드 = LineTestFixture.createSection(lineId, 강남역, 정자역, 60).extract().statusCode();

        assertAll(
                () -> assertThat(양재_응답_코드).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(판교_응답_코드).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(정자_응답_코드).isEqualTo(HttpStatus.CREATED.value())
        );
    }

    /*
    * When 구간 사이에 등록되지 않은 지하철 역을 상행 종점으로 등록하면
    * Then 지하철 역이 등록된다.
    * */
    @DisplayName("새로운 역을 상행 종점으로 등록")
    @Test
    void extendUpStationSection() {
        StationResponse 신사역 = StationTestFixture.create("신사역").extract().as(StationResponse.class);
        ExtractableResponse<Response> response = LineTestFixture.createSection(lineId, 신사역, 강남역, 30).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /*
     * When 구간 사이에 등록되지 않은 지하철 역을 하행 종점으로 등록하면
     * Then 지하철 역이 등록된다.
     * */
    @DisplayName("새로운 역을 하행 종점으로 등록")
    @Test
    void extendDownStationSection() {
        StationResponse 광교중앙역 = StationTestFixture.create("광교중앙역").extract().as(StationResponse.class);
        ExtractableResponse<Response> response = LineTestFixture.createSection(lineId, 상현역, 광교중앙역, 10).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /*
     * When 구간 사이에 등록되지 않은 역과 길이를 기존 역 사이 길이보다 크게 지정해서 등록하면
     * Then 지하철 역이 등록되지 않는다.
     * */
    @DisplayName("역 사이에 새로운 역을 등록하는 경우 기존 역 사이 길이보다 크거나 같으면 등록 불가")
    @Test
    void registerOverSectionLengthStationError() {
        StationResponse 판교역 = StationTestFixture.create("판교역").extract().as(StationResponse.class);
        ExtractableResponse<Response> response = LineTestFixture.createSection(lineId, 강남역, 판교역, 100).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /*
     * When 구간 사이에 이미 등록된 상행, 하행 두 종점역을 등록하면
     * Then 지하철 역이 등록되지 않는다.
     * */
    @DisplayName("이미 노선에 모두 등록된 상행 종점, 하행 종점역 등록")
    @Test
    void registerUpAndDownStationError() {
        ExtractableResponse<Response> response = LineTestFixture.createSection(lineId, 강남역, 상현역, 50).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /*
     * When 구간에 등록되지 않은 두 역을 등록하면
     * Then 지하철 역이 등록되지 않는다.
     * */
    @DisplayName("등록하려는 두 역이 모두 노선에 포함되지 않은 경우")
    @Test
    void registerBothNoneStation() {
        StationResponse 판교역 = StationTestFixture.create("판교역").extract().as(StationResponse.class);
        StationResponse 정자역 = StationTestFixture.create("정자역").extract().as(StationResponse.class);
        ExtractableResponse<Response> response = LineTestFixture.createSection(lineId, 판교역, 정자역, 50).extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
