package nextstep.subway.section;

import static nextstep.subway.helper.DomainCreationHelper.지하철구간_상행_기점_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철구간_상행_종점_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철구간_상행_하행_새로_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철구간_이미_둘다_등록된_노선_요청;
import static nextstep.subway.helper.DomainCreationHelper.지하철구간_중간역_상행역_교체_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철구간_중간역_하행역_교체_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철구간_하행_기점_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철노선_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.abstracts.DoBeforeEachAbstract;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철구간 관련 기능")
public class SectionAcceptanceTest extends DoBeforeEachAbstract {

    private Long lineUpStationId;
    private Long lineDownStationId;

    private Long lineId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        lineUpStationId = 지하철역_생성됨("노량진역").jsonPath().getLong("id");
        lineDownStationId = 지하철역_생성됨("남영역").jsonPath().getLong("id");
        lineId = 지하철노선_생성됨("1호선", "bg-blue-600", lineUpStationId, lineDownStationId, 10L).jsonPath().getLong("id");
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 상행_종점 = 지하철구간_상행_종점_생성됨(lineId, "대방역", lineUpStationId, 6L);
        final ExtractableResponse<Response> 하행_종점 = 지하철구간_하행_기점_생성됨(lineId, lineDownStationId, "서울역", 6L);

        // then
        assertThat(상행_종점.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(하행_종점.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        // 지하철_노선에_지하철역_등록됨
        final ExtractableResponse<Response> 노선 = 노선_조회(lineId);
        final List<String> actual = 노선.jsonPath().getList("stations.", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly("대방역", "노량진역", "남영역", "서울역");
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 수 있다.")
    @Test
    void addNewSectionInUpStation() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 상행_종점 = 지하철구간_상행_종점_생성됨(lineId, "대방역", lineUpStationId, 6L);

        // then
        assertThat(상행_종점.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        // 지하철_노선에_지하철역_등록됨
        final ExtractableResponse<Response> 노선 = 노선_조회(lineId);
        final List<String> actual = 노선.jsonPath().getList("stations.", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly("대방역", "노량진역", "남영역");
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 수 있다.")
    @Test
    void addNewSectionInDownStation() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 하행_종점 = 지하철구간_하행_기점_생성됨(lineId, lineDownStationId, "서울역", 6L);

        // then
        assertThat(하행_종점.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        // 지하철_노선에_지하철역_등록됨
        final ExtractableResponse<Response> 노선 = 노선_조회(lineId);
        final List<String> actual = 노선.jsonPath().getList("stations.", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly("노량진역", "남영역", "서울역");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void addSectionBothStationAlreadyExistsException() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 결과 = 지하철구간_이미_둘다_등록된_노선_요청(lineId, lineUpStationId, lineDownStationId, 10L);

        // then
        assertThat(결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void addSectionBetweenException() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 상행_기점 = 지하철구간_상행_기점_생성됨(lineId, lineUpStationId, "용산역", 10L);

        // then
        assertThat(상행_기점.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("역 사이에 새로운 역을 중간역(상행역 교체)으로 등록할 수 있다.")
    @Test
    void addSectionBetweenUpStation() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 상행_기점 = 지하철구간_중간역_상행역_교체_생성됨(lineId, lineUpStationId, "용산역", 5L);

        // then
        assertThat(상행_기점.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        // 지하철_노선에_지하철역_등록됨
        final ExtractableResponse<Response> 노선 = 노선_조회(lineId);
        final List<String> actual = 노선.jsonPath().getList("stations.", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly("노량진역", "용산역", "남영역");
    }

    @DisplayName("역 사이에 새로운 역을 중간역(하행역 교체)으로 등록할 수 있다.")
    @Test
    void addSectionBetweenDownStation() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 하행_교체 = 지하철구간_중간역_하행역_교체_생성됨(lineId, "용산역", lineDownStationId, 5L);

        // then
        assertThat(하행_교체.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        // 지하철_노선에_지하철역_등록됨
        final ExtractableResponse<Response> 노선 = 노선_조회(lineId);
        final List<String> actual = 노선.jsonPath().getList("stations.", StationResponse.class).stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(actual).containsExactly("노량진역", "용산역", "남영역");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void addSectionBothNotExistsException() {
        // when
        // 지하철_노선에_지하철_구간_등록_요청
        final ExtractableResponse<Response> 결과 = 지하철구간_상행_하행_새로_생성됨(lineId, "신길역", "시청역", 18L);

        // then
        assertThat(결과.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 노선_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }
}
