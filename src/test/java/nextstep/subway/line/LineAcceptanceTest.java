package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.application.LineService;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Autowired
    StationRepository stationRepository;

    @Autowired
    LineRepository lineRepository;

    @Autowired
    LineService lineService;

    ExtractableResponse createResponse;

    Station 신사역;
    Station 강남역;
    Station 양재역;
    Station 판교역;

    @BeforeEach
    void 미리_노선_및_역_생성() {

        신사역 = stationRepository.save(new Station("신사역"));
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        판교역 = stationRepository.save(new Station("판교역"));

        LineRequest lineRequest = 노선_요청_정보("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);

        createResponse = 노선_생성(lineRequest);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성() {
        // then
        // 지하철_노선_생성됨
        assertThat(createResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성_중복_발생() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = 노선_요청_정보("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse response = 노선_생성(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = 노선_요청_정보("3호선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        노선_생성(lineRequest);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse response = 노선_목록_조회();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> responseList = response.jsonPath().getList(".", LineResponse.class);
        List<String> names = responseList.stream().map(LineResponse::getName).collect(Collectors.toList());
        assertThat(names).containsExactly("신분당선", "3호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long id = 생성_노선_아이디(createResponse);

        lineService.addSection(id, 양재역.getId(), 판교역.getId(), 3);
        lineService.addSection(id, 신사역.getId(), 강남역.getId(), 10);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse response = 노선_조회(id);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("신분당선");
        assertThat(response.jsonPath().getObject(".", LineResponse.class)
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                .containsExactly("신사역", "강남역", "양재역", "판교역");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long id = 생성_노선_아이디(createResponse);
        LineRequest fixedRequest = 노선_요청_정보("분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse response = 노선_수정(id, fixedRequest);
        // then
        // 지하철_노선_수정됨
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("분당선");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long id = 생성_노선_아이디(createResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse response = 노선_삭제(id);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
