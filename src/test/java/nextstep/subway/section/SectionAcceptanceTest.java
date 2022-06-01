package nextstep.subway.lineStation;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@DisplayName("노선 구간 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;
    StationResponse 인천역;
    StationResponse 동인천역;
    LineResponse 호선_1;
    LineRequest 생성_요청한_1호선;

    //초기 거리
    private final static int INIT_DISTANCE = 10;


    @BeforeEach
    void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        //given 지하철 역을 생성하고 지하철 노선을 생성한다.
        인천역 = 지하철역을_생성_한다("인천역").as(StationResponse.class);
        동인천역 = 지하철역을_생성_한다("동인천역").as(StationResponse.class);
        LineRequest 생성_요청한_1호선 = new LineRequest("1호선", "파랑색",INIT_DISTANCE, 인천역.getId(), 동인천역.getId());

        호선_1 = 노선을_생성한다(생성_요청한_1호선).as(LineResponse.class);

    }

    /**
     * given 지하철 역을 생성하고 지하철 노선을 생성한다.
     * when 노선의 구간을 조회한다.
     * then 구간의 정보를 얻는다.
     * */
    @Test
    @DisplayName("노선에 구간을 조회")
    void searchSection() {
        //when 노선의 구간을 조회한다.
        final JsonPath 노선의_구간을_조회 = 노선의_구간을_조회(호선_1.getId(), 1).jsonPath();

        //then 구간의 정보를 얻는다.
        assertAll(
                () -> assertThat(노선의_구간을_조회.getInt("distance")).isEqualTo(10),
                () -> assertThat(노선의_구간을_조회.getString("upStation.name")).isEqualTo("인천역"),
                () -> assertThat(노선의_구간을_조회.getString("downStation.name")).isEqualTo("동인천역")
        );
    }

    @Test
    @DisplayName("노선에 구간 등록")
    void addLineStation() {
        //given 지하철 역을 생성하고 지하철 노선을 추가한다.
        //when 노선을 추가 등록하면
        //then 노선이 등록된다.
        //then 기존의 구간은 거리가 신규 노선의 거리만큼 줄어든다
    }

    @Test
    @DisplayName("기존 노선보다 긴 노선 등록")
    void longAddLinStation() {
        //given 지하철 역을 생성하고 지하철 노선을 추가한다.
        //when 기존 노선 보다 긴 노선을 등록 요청 한다.
        //then 등록이 되지 않는다.
    }

    @Test
    @DisplayName("이미 존재하는 구간을 노선 구간에 등록한다.")
    void addExistLineStation() {
        //given 지하철 역을 생성하고 지하철 노선을 추가한다.
        //when 기존 노선과 같은 노선을 등록한다.
        //then 등록이 되지 않는다.
    }

    @Test
    @DisplayName("상행역과 하행역이 포함되지 않는 노선을 구간에 등록한다.")
    void noStationLine() {
        //given 지하철 역을 생성하고 지하철 노선을 추가한다.
        //when 상행역과 하행역이 포함되지 않는 노선을 구간에 등록 요청한다.
        //then 등록이 되지 않는다.
    }

    @Test
    @DisplayName("새로운 역을 상행 종점에 등록한다.")
    void upStationAddLineStation() {
        //given 지하철 역을 생성하고 지하철 노선을 추가한다.
        //when 상행역 노선의 새로운 역이며 하행역이 기존 상행종점인역인 구간을 등록 요청한다.
        //then 새로운 역이 상행 종점에 등록된다. - 이때는 거리를 신경쓰지 않는다.
    }

    @Test
    @DisplayName("새로운 역을 하행 종점에 등록한다.")
    void downStationAddLineStation() {
        //given 지하철 역을 생성하고 지하철 노선을 추가한다.
        //when 상행역이 기존 하행 종점이며 하행역 노선의 새로운 역인 노선을 등록 요청한다.
        //then 새로운 역을 하행 종점에 등록된다..- 이때는 거리를 신경쓰지 않는다.
    }
}
