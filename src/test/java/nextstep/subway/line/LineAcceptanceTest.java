package nextstep.subway.line;

import static nextstep.subway.line.LineAcceptanceFixture.LineFixture.*;
import static nextstep.subway.line.LineAcceptanceFixture.PATH;
import static nextstep.subway.line.LineAcceptanceFixture.SectionFixture.*;
import static nextstep.subway.line.LineAcceptanceFixture.*;
import static nextstep.subway.station.StationAcceptanceFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.StationAcceptanceFixture;
import nextstep.subway.station.domain.Station;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 잠실역;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_생성_요청("강남역", StationAcceptanceFixture::toStation);
        역삼역 = 지하철역_생성_요청("역삼역", StationAcceptanceFixture::toStation);
        선릉역 = 지하철역_생성_요청("선릉역", StationAcceptanceFixture::toStation);
        잠실역 = 지하철역_생성_요청("잠실역", StationAcceptanceFixture::toStation);
    }

    @DisplayName("지하철 구간을 추가한다.")
    @Test
    void given_Line_when_AddSection_then_ReturnOk() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);
        final List<Station> expected = Arrays.asList(강남역, 역삼역, 선릉역);

        // when
        final ExtractableResponse<Response> response = 지하철_구간에_지하철역_등록_요청(PATH + "/1/sections", SECTION);

        // then
        assertAll(
            () -> assertThat(statusCode(response)).isEqualTo(statusCode(OK)),
            () -> assertThat(toStations(response)).isEqualTo(expected)
        );
    }

    @DisplayName("기존에 존재하는 구간을 추가한다.")
    @Test
    void given_DuplicatedSection_when_AddExistingSection_then_ReturnBadRequest() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_구간에_지하철역_등록_요청(PATH + "/1/sections", DUPLICATED_SECTION);

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(BAD_REQUEST));
    }

    @DisplayName("기존 노선에 존재하지 않는 상행역과 하행역이 포함된 구간을 추가한다.")
    @Test
    void given_SectionHasStationsLineNotContain_when_AddExistingSection_then_ReturnBadRequest() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_구간에_지하철역_등록_요청(PATH + "/1/sections", SECTION_NEW_STATION);

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(BAD_REQUEST));
    }

    @DisplayName("상행역과 하행역 거리가 이미 등록된 역 간 거리와 같은 구간을 추가한다.")
    @Test
    void given_TooLongDistance_when_AddSection_then_ReturnBadRequest() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_구간에_지하철역_등록_요청(PATH + "/1/sections", TOO_LONG_DISTANCE);

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(BAD_REQUEST));
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void given_NoExisingLine_when_CreateLineWithStation_then_ReturnLine() {
        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(PATH, FIRST);

        // then
        assertAll(
            () -> assertThat(statusCode(response)).isEqualTo(statusCode(CREATED)),
            () -> assertThat(response.header("Location")).isNotBlank(),
            () -> assertThat(toLine(response)).isEqualTo(FIRST.line()),
            () -> assertThat(toStations(response)).isEqualTo(Arrays.asList(강남역, 역삼역))
        );
    }

    private int statusCode(final ExtractableResponse<Response> response) {
        return response.statusCode();
    }

    private int statusCode(final HttpStatus httpStatus) {
        return httpStatus.value();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void given_ExistingLine_when_CreateLineAlreadyExisting_then_ReturnBadRequest() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_생성_요청(PATH, FIRST);

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(BAD_REQUEST));
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void given_ExistingLine_when_SearchAllLine_then_ReturnLines() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);
        지하철_노선_생성_요청(PATH, SECOND);

        // when
        final ExtractableResponse<Response> response = 지하철_노션_조회_요청(PATH);

        // then
        assertAll(
            () -> assertThat(statusCode(response)).isEqualTo(statusCode(OK)),
            () -> assertThat(actual(response)).isEqualTo(expected())
        );
    }

    @SuppressWarnings("unchecked")
    private List<Line> actual(final ExtractableResponse<Response> response) {
        return response.jsonPath()
            .getList(".")
            .stream()
            .map(obj -> (Map<String, Object>)obj)
            .map(map -> new Line(Long.parseLong(String.valueOf(map.get("id"))), (String)map.get("name"),
                (String)map.get("color")))
            .collect(Collectors.toList());
    }

    private List<Line> expected() {
        return Arrays.asList(FIRST.line(), SECOND.line());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void given_ExistingLine_when_SearchLine_returnLine() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노션_조회_요청(PATH + "/" + FIRST.getId());

        // then
        assertAll(
            () -> assertThat(statusCode(response)).isEqualTo(statusCode(OK)),
            () -> assertThat(toLine(response)).isEqualTo(FIRST.line())
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void given_ExistingLine_when_ModifyLine_then_ReturnOkStatus() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_수정_요청(PATH + "/" + FIRST.getId(), SECOND);

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(OK));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void given_ExistingLine_when_DeleteLine_then_ReturnNoContentStatus() {
        // given
        지하철_노선_생성_요청(PATH, FIRST);

        // when
        final ExtractableResponse<Response> response = 지하철_노선_제거_요청(PATH + "/" + FIRST.getId());

        // then
        assertThat(statusCode(response)).isEqualTo(statusCode(NO_CONTENT));
    }
}
