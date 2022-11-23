package nextstep.subway.dto;

import java.util.List;

public class SectionsResponse {

    List<SectionResponse> sectionResponses;

    public SectionsResponse(List<SectionResponse> sectionResponses) {
        this.sectionResponses = sectionResponses;
    }

    public List<SectionResponse> getSectionResponses() {
        return sectionResponses;
    }
}
