package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.exception.CanNotRemoveStationException;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.common.Constants.*;
import static nextstep.subway.line.LineAcceptanceRequests.*;
import static nextstep.subway.station.StationAcceptanceRequests.requestCreateStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private SectionRepository sectionRepository;

    private Station createStation(String stationName) {
        Station stationSaved = stationRepository.save(new Station(stationName));
        return stationSaved;
    }

    private Section createSection(Station upStation, Station downStation, int distnace) {
        Section sectionSaved = sectionRepository.save(new Section(upStation, downStation, distnace));
        return sectionSaved;
    }

    private Line addSection(Long id, Section section) {
        Section sectionSaved = sectionRepository.save(section);
        Line line = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        sectionSaved.addLine(line);
        return line;
    }


    private Line createLine(String name, String color, Section section) {
        Line lineSaved = lineRepository.save(new Line(name, color, section));
        return lineSaved;
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR,
                createStation("강남역").getId(), createStation("역삼역").getId(), 10);
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine_error() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR,
                createStation("강남역").getId(), createStation("역삼역").getId(), 10);
        requestCreateLine(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = requestCreateLine(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestFirst = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR, 강남역.getId(), 역삼역.getId(), 10);
        ExtractableResponse<Response> createResponse1 = requestCreateLine(lineRequestFirst);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestSecond = new LineRequest(SECOND_LINE_COLOR, SECOND_LINE_NAME, 강남역.getId(), 역삼역.getId(), 5);
        ExtractableResponse<Response> createResponse2 = requestCreateLine(lineRequestSecond);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = requestShowLines();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // 지하철 노선이 등록되어 있음
        LineRequest lineRequest = new LineRequest(NEW_BUNDANG_LINE_NAME, NEW_BUNDANG_LINE_COLOR,
                createStation("강남역").getId(), createStation("역삼역").getId(), 10);
        ExtractableResponse<Response> createResponseLine = requestCreateLine(lineRequest);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = requestShowLine(createResponseLine.jsonPath().getLong("id"));

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestNew = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME,
                createStation("강남역").getId(), createStation("역삼역").getId(), 10);
        ExtractableResponse<Response> createResponse = requestCreateLine(lineRequestNew);

        // when
        // 지하철_노선_수정_요청
        String uri = createResponse.header("Location");
        LineRequest lineRequestOld = new LineRequest(OLD_BUNDANG_LINE_COLOR, OLD_BUNDANG_LINE_NAME, 1L, 2L, 10);
        ExtractableResponse<Response> response = requestUpdateLine(uri, lineRequestOld);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequestNew = new LineRequest(NEW_BUNDANG_LINE_COLOR, NEW_BUNDANG_LINE_NAME,
                createStation("강남역").getId(), createStation("역삼역").getId(), 10);
        ExtractableResponse<Response> createResponse = requestCreateLine(lineRequestNew);

        // when
        // 지하철_노선_제거_요청
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = requestDeleteLine(uri);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection_성공케이스_역사이에_새로운역_등록() {
        //given
        // 지하철_노선_등록되어_있음
        Station 강남역 = createStation("강남역");
        Station 잠실역 = createStation("잠실역");
        Station 역삼역 = createStation("역삼역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(강남역, 잠실역, 10));

        //when
        //노선에 구간 추가 요청함
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 강남역.getId(), 역삼역.getId()));

        //then
        //노선에 구간 추가됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운역을_상행종점으로_등록할 경우")
    @Test
    void addSection_성공케이스_새로운역을_상행종점으로_등록() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(역삼역, 잠실역, 5));


        //when
        //노선에 구간 추가 요청함
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 강남역.getId(), 역삼역.getId()));


        //then
        //노선에 구간 추가됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운역을_하행종점으로_등록할 경우")
    @Test
    void addSection_성공케이스_새로운역을_하행종점으로_등록() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when
        //노선에 구간 추가 요청함
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 역삼역.getId(), 잠실역.getId()));

        //then
        //노선에 구간 추가됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("예외케이스_역사이에_등록시_새로운역구간이_더큰_경우")
    @Test
    void addSection_예외케이스_역사이에_등록시_새로운역구간이_더큰경우() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(10, 강남역.getId(), 잠실역.getId()));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("예외케이스_상행역_하행역_모두_이미노선에_등록된경우")
    @Test
    void addSection_예외케이스_상행역_하행역_모두_이미노선에_등록된경우() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 강남역.getId(), 역삼역.getId()));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("예외케이스_상행역_하행역_모두_기존구간에_포함되어있지않은_경우")
    @Test
    void addSection_예외케이스_상행역_하행역_모두_기존구간에_포함되어있지않은_경우() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Station 잠실나루역 = createStation("잠실나루역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 잠실역.getId(), 잠실나루역.getId()));

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("구간의_가운데역을_제거하는_경우")
    @Test
    void removeStation_성공케이스_가운데역을_삭제() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(역삼역, 잠실역, 5));
        //노선에 구간 추가 요청함
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 강남역.getId(), 역삼역.getId()));

        //when
        ExtractableResponse<Response> removedResponse = requestRemoveStation(line.getId(), 역삼역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간의_상행역을_제거하는_경우")
    @Test
    void removeStation_성공케이스_상행역을_삭제() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(역삼역, 잠실역, 5));
        //노선에 구간 추가 요청함
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 강남역.getId(), 역삼역.getId()));

        //when
        ExtractableResponse<Response> removedResponse = requestRemoveStation(line.getId(), 강남역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("구간의_상행역을_제거하는_경우")
    @Test
    void removeStation_성공케이스_하행역을_삭제() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(역삼역, 잠실역, 5));
        //노선에 구간 추가 요청함
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 강남역.getId(), 역삼역.getId()));

        //when
        ExtractableResponse<Response> removedResponse = requestRemoveStation(line.getId(), 잠실역.getId());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("예외케이스_없는역을_제거하는_경우")
    @Test
    void removeStation_예외케이스_없는역_삭제() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Station 잠실역 = createStation("잠실역");
        Station 잠실나루 = createStation("잠실나루");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(역삼역, 잠실역, 5));
        //노선에 구간 추가 요청함
        ExtractableResponse<Response> response = requestAddSection(line.getId(), new SectionRequest(5, 강남역.getId(), 역삼역.getId()));

        //when-then
        ExtractableResponse<Response> removedResponse = requestRemoveStation(line.getId(), 잠실나루.getId());

        //then
        assertThat(removedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("예외케이스_구간하나일때_제거하는_경우")
    @Test
    void removeStation_예외케이스_구간하나일때_삭제() {
        //given
        //구간이 등록되어 있음
        Station 강남역 = createStation("강남역");
        Station 역삼역 = createStation("역삼역");
        Line line = createLine(SECOND_LINE_NAME, SECOND_LINE_COLOR, new Section(강남역, 역삼역, 5));

        //when-then
        ExtractableResponse<Response> removedResponse = requestRemoveStation(line.getId(), 강남역.getId());

        //then
        assertThat(removedResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }
}
