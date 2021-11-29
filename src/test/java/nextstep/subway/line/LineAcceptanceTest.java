package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String API_URL = "/lines";
    private static final String STATION_API_URL = "/stations";

    private Long upStationId;
    private Long downStationId;
    private Long addStationId;
    private ExtractableResponse<Response> line1;
    private String sectionUrl;

    @Test
    @DisplayName("상행, 하행 정보를 포함한 1호선 생성")
    void create() {
        //given
        //노선 생성 Request
        //상행역, 하행역 존재
        Long 상행역_ID = getLongIdByResponse(저장한다(new Station("상행역"), STATION_API_URL));
        Long 하행역_ID = getLongIdByResponse(저장한다(new Station("하행역"), STATION_API_URL));

        // 이름, 색상, 상행역id, 하행역id, 거리
        LineRequest lineRequest =
            new LineRequest("1호선", "Blue", 상행역_ID, 하행역_ID, 10);

        //when
        //지하철 노선 생성 (상행, 하행 역 정보 포함) 요청
        ExtractableResponse<Response> response = 저장한다(lineRequest, API_URL);

        //then
        //지하철 노선 생성됨
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(response.body().jsonPath().getList("stations", StationResponse.class).size())
                .isEqualTo(2);
        });
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();
        // 역 등록되어 있음
        Long 상행_서울역_ID = getLongIdByResponse(저장한다(new Station("서울역"), STATION_API_URL));
        Long 햐행_용산역_ID = getLongIdByResponse(저장한다(new Station("용산역"), STATION_API_URL));

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
            저장한다(new LineRequest("1호선", "Orange", 상행_서울역_ID, 햐행_용산역_ID, 10), API_URL);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getList() {
        // given
        // 지하철_노선_등록되어_있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        Long 역삼역_ID = getLongIdByResponse(저장한다(new Station("역삼역"), STATION_API_URL));
        Long 강남역_ID = getLongIdByResponse(저장한다(new Station("강남역"), STATION_API_URL));
        ExtractableResponse<Response> line2 =
            저장한다(new LineRequest("2호선", "green", 강남역_ID, 역삼역_ID, 10), API_URL);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 조회한다(API_URL);

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = getIdsByResponse(Arrays.asList(line1, line2), Long.class);
        List<Long> resultLineIds = response.jsonPath().getList("id", Long.class);

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(resultLineIds).containsAll(expectedLineIds);
        });
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getOne() {
        // given
        // 지하철_노선_등록되어_있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 조회한다(line1.header("Location"));

        // then
        // 지하철_노선_응답됨
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().get("name").equals("1호선")).isTrue();
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        // given
        // 지하철_노선_등록되어_있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response =
            수정한다(new LineRequest("3호선", "Orange"), line1.header("Location"));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void delete() {
        // given
        // 지하철_노선_등록되어_있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 삭제한다(line1.header("Location"));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)에 하행 구간(하행역-추가역, 10) 추가")
    void addDownSection() {
        //given
        // 지하철 역 3개(상행역, 하행역, 추가역) 추가 되어 있음
        // 상행역-하행역을 구간으로 가진 1호선 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //when
        // 하행역-추가역 구간 추가
        SectionRequest sectionRequest =
            new SectionRequest(downStationId, addStationId, 10);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        // 노선 정보에 추가역 추가되어 지하철 역 정보 3개(서울역,하행역,추가역 순으로) 목록 조회됨
        노선정보에구간추가되어_역정보목록정렬되어조회됨(saveResponse, "상행역", "하행역", "추가역");
    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)에 상행 구간(추가역-상행역, 10) 추가")
    void addUpSection() {
        //given
        // 지하철 역 3개(상행역, 하행역, 추가역) 추가 되어 있음
        // 상행역-하행역을 구간으로 가진 1호선 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //when
        // 추가역-서울역 구간 추가
        SectionRequest sectionRequest = new SectionRequest(addStationId, upStationId, 10);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        // 노선 정보에 역 추가되어 지하철 역 정보 3개(추가역,서울역,하행역 순으로) 목록 조회됨
        노선정보에구간추가되어_역정보목록정렬되어조회됨(saveResponse, "추가역", "상행역", "하행역");
    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)에 중간 구간(상행역-추가역, 4) 추가")
    void addMiddleSection() {
        //given
        // 지하철 역 3개(상행역, 하행역, 추가역) 추가 되어 있음
        // 상행역-하행역(distance : 10)을 구간으로 가진 1호선 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //when
        // 서울역-추가역(distance: 4) 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, addStationId, 4);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        // 노선 정보에 추가역 추가되어 지하철 역 정보 3개(서울역,추가역,하행역 순으로) 목록 조회됨
        노선정보에구간추가되어_역정보목록정렬되어조회됨(saveResponse, "상행역", "추가역", "하행역");
    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)에 구간(상행역-하행역, 4) 추가 시 상행, 하행이 이미 모두 등록된 경우 구간 등록할 수 없음")
    void addSectionAlreadyExistFail() {
        //given
        //상행역,하행역을 구간으로 가진 1호선 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //when
        //서울역,하행역을 상행-하행으로 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, 4);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"상행역, 하행역이 이미 구간으로 등록되어 있습니다." 오류 메세지 반환
        assertAll(() -> {
            assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertExceptionMessage(saveResponse, "상행역, 하행역이 이미 구간으로 등록되어 있습니다.");
        });
    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)에 구간(강남역-역삼역, 4) 추가 시 상행, 하행이 모두 포함되어 있지 않은 경우 구간 등록할 수 없음")
    void addSectionNotIncludeFail() {
        //given
        //상행역,하행역을 구간으로 가진 1호선 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //강남역,역삼역이 저장되어 있음
        Long 강남역ID = getLongIdByResponse(저장한다(new Station("강남역"), STATION_API_URL));
        Long 역삼역ID = getLongIdByResponse(저장한다(new Station("역삼역"), STATION_API_URL));

        //when
        //강남역-역삼역을 상행-하행으로 구간 추가
        SectionRequest sectionRequest = new SectionRequest(강남역ID, 역삼역ID, 4);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"상행역과 하행역이 포함된 구간이 없습니다." 오류 메세지 반환
        assertAll(() -> {
            assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertExceptionMessage(saveResponse, "상행역과 하행역이 포함된 구간이 없습니다.");
        });
    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)에 구간(상행역-추가역, 15) 추가 시 새로등록되는 역이 기존 역사보다 길이가 긴 경우 구간 등록할 수 없음")
    void addSectionLongDistanceFail() {
        //given
        //상행역,하행역을 10길이 구간으로 가진 1호선, 추가역 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //when
        //상행역-추가역을 상행-하행으로 15길이 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, addStationId, 15);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"구간의 길이는 기존 구간보다 작아야 합니다." 오류 메세지 반환
        assertAll(() -> {
            assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertExceptionMessage(saveResponse, "구간의 길이는 기존 구간보다 작아야 합니다.");
        });
    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)에 구간(상행역-추가역, 10) 추가 시 새로등록되는 역이 기존 역사와 길이가 같은 경우 구간 등록할 수 없음")
    void addSectionSameDistanceFail() {
        //given
        //상행역,하행역을 10길이 구간으로 가진 1호선, 추가역 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //when
        //상행역-추가역을 상행-하행으로 10길이 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, addStationId, 10);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"구간의 길이는 기존 구간보다 작아야 합니다." 오류 메세지 반환
        assertAll(() -> {
            assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertExceptionMessage(saveResponse, "구간의 길이는 기존 구간보다 작아야 합니다.");
        });

    }

    @Test
    @DisplayName("기존 1호선(상행역-하행역, 10)과 같은 구간(상행역-하행역, 10)을 가지는 1호선-천안 노선 추가")
    void createLine1Cheonan() {
        //given
        //상행역,하행역을 10길이 구간으로 가진 1호선, 추가역 등록되어 있음
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        //when
        //상행역-하행역을 구간으로 가지는 1호선-천안 노선 생성 요청
        LineRequest lineRequest =
            new LineRequest("1호선-천안", "navy", upStationId, downStationId, 10);
        ExtractableResponse<Response> response = 저장한다(lineRequest, API_URL);

        //then
        //1호선-천안 노선 생성 성공
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("1호선(상행역-추가역-하행역, 5-5)에서 추가역 삭제 성공")
    void deleteLine1AddStation() {
        //given
        상행역_5_추가역_5_하행역을_노선으로가지는_1호선_저장되어있다();

        // when
        // 추가역_제거_요청
        ExtractableResponse<Response> response = 삭제한다(getSectionDeleteUrl(addStationId));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("1호선(상행역-추가역-하행역, 5-5)에서 상행역 삭제 성공")
    void deleteLine1UpStation() {
        //given
        상행역_5_추가역_5_하행역을_노선으로가지는_1호선_저장되어있다();

        // when
        // 추가역_제거_요청
        ExtractableResponse<Response> deletedResponse = 삭제한다(getSectionDeleteUrl(upStationId));

        // then
        // 지하철_노선_삭제됨
        assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("1호선(상행역-추가역-하행역, 5-5)에서 하행역 삭제 성공")
    void deleteLine1DownStation() {
        //given
        상행역_5_추가역_5_하행역을_노선으로가지는_1호선_저장되어있다();

        // when
        // 추가역_제거_요청
        ExtractableResponse<Response> deletedResponse = 삭제한다(getSectionDeleteUrl(downStationId));

        // then
        // 지하철_노선_삭제됨
        assertThat(deletedResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("1호선(상행역-추가역-하행역, 10-10)에서 노선에 포함되지 않은 강남역 삭제 요청 시 실패")
    void deleteLine1NotIncludeStation() {
        //given
        상행역_5_추가역_5_하행역을_노선으로가지는_1호선_저장되어있다();
        //강남역 저장되어 있음
        Long 강남역ID = getLongIdByResponse(저장한다(new Station("강남역"), STATION_API_URL));

        // when
        // 강남역_제거_요청 시
        ExtractableResponse<Response> response = 삭제한다(getSectionDeleteUrl(강남역ID));

        //then
        //"구간으로 등록되어 있지 않은 역입니다." 오류 메세지 반환
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertExceptionMessage(response, "구간으로 등록되어 있지 않은 역입니다.");
        });
    }

    @Test
    @DisplayName("구간이 하나만 있는 1호선(상행역-하행역, 10)에서 상행역 삭제 요청 시 등록된 구간이 하나밖에 없어서 실패")
    void deleteLine1CannotDeleteStation() {
        //given
        상행역_하행역_추가역_AND_1호선_저장되어있다();

        // when
        // 강남역_제거_요청 시
        ExtractableResponse<Response> response = 삭제한다(getSectionDeleteUrl(upStationId));

        //then
        //"노선에 등록된 구간이 1개만 존재하므로 삭제할 수 없습니다." 오류 메세지 반환
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertExceptionMessage(response, "노선에 등록된 구간이 1개만 존재하므로 삭제할 수 없습니다.");
        });
    }

    private void 노선정보에구간추가되어_역정보목록정렬되어조회됨(ExtractableResponse<Response> response,
        String... stationNames) {
        LineResponse lineResponse = response.body().as(LineResponse.class);
        List<StationResponse> stations = lineResponse.getStations();
        List<String> actualNames = stations.stream()
            .map(stationResponse -> stationResponse.getName())
            .collect(Collectors.toList());

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(stations.size()).isEqualTo(stationNames.length);
            assertThat(actualNames).containsExactly(stationNames);
        });
    }

    private void 상행역_하행역_추가역_AND_1호선_저장되어있다() {
        // given
        // 지하철 역 3개(상행역, 하행역, 추가역) 추가 되어 있음
        upStationId = getLongIdByResponse(저장한다(new Station("상행역"), STATION_API_URL));
        downStationId = getLongIdByResponse(저장한다(new Station("하행역"), STATION_API_URL));
        addStationId = getLongIdByResponse(저장한다(new Station("추가역"), STATION_API_URL));

        // 상행역-하행역을 구간으로 가진 1호선 등록되어 있음
        LineRequest lineRequest =
            new LineRequest("1호선", "blue", upStationId, downStationId, 10);
        line1 = 저장한다(lineRequest, API_URL);
        sectionUrl = line1.header("Location") + "/sections";
    }

    private void 상행역_5_추가역_5_하행역을_노선으로가지는_1호선_저장되어있다() {
        상행역_하행역_추가역_AND_1호선_저장되어있다();
        // 서울역-추가역(distance: 5) 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, addStationId, 5);
        저장한다(sectionRequest, sectionUrl);
    }

    private String getSectionDeleteUrl(Long targetId) {
        return sectionUrl+"?stationId="+targetId;
    }

    private void assertExceptionMessage(ExtractableResponse<Response> response, String message) {
        assertThat(response.body().jsonPath().getString("message")).isEqualTo(message);
    }

}
