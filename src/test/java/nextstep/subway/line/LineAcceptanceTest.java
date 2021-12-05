package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 신사역;
    private StationResponse 삼성역;
    private LineResponse 호선2;

    @BeforeEach
    void setup() {
        super.setUp();

        강남역 = StationAcceptanceTest.지하철역을_생성한다("강남역");
        역삼역 = StationAcceptanceTest.지하철역을_생성한다("역삼역");
        신사역 = StationAcceptanceTest.지하철역을_생성한다("신사역");
        삼성역 = StationAcceptanceTest.지하철역을_생성한다("삼성역");
    }

    @Nested
    @DisplayName("노선 관련 테스트")
    public class AboutLine {
        @DisplayName("지하철 노선을 구역과 함께 생성할 수 있다.")
        @Test
        void createLineWithSection() {
            // when
            // 지하철_노선_생성_요청
            ExtractableResponse<Response> response =
                    LineAcceptanceFixture.노선_생성을_요청한다(역삼역, 강남역, 9, "2호선", "green");

            // then
            // 지하철_노선_생성됨
            같은_응답인지_확인한다(response, HttpStatus.CREATED);
        }

        @DisplayName("지하철 노선을 구역 없이 생성할 수 없다.")
        @Test
        void createLineWithoutSectionException() {
            // when
            // 지하철_노선_생성_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.노선_생성을_요청한다("2호선", "green");

            // then
            // 지하철_노선_생성됨
            같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
        }


        @DisplayName("지하철 노선 중복으로 생성할 수 없다.")
        @Test
        void createLine2() {
            // given
            // 지하철_노선_등록되어_있음
            LineAcceptanceFixture.노선_생성을_요청한다(역삼역, 강남역, 9, "2호선", "green");

            // when
            // 지하철_노선_생성_요청
            ExtractableResponse<Response> response =
                    LineAcceptanceFixture.노선_생성을_요청한다(신사역, 삼성역, 9, "2호선", "green");

            // then
            // 지하철_노선_생성_실패됨
            같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
        }

        @DisplayName("지하철 노선 목록을 구간과 함께 조회한다.")
        @Test
        void getLinesWithSection() {
            // given
            // 지하철_노선_등록되어_있음
            ExtractableResponse<Response> createdResponse1 =
                    LineAcceptanceFixture.노선_생성을_요청한다(역삼역, 강남역, 9, "2호선", "green");
            // 지하철_노선_등록되어_있음
            ExtractableResponse<Response> createdResponse2 =
                    LineAcceptanceFixture.노선_생성을_요청한다(신사역, 삼성역, 6, "3호선", "orange");

            // when
            // 지하철_노선_목록_조회_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.노선목록_조회를_요청한다();

            // then
            // 지하철_노선_목록_응답됨
            같은_응답인지_확인한다(response, HttpStatus.OK);
            // 지하철_노선_목록_포함됨
            지하철_노선_목록인지_확인한다(LineAcceptanceFixture.ofLineResponses(createdResponse1, createdResponse2), response);

        }

        @DisplayName("지하철 노선을 구간과 함께 조회한다.")
        @Test
        void getLineWithSection() {
            // given
            // 지하철_노선_등록되어_있음
            LineResponse 생성된_노선 = LineAcceptanceTest.노선을_생성한다(역삼역, 강남역, 9, "2호선", "green");

            // when
            // 지하철_노선_조회_요청
            ExtractableResponse<Response> 조회된_노선 = LineAcceptanceFixture.노선을_조회를_요청한다(생성된_노선.getId());

            // then
            // 지하철_노선_응답됨
            같은_응답인지_확인한다(조회된_노선, HttpStatus.OK);
            // 지하철_노선_동일_확인됨
            지하철_노선_동일_확인한다(생성된_노선, 조회된_노선);
        }


        @DisplayName("지하철 노선을 구간과 함께 제거할 수 있다.")
        @Test
        void deleteLineWithSection() {
            // given
            // 지하철_노선_등록되어_있음
            LineResponse createdLineResponse = LineAcceptanceTest.노선을_생성한다(역삼역, 강남역, 9, "2호선", "green");

            // when
            // 지하철_노선_제거_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.노선_삭제를_요청한다(createdLineResponse.getId());

            // then
            // 지하철_노선_삭제됨
            같은_응답인지_확인한다(response, HttpStatus.NO_CONTENT);
            // 지하철_노선_삭제_확인됨
            ExtractableResponse<Response> getResponse = LineAcceptanceFixture.노선을_조회를_요청한다(createdLineResponse.getId());
            같은_응답인지_확인한다(getResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    @DisplayName("구간 관련 테스트")
    public class AboutSection {

        @BeforeEach
        void setUp() {
            호선2 = LineAcceptanceTest.노선을_생성한다(강남역, 역삼역, 9, "2호선", "green");
        }

        @DisplayName("새로운 역을 상행 종점으로 등록한다.")
        @Test
        void addSectionAsFirstStation() {
            // given
            // 지하철 역_생성
            StationResponse 신분당역 = StationAcceptanceTest.지하철역을_생성한다("신분당역");

            // when
            // 지하철_노선에_구간_등록_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_추가를_요청한다(호선2.getId(), 신분당역, 강남역, 4);

            // then
            // 지하철_노선에_지하철역_등록됨
            같은_응답인지_확인한다(response, HttpStatus.OK);
            // 지하철_노선_순서_확인
            지하철역이_같은순서인지_확인한다(response, 신분당역, 강남역, 역삼역);
        }

        @DisplayName("새로운 역을 하행 종점으로 등록한다.")
        @Test
        void addSectionAsLastStation() {
            // given
            // 지하철 역_생성
            StationResponse 잠실역 = StationAcceptanceTest.지하철역을_생성한다("잠실역");

            // when
            // 지하철_노선에_지하철역_등록_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_추가를_요청한다(호선2.getId(), 역삼역, 잠실역, 4);

            // then
            // 지하철_노선에_지하철역_등록됨
            같은_응답인지_확인한다(response, HttpStatus.OK);
            // 지하철_노선_순서_확인
            지하철역이_같은순서인지_확인한다(response, 강남역, 역삼역, 잠실역);
        }

        @DisplayName("역 사이에 새로운 역을 등록한다. (위쪽 부터)")
        @Test
        void addSectionAsNewStation() {
            // given
            // 지하철 역_생성
            StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");

            // when
            // 지하철_노선에_지하철역_등록_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_추가를_요청한다(호선2.getId(), 강남역, 강남역삼사이역, 4);

            // then
            // 지하철_노선에_지하철역_등록됨
            같은_응답인지_확인한다(response, HttpStatus.OK);
            // 지하철_노선_순서_확인
            지하철역이_같은순서인지_확인한다(response, 강남역, 강남역삼사이역, 역삼역);
        }

        @DisplayName("역 사이에 새로운 역을 등록한다. (아래쪽 부터)")
        @Test
        void addSectionAsNewStation2() {
            // given
            // 지하철 역_생성
            StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");

            // when
            // 지하철_노선에_지하철역_등록_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_추가를_요청한다(호선2.getId(), 강남역삼사이역, 역삼역, 4);

            // then
            // 지하철_노선에_지하철역_등록됨
            같은_응답인지_확인한다(response, HttpStatus.OK);
            // 지하철_노선_순서_확인
            지하철역이_같은순서인지_확인한다(response, 강남역, 강남역삼사이역, 역삼역);
        }

        @DisplayName("역 사이 요청 역의 길이가 기존 역 길이보다 긴 경우 등록할 수 없다.")
        @Test
        void addSectionWithLongerDistanceThanOrigin() {
            // given
            // 지하철 역_생성
            StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");


            // when
            // 지하철_노선에_지하철역_등록_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_추가를_요청한다(호선2.getId(), 강남역삼사이역, 역삼역, 10);

            // then
            // 지하철_노선에_지하철역_등록_실패
            같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
        }

        @DisplayName("기존에 두가지 역이 모두 등록 되어 있는 경우 등록할 수 없다.")
        @Test
        void addSectionWithAlreadyRegisteredStations() {
            // when
            // 지하철_노선에_지하철역_등록_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_추가를_요청한다(호선2.getId(), 강남역, 역삼역, 4);

            // then
            // 지하철_노선에_지하철역_등록_실패
            같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
        }

        @DisplayName("추가할 역이 모두 등록 되어 있지 않은 경우 등록할 수 없다.")
        @Test
        void addSectionWithNoRegisteredStations() {
            // given
            // 지하철 역_생성
            StationResponse 새로운역1 = StationAcceptanceTest.지하철역을_생성한다("새로운 역1");
            StationResponse 새로운역2 = StationAcceptanceTest.지하철역을_생성한다("새로운 역2");

            // when
            // 지하철_노선에_지하철역_등록_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_추가를_요청한다(호선2.getId(), 새로운역1, 새로운역2, 4);

            // then
            // 지하철_노선에_지하철역_등록_실패
            같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
        }

        @DisplayName("상행 종점을 삭제한다.")
        @Test
        void removeFirstStation() {
            // given
            // 지하철 역_생성
            StationResponse 신분당역 = StationAcceptanceTest.지하철역을_생성한다("신분당역");
            구간을_추가한다(호선2.getId(), 신분당역, 역삼역, 4);

            // when
            // 지하철_노선에_지하철역_삭제_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_삭제를_요청한다(호선2.getId(), 신분당역.getId());


            // then
            // 지하철_노선에_지하철역_삭제됨
            같은_응답인지_확인한다(response, HttpStatus.NO_CONTENT);
            // 지하철_노선_순서_확인
            지하철역이_같은순서인지_확인한다(LineAcceptanceFixture.노선을_조회를_요청한다(호선2.getId()), 강남역, 역삼역);
        }

        @DisplayName("하행 종점을 삭제한다.")
        @Test
        void removeLastStation() {
            // given
            // 지하철 역_생성
            StationResponse 잠실역 = StationAcceptanceTest.지하철역을_생성한다("잠실역");
            구간을_추가한다(호선2.getId(), 역삼역, 잠실역, 4);

            // when
            // 지하철_노선에_지하철역_삭제_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_삭제를_요청한다(호선2.getId(), 잠실역.getId());


            // then
            // 지하철_노선에_지하철역_삭제됨
            같은_응답인지_확인한다(response, HttpStatus.NO_CONTENT);
            // 지하철_노선_삭제_확인
            지하철역이_같은순서인지_확인한다(LineAcceptanceFixture.노선을_조회를_요청한다(호선2.getId()), 강남역, 역삼역);
        }

        @DisplayName("중간 역을 삭제한다.")
        @Test
        void removeStationMiddle() {
            // given
            // 지하철 역_생성
            StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");
            구간을_추가한다(호선2.getId(), 강남역삼사이역, 역삼역, 4);

            // when
            // 지하철_노선에_지하철역_삭제_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_삭제를_요청한다(호선2.getId(), 강남역삼사이역.getId());


            // then
            // 지하철_노선에_지하철역_삭제됨
            같은_응답인지_확인한다(response, HttpStatus.NO_CONTENT);
            // 지하철_노선_삭제_확인
            지하철역이_같은순서인지_확인한다(LineAcceptanceFixture.노선을_조회를_요청한다(호선2.getId()), 강남역, 역삼역);
        }

        @DisplayName("노선에 등록되어있지 않은 역을 제거하려 한다.")
        @Test
        void removeUnRegisteredStationException() {
            // given
            // 지하철 역_생성
            StationResponse 강남역삼사이역 = StationAcceptanceTest.지하철역을_생성한다("강남역삼사이역");
            StationResponse 새로운역1 = StationAcceptanceTest.지하철역을_생성한다("새로운역1");
            구간을_추가한다(호선2.getId(), 강남역삼사이역, 역삼역, 4);

            // when
            // 지하철_노선에_지하철역_삭제_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_삭제를_요청한다(호선2.getId(), 새로운역1.getId());


            // then
            // 지하철_노선에_지하철역_삭제됨
            같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
        }

        @DisplayName("구간이 하나일 때 제거하려 한다.")
        @Test
        void removeLastSectionInLineException() {
            // when
            // 지하철_노선에_지하철역_삭제_요청
            ExtractableResponse<Response> response = LineAcceptanceFixture.구간_삭제를_요청한다(호선2.getId(), 강남역.getId());


            // then
            // 지하철_노선에_지하철역_삭제됨
            같은_응답인지_확인한다(response, HttpStatus.BAD_REQUEST);
        }
    }

    private void 지하철역이_같은순서인지_확인한다(ExtractableResponse<Response> response, StationResponse... stations) {
        assertThat(LineAcceptanceFixture.ofLineResponse(response).getStations())
                .containsExactly(stations);
    }

    public static LineResponse 노선을_생성한다(StationResponse upStation, StationResponse downStation, int distance, String lineName, String lineColor) {
        return LineAcceptanceFixture.ofLineResponse(
                LineAcceptanceFixture.노선_생성을_요청한다(upStation, downStation, distance, lineName, lineColor)
        );
    }

    private LineResponse 구간을_추가한다(Long lineId, StationResponse upStation, StationResponse downStation, int distance) {
        return LineAcceptanceFixture.ofLineResponse(
                LineAcceptanceFixture.구간_추가를_요청한다(lineId, upStation, downStation, distance)
        );
    }

    private void 지하철_노선_동일_확인한다(LineResponse createdLineResponse, ExtractableResponse<Response> response) {
        assertThat(createdLineResponse).isEqualTo(LineAcceptanceFixture.ofLineResponse(response));
    }

    private void 지하철_노선_목록인지_확인한다(List<LineResponse> expectedLines, ExtractableResponse<Response> response) {
        List<LineResponse> lines = LineAcceptanceFixture.ofLineResponses(response);
        assertThat(lines).containsAll(expectedLines);
    }

    private void 같은_응답인지_확인한다(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
