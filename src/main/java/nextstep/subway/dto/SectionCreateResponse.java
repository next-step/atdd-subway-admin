package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import java.util.List;
import java.util.stream.Collectors;

public class SectionCreateResponse {

    private long lineId;
    private long lineUpStationId;
    private long lineDownStationId;

    private int lineDistance;
    private List<SectionResponse> sectionResponseList;

    private SectionCreateResponse(long lineId, long lineUpStationId, long lineDownStationId, int lineDistance,
            List<SectionResponse> sectionResponseList) {
        this.lineId = lineId;
        this.lineUpStationId = lineUpStationId;
        this.lineDownStationId = lineDownStationId;
        this.lineDistance = lineDistance;
        this.sectionResponseList = sectionResponseList;
    }

    public static SectionCreateResponse of(Line line, List<Section> sectionList) {
        return new SectionCreateResponse(line.getId(), line.getUpStationId(), line.getDownStationId(),
                line.getDistanceIntValue(),
                sectionList.stream()
                        .map(SectionResponse::from)
                        .collect(Collectors.toList()));
    }

    public long getLineId() {
        return lineId;
    }

    public long getLineUpStationId() {
        return lineUpStationId;
    }

    public long getLineDownStationId() {
        return lineDownStationId;
    }

    public int getLineDistance() {
        return lineDistance;
    }

    public List<SectionResponse> getSectionResponseList() {
        return sectionResponseList;
    }
}
