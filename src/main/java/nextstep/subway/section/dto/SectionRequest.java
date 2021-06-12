package nextstep.subway.section.dto;

import nextstep.subway.section.domain.Section;

public class SectionRequest {
    private int distance;

    public SectionRequest() {}

    public SectionRequest(int distance) {
        this.distance = distance;
    }
    public int getDistance() {
        return distance;
    }

    public Section toSection() {
        return new Section(distance);
    }
}
