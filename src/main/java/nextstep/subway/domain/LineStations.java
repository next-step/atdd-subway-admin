package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.SectionRequest;

@Embeddable
public class LineStations {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineStation> lineStations = new ArrayList<>();

    private static final int NO_PARENT = -1;

    public LineStations() {
    }

    public LineStations(List<LineStation> lineStations) {
        this.lineStations = lineStations;
    }

    public List<LineStation> getLineStations() {
        return lineStations;
    }

    public LineStations addSection(SectionRequest sectionRequest, Line line) {
        LineStations returnLineStations = new LineStations(lineStations);
        List<LineStation> lineStationsInstance = returnLineStations.getLineStations();

        LineStation lineStation = new LineStation(sectionRequest.getDownStationId(), sectionRequest.getUpStationId(),
                sectionRequest.getDistance(), line);

        boolean initalSectionAdded = addInitalSection(sectionRequest, line, lineStationsInstance, lineStation);

        if (initalSectionAdded) {
            return returnLineStations;
        }

        returnLineStations = addBetweenStation(sectionRequest, line, lineStationsInstance);

        if (returnLineStations == null) {
            returnLineStations = addBothEnds(sectionRequest, line, lineStationsInstance);
        }

        if (returnLineStations == null) {
            throw new RuntimeException("상행역과 하행역 둘중 하나도 포함되어 있는 게 없습니다.");
        }
        return returnLineStations;
    }

    private boolean addInitalSection(SectionRequest sectionRequest, Line line, List<LineStation> lineStationsInstance,
                                     LineStation lineStation) {
        if (lineStationsInstance.size() < 1) {
            lineStationsInstance.add(new LineStation(sectionRequest.getUpStationId(), -1, 0, line));
            lineStationsInstance.add(lineStation);
            return true;
        }
        return false;
    }

    private LineStations addBothEnds(SectionRequest sectionRequest, Line line, List<LineStation> lineStationsInstance) {
        LineStation lineStation = new LineStation(sectionRequest.getDownStationId(), sectionRequest.getUpStationId(),
                sectionRequest.getDistance(), line);
        // 상행 역으로 추가
        LineStations lineStationsInstance1 = addFrontEnd(sectionRequest, line, lineStationsInstance,
                lineStation);
        if (lineStationsInstance1 != null) {
            return lineStationsInstance1;
        }
        // 하행 역으로 추가
        return addLastEnd(sectionRequest, lineStationsInstance, lineStation);
    }

    private LineStations addBetweenStation(SectionRequest sectionRequest, Line line, List<LineStation> lineStationsInstance)
            throws RuntimeException {

        LineStation lineStation = new LineStation(sectionRequest.getDownStationId(), sectionRequest.getUpStationId(),
                sectionRequest.getDistance(), line);
        boolean upMatch = isUpMatch(sectionRequest, lineStationsInstance);
        boolean downMatch = isDownMatch(sectionRequest, lineStationsInstance);

        //a-b, b-c 구간 또는 a-c 존재시 a-c 등록 불가
        if (upMatch && downMatch) {
            throw new RuntimeException("이미 존재하는 구간");
        }

        if (upMatch || downMatch) {
            LineStation matchSection = lineStationsInstance.stream().filter(it -> matchPreStation(it, sectionRequest))
                    .findFirst().get();

            matchSection.updateUpStationTo(sectionRequest.getDownStationId());
            matchSection.minusDistace(sectionRequest.getDistance());
            lineStationsInstance.add(lineStation);
            return new LineStations(lineStationsInstance);
        }
        return null;
    }

    private LineStations addFrontEnd(SectionRequest sectionRequest, Line line, List<LineStation> lineStationsInstance,
                                     LineStation lineStation) {
        if (lineStationsInstance.stream()
                .anyMatch(it -> it.getPreStationId() == NO_PARENT && matchDownStation(it, sectionRequest))) {

            lineStationsInstance.stream()
                    .filter(it -> it.getPreStationId() == NO_PARENT && matchDownStation(it, sectionRequest))
                    .findFirst().get().updateUpStationTo(sectionRequest.getUpStationId());

            lineStationsInstance.add(new LineStation(sectionRequest.getUpStationId(), -1, 0, line));
            lineStationsInstance.add(lineStation);
            return new LineStations(lineStationsInstance);
        }
        return null;
    }

    private LineStations addLastEnd(SectionRequest sectionRequest, List<LineStation> lineStationsInstance,
                                    LineStation lineStation) {
        if (lineStationsInstance.stream().anyMatch(it -> requestIsAfterStation(it, sectionRequest))) {
            lineStationsInstance.add(lineStation);
            return new LineStations(lineStationsInstance);
        }
        return null;
    }

    private boolean matchPreStation(LineStation lineStation, SectionRequest sectionRequest) {
        return lineStation.getPreStationId() == sectionRequest.getUpStationId();
    }

    private boolean matchDownStation(LineStation lineStation, SectionRequest sectionRequest) {
        return lineStation.getStationId() == sectionRequest.getDownStationId();
    }

    private boolean requestIsAfterStation(LineStation lineStation, SectionRequest sectionRequest) {
        return lineStation.getStationId() == sectionRequest.getUpStationId();
    }

    private boolean isDownMatch(SectionRequest sectionRequest, List<LineStation> lineStationsInstance) {
        return lineStationsInstance.stream().anyMatch(it -> it.getStationId() == sectionRequest.getDownStationId());
    }

    private boolean isUpMatch(SectionRequest sectionRequest, List<LineStation> lineStationsInstance) {
        return lineStationsInstance.stream().anyMatch(it -> it.getPreStationId() == sectionRequest.getUpStationId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineStations)) {
            return false;
        }
        LineStations that = (LineStations) o;
        return Objects.equals(lineStations, that.lineStations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineStations);
    }
}
