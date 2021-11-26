package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.domain.LineStationType;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final Integer DISTANCE_10 = 10;
    private static final Integer DISTANCE_4 = 4;
    private static final Integer DISTANCE_15 = 15;
    private static final String line1Name = "1호선";
    private static final String API_URL = "/lines";
    private static final String STATION_API_URL = "/stations";

    private Long upStationId;
    private Long downStationId;
    private Long addStationId;
    private ExtractableResponse<Response> line1;
    private String sectionUrl;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        // 지하철 역 3개(서울역, 용산역, 추가역) 추가 되어 있음
        Map<LineStationType, Long> idsMap = 상행역_하행역_저장한다(new Station("서울역"), new Station("용산역"));
        ExtractableResponse<Response> addStationResponse = 저장한다(new Station("추가역"),
            STATION_API_URL);

        // 서울역-용산역을 구간으로 가진 1호선 등록되어 있음
        LineRequest lineRequest =
            new LineRequest(line1Name, "blue", idsMap.get(LineStationType.UP),
                idsMap.get(LineStationType.DOWN), DISTANCE_10);
        line1 = 저장한다(lineRequest, API_URL);

        sectionUrl = line1.header("Location") + "/sections";
        upStationId = idsMap.get(LineStationType.UP);
        downStationId = idsMap.get(LineStationType.DOWN);
        addStationId = getLongIdByResponse(addStationResponse);
    }

    @Test
    @DisplayName("상행, 하행 정보를 포함한 라인 생성")
    void create() {
        //given
        //노선 생성 Request
        //상행역, 하행역 존재
        Map<LineStationType, Long> idsMap = 상행역_하행역_저장한다(new Station("구로역"), new Station("안양역"));

        // 이름, 색상, 상행역id, 하행역id, 거리
        LineRequest lineRequest =
            new LineRequest("1호선-천안", "Blue", idsMap.get(LineStationType.UP),
                idsMap.get(LineStationType.DOWN), DISTANCE_10);

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
        // 역 등록되어 있음
        Map<LineStationType, Long> idsMap = 상행역_하행역_저장한다(new Station("강남역"), new Station("역삼역"));

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
            저장한다(new LineRequest(line1Name, "Orange", idsMap.get(LineStationType.UP),
                idsMap.get(LineStationType.DOWN), DISTANCE_10), API_URL);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getList() {
        // given
        // 지하철_노선_등록되어_있음
        Map<LineStationType, Long> idsMap = 상행역_하행역_저장한다(new Station("강남역"), new Station("역삼역"));
        ExtractableResponse<Response> line2 =
            저장한다(new LineRequest("2호선", "green", idsMap.get(LineStationType.UP),
                idsMap.get(LineStationType.DOWN), DISTANCE_10), API_URL);

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
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 조회한다(line1.header("Location"));

        // then
        // 지하철_노선_응답됨
        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.body().jsonPath().get("name").equals(line1Name)).isTrue();
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void update() {
        // given
        // 지하철_노선_등록되어_있음

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

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 삭제한다(line1.header("Location"));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("노선에 하행 구간 추가")
    void addDownSection() {
        //given
        // 지하철 역 3개(서울역, 용산역, 추가역) 추가 되어 있음
        // 서울역-용산역을 구간으로 가진 1호선 등록되어 있음

        //when
        // 용산역-추가역 구간 추가
        SectionRequest sectionRequest =
            new SectionRequest(downStationId, addStationId, DISTANCE_10);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        // 노선 정보에 추가역 추가되어 지하철 역 정보 3개(서울역,용산역,추가역 순으로) 목록 조회됨
        노선정보에구간추가되어_역정보목록정렬되어조회됨(saveResponse, Arrays.asList("서울역", "용산역", "추가역"));
    }

    @Test
    @DisplayName("노선에 상행 구간 추가")
    void addUpSection() {
        //given
        // 지하철 역 3개(서울역, 용산역, 추가역) 추가 되어 있음
        // 서울역-용산역을 구간으로 가진 1호선 등록되어 있음
        //when
        // 추가역-서울역 구간 추가
        SectionRequest sectionRequest = new SectionRequest(addStationId, upStationId, DISTANCE_10);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        // 노선 정보에 역 추가되어 지하철 역 정보 3개(추가역,서울역,용산역 순으로) 목록 조회됨
        노선정보에구간추가되어_역정보목록정렬되어조회됨(saveResponse, Arrays.asList("추가역", "서울역", "용산역"));
    }

    @Test
    @DisplayName("노선에 중간 구간 추가")
    void addMiddleSection() {
        //given
        // 지하철 역 3개(서울역, 용산역, 추가역) 추가 되어 있음
        // 서울역-용산역(distance : 10)을 구간으로 가진 1호선 등록되어 있음

        //when
        // 서울역-추가역(distance: 4) 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, addStationId, DISTANCE_4);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        // 노선 정보에 추가역 추가되어 지하철 역 정보 3개(서울역,추가역,용산역 순으로) 목록 조회됨
        노선정보에구간추가되어_역정보목록정렬되어조회됨(saveResponse, Arrays.asList("서울역", "추가역", "용산역"));
    }

    @Test
    @DisplayName("1호선에 구간 추가 시 상행, 하행이 이미 모두 등록된 경우 BusinessException 발생")
    void addSectionExistBusinessException() {
        //given
        //서울역,용산역을 구간으로 가진 1호선 등록되어 있음

        //when
        //서울역,용산역을 상행-하행으로 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, downStationId, DISTANCE_4);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"상행역, 하행역이 이미 구간으로 등록되어 있습니다." 오류 메세지 반환
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("1호선에 구간 추가 시 상행, 하행이 모두 포함되어 있지 않은 경우 BusinessException 발생")
    void addSectionNotIncludeBusinessException() {
        //given
        //서울역,용산역을 구간으로 가진 1호선 등록되어 있음
        //강남역,역삼역이 저장되어 있음
        Map<LineStationType, Long> idsMap = 상행역_하행역_저장한다(new Station("강남역"), new Station("역삼역"));

        //when
        //강남역-역삼역을 상행-하행으로 구간 추가
        SectionRequest sectionRequest =
            new SectionRequest(idsMap.get(LineStationType.UP), idsMap.get(LineStationType.DOWN),
                DISTANCE_4);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"상행역과 하행역이 포함된 구간이 없습니다." 오류 메세지 반환
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("1호선에 구간 추가 시 새로등록되는 역이 기존 역사보다 길이가 긴 경우 BusinessException 발생")
    void addSectionLongDistanceBusinessException() {
        //given
        //서울역,용산역을 10길이 구간으로 가진 1호선 등록되어 있음
        //추가역 등록되어 있음

        //when
        //서울역-추가역을 상행-하행으로 15길이 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, addStationId, DISTANCE_15);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"구간의 길이가 기존 구간보다 깁니다." 오류 메세지 반환
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("1호선에 구간 추가 시 새로등록되는 역이 기존 역사보다 길이가 같은 경우 BusinessException 발생")
    void addSectionSameDistanceBusinessException() {
        //given
        //서울역,용산역을 10길이 구간으로 가진 1호선 등록되어 있음
        //추가역 등록되어 있음

        //when
        //서울역-추가역을 상행-하행으로 10길이 구간 추가
        SectionRequest sectionRequest = new SectionRequest(upStationId, addStationId, DISTANCE_10);
        ExtractableResponse<Response> saveResponse = 저장한다(sectionRequest, sectionUrl);

        //then
        //"구간의 길이가 기존 구간의 길이와 같습니다." 오류 메세지 반환
        assertThat(saveResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("1호선과 같은 구간(서울역-용산역, 10)을 가지는 1호선-천안 노선 추가")
    void createLine1Cheonan() {
        //given
        //서울역-용산역을 구간으로 가지는 1호선 저장되어 있다.

        //when
        //서울역-용산역을 구간으로 가지는 1호선-천안 노선 생성 요청
        LineRequest lineRequest =
            new LineRequest("1호선-천안", "navy", upStationId, downStationId, DISTANCE_10);
        ExtractableResponse<Response> response = 저장한다(lineRequest, API_URL);

        //then
        //1호선-천안 노선 생성 성공
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 노선정보에구간추가되어_역정보목록정렬되어조회됨(ExtractableResponse<Response> response,
        List<String> stationNames) {
        LineResponse lineResponse = response.body().as(LineResponse.class);
        List<StationResponse> stations = lineResponse.getStations();

        assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            assertThat(stations.size()).isEqualTo(3);
            for (int i = 0; i < stationNames.size(); i++) {
                assertThat(stations.get(i).getName()).isEqualTo(stationNames.get(i));
            }
        });
    }

    private Map<LineStationType, Long> 상행역_하행역_저장한다(Station upStation, Station downStation) {
        ExtractableResponse<Response> upResponse = 저장한다(upStation, STATION_API_URL);
        ExtractableResponse<Response> downResponse = 저장한다(downStation, STATION_API_URL);

        Map<LineStationType, Long> idsMap = new HashMap<>();
        idsMap.put(LineStationType.UP, getLongIdByResponse(upResponse));
        idsMap.put(LineStationType.DOWN, getLongIdByResponse(downResponse));

        return idsMap;
    }

}
