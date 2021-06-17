package nextstep.subway.section.domain;

public enum SectionStatus {
    UP("up"),
    DOWN("down");

    private String status;

    SectionStatus(String status) {
        this.status = status;
    }
}
