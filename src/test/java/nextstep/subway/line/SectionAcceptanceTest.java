package nextstep.subway.line;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.AcceptanceTest;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

    }

    @DisplayName("노선에 지하철역 등록 요청")
    @Test
    void addSection() {
        //given

        //when

        //then

    }

    @DisplayName("역 사이에 새로운 역 등록 요청")
    @Test
    void addBetweenStations() {
        //given

        //when

        //then

    }

    @DisplayName("새로운 역을 상행 종점으로 등록 요청")
    @Test
    void addEndUpStation() {
        //given

        //when

        //then

    }

    @DisplayName("새로운 역을 하행 종점으로 등록 요청")
    @Test
    void addEndDownStation() {
        //given

        //when

        //then

    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void addInvalidDistance() {
        //given

        //when

        //then

    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addInvalidContainsStations() {
        //given

        //when

        //then

    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addInvalidNotNotExistsStations() {
        //given

        //when

        //then

    }
}
