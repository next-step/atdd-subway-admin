package nextstep.subway.section;

public class SectionAcceptanceTestRequest {

    final Long 노선_식별자;
    final Long 상행역_식별자;
    final Long 하행역_식별자;
    final int 거리;

    public SectionAcceptanceTestRequest(Long 노선_식별자, Long 상행역_식별자, Long 하행역_식별자, int 거리) {
        this.노선_식별자 = 노선_식별자;
        this.상행역_식별자 = 상행역_식별자;
        this.하행역_식별자 = 하행역_식별자;
        this.거리 = 거리;
    }
}
