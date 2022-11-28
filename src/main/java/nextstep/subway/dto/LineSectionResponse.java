package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import java.util.List;
import java.util.stream.Collectors;

public class LineSectionResponse {

    private long lineId;
    private String lineName;
    private String lineColor;
    private List<SectionResponse> sectionResponseList;

    private LineSectionResponse(long lineId, String lineName, String lineColor,
            List<SectionResponse> sectionResponseList) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.sectionResponseList = sectionResponseList;
    }

    public static LineSectionResponse of(Line line, List<Section> sectionList) {
        return new LineSectionResponse(line.getId(), line.getNameString(), line.getColorString(),
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
