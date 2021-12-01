package nextstep.subway.station;

import nextstep.subway.station.dto.StationRequest;

public class StationTestFixture {

    private StationTestFixture() {
        throw new UnsupportedOperationException();
    }

    public static StationRequest 강남역_요청_데이터() {
        return new StationRequest("강남역");
    }

    public static StationRequest 역삼역_요청_데이터() {
        return new StationRequest("역삼역");
    }

    public static StationRequest 교대역_요청_데이터() {
        return new StationRequest("교대역");
    }

    public static StationRequest 서초역_요청_데이터() {
        return new StationRequest("서초역");
    }
}
