package nextstep.subway.section;
import static nextstep.subway.station.StationTestFixture.requestCreateStation;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineTestFixture;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@DisplayName("구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    StationResponse 강남역;
    StationResponse 광교역;
    StationResponse 역삼역;
    StationResponse 상행신설역;
    StationResponse 하행신설역;
    LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanup.execute();

        // given
        강남역 = requestCreateStation("강남역").as(StationResponse.class);
        광교역 = requestCreateStation("광교역").as(StationResponse.class);
        역삼역 = requestCreateStation("역삼역").as(StationResponse.class);
        상행신설역 = requestCreateStation("상행신설역").as(StationResponse.class);
        하행신설역 = requestCreateStation("하행신설역").as(StationResponse.class);
        신분당선 = LineTestFixture.requestCreateLine("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10)
                .as(LineResponse.class);
    }

    /**
     * When 지하철_노선에_지하철역_등록_요청
     * Then 지하철_노선에_지하철역_등록됨
     */
    @DisplayName("노선 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        ExtractableResponse<Response> response =
                SectionTestFixture.requestAddSection(신분당선.getId().toString(), 강남역.getId(), 역삼역.getId(), 9);
        // then
        LineResponse lineResponse =
                LineTestFixture.requestGetLine(신분당선.getId()).jsonPath().getObject(".",LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(lineResponse.getStations()).hasSize(3);
        assertThat(lineResponse.getStations().get(1).getName()).isEqualTo(역삼역.getName());
    }

    /**
     * When 새로운 역을 상행 종점으로 등록한다.
     * Then 기존 구간과 상행 종점으로 등록한 구간이 함께 조회된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSectionUpStation() {
        // when
        SectionTestFixture.requestAddSection(신분당선.getId().toString(), 상행신설역.getId(), 강남역.getId(), 10);
        // then
        LineResponse lineResponse =
                LineTestFixture.requestGetLine(신분당선.getId()).jsonPath().getObject(".",LineResponse.class);

        assertThat(lineResponse.getStations().get(0).getName()).isEqualTo(상행신설역.getName());
    }

    /**
     * When 새로운 역을 하행 종점으로 등록한다.
     * Then 기존 구간과 하행 종점으로 등록한 구간이 함께 조회된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSectionDownStation() {
        // when
        SectionTestFixture.requestAddSection(신분당선.getId().toString(), 광교역.getId(), 하행신설역.getId(), 10);
        // then
        List<StationResponse> stations =
                LineTestFixture.requestGetLine(신분당선.getId()).jsonPath().getObject(".",LineResponse.class).getStations();

        assertThat(stations.get(stations.size() - 1).getName()).isEqualTo(하행신설역.getName());
    }

    /**
     * When 기존 역 사이에 새로운 역을 기존길이보다 길게 등록한다.
     * Then BAD_REQUEST 를 응답한다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void checkDistanceValidation() {
        // when
        ExtractableResponse<Response> response =
                SectionTestFixture.requestAddSection(신분당선.getId().toString(), 강남역.getId(), 역삼역.getId(), 10);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 기존 노선 뒤에 새로운 역을 하행 종점으로 등록한다.
     * When 기존 노선의 상행역과 신규 하행역으로 등록을 한다.
     * Then Then BAD_REQUEST 를 응답한다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void checkExistSectionValidation() {
        // given
        SectionTestFixture.requestAddSection(신분당선.getId().toString(), 광교역.getId(), 하행신설역.getId(), 10);
        // given
        ExtractableResponse<Response> response =
                SectionTestFixture.requestAddSection(신분당선.getId().toString(), 강남역.getId(), 하행신설역.getId(), 1);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 노선에 신규상행역과 신규하행역으로 등록을 한다.
     * Then Then BAD_REQUEST 를 응답한다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void checkExistUpOrDownValidation() {
        // given
        ExtractableResponse<Response> response =
                SectionTestFixture.requestAddSection(신분당선.getId().toString(), 상행신설역.getId(), 하행신설역.getId(), 1);
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 강남 - 광교역 뒤에 하행신설역을 추가한다.
     * When 상행종점인 강남역을 제거한다.
     * Then 광교역이 상행종점이 된다.
     */
    @DisplayName("상행종점을 제거하는 경우 다음역이 종점이 됨")
    @Test
    void deleteUpStation() {
        // given
        SectionTestFixture.requestAddSection(신분당선.getId().toString(), 광교역.getId(), 하행신설역.getId(), 1);
        // when
        SectionTestFixture.requestRemoveSection(신분당선.getId().toString(), 강남역.getId());
        // then
        List<StationResponse> stations =
                LineTestFixture.requestGetLine(신분당선.getId()).jsonPath().getObject(".",LineResponse.class).getStations();

        assertThat(stations.get(0).getName()).isEqualTo(광교역.getName());
    }

    /**
     * Given 상행신설역을 강남 - 광교 앞에 추가한다.
     * When 하행종점인 광교역을 제거한다.
     * Then 강남역이 하행종점이 된다.
     */
    @DisplayName("하행종점을 제거하는 경우 다음역이 종점이 됨")
    @Test
    void deleteDownStation() {
        // given
        SectionTestFixture.requestAddSection(신분당선.getId().toString(), 상행신설역.getId(), 강남역.getId(), 1);
        // when
        SectionTestFixture.requestRemoveSection(신분당선.getId().toString(), 광교역.getId());
        // then
        List<StationResponse> stations =
                LineTestFixture.requestGetLine(신분당선.getId()).jsonPath().getObject(".",LineResponse.class).getStations();

        assertThat(stations.get(stations.size() - 1).getName()).isEqualTo(강남역.getName());
    }

    /**
     * Given 강남 - 광교역 뒤에 하행신설역을 추가한다.
     * When 중간역인 광교역을 제거한다.
     * Then 강남역과 하행신설역이 구간으로 재배치 됨
     */
    @DisplayName("중간역이 제거될 경우 재배치를 함")
    @Test
    void deleteMiddleStation() {
        // given
        SectionTestFixture.requestAddSection(신분당선.getId().toString(), 광교역.getId(), 하행신설역.getId(), 1);
        // when
        SectionTestFixture.requestRemoveSection(신분당선.getId().toString(), 광교역.getId());
        // then
        List<StationResponse> stations =
                LineTestFixture.requestGetLine(신분당선.getId()).jsonPath().getObject(".",LineResponse.class).getStations();

        assertThat(stations.get(0).getName()).isEqualTo(강남역.getName());
        assertThat(stations.get(1).getName()).isEqualTo(하행신설역.getName());
    }

}
