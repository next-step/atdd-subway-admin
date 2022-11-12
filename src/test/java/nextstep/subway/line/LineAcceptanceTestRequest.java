package nextstep.subway.line;

public class LineAcceptanceTestRequest {

    public final int 거리;
    public final String 노선;
    public final String 상행종점역;
    public final String 하행종점역;

    public LineAcceptanceTestRequest(String 노선, String 상행종점역, String 하행종점역, int 거리) {
        this.노선 = 노선;
        this.상행종점역 = 상행종점역;
        this.하행종점역 = 하행종점역;
        this.거리 = 거리;
    }
}
