package nextstep.subway.domain;

import org.junit.jupiter.api.DisplayName;

@DisplayName("Line 도메인 객체에 대한 테스트")
class LineTest {
    public static final Line 칠호선 = new Line("7호선", "green", StationTest.수락산역, StationTest.노원역, 10L);

}
