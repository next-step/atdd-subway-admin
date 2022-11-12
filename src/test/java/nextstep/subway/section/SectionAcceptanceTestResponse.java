package nextstep.subway.section;

public class SectionAcceptanceTestResponse {

    final String 노선_이름;
    final Long 노선_식별자;
    final Long 상행종점역_식별자;
    final Long 하행종점역_식별자;

    public SectionAcceptanceTestResponse(String 노선_이름, Long 노선_식별자, Long 상행종점역_식별자, Long 하행종점역_식별자) {
        this.노선_이름 = 노선_이름;
        this.노선_식별자 = 노선_식별자;
        this.상행종점역_식별자 = 상행종점역_식별자;
        this.하행종점역_식별자 = 하행종점역_식별자;
    }
}
