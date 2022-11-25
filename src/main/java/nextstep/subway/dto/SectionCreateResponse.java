package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionLineUp;

import java.util.List;
import java.util.stream.Collectors;

public class SectionCreateResponse {

    private long lineId;
    private String lineName;
    private String lineColor;
    private List<SectionResponse> sectionResponseList;

    private SectionCreateResponse(long lineId, String lineName, String lineColor,
            List<SectionResponse> sectionResponseList) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.sectionResponseList = sectionResponseList;
    }

    public static SectionCreateResponse of(Line line, List<Section> sectionList) {
        return new SectionCreateResponse(line.getId(), line.getNameString(), line.getColorString(),
                sectionList.stream()
                        .map(SectionResponse::from)
                        .collect(Collectors.toList()));
    }

    public long getLineId() {
        return lineId;
    }

    public String getLineName() {
        return lineName;
    }

    public String getLineColor() {
        return lineColor;
    }

    public List<SectionResponse> getSectionResponseList() {
        return sectionResponseList;
    }
}
