package nextstep.subway.line;

public class LineAcceptanceTestResponse {

    public final Long 노선_식별자;
    public final Long 상행종점역_식별자;
    public final Long 하행종점역_식별자;

    public LineAcceptanceTestResponse(Long 노선_식별자, Long 상행종점역_식별자, Long 하행종점역_식별자) {
        this.노선_식별자 = 노선_식별자;
        this.상행종점역_식별자 = 상행종점역_식별자;
        this.하행종점역_식별자 = 하행종점역_식별자;
    }
}
