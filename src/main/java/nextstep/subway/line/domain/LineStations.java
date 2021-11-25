package nextstep.subway.line.domain;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class LineStations {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<LineStation> lineStations = new ArrayList<>();

    protected LineStations() {
    }

    public void add(LineStation lineStation) {
        Optional<LineStation> changeSection = lineStations.stream()
            .filter(it -> Objects.equals(it.getNextStationId(), lineStation.getNextStationId()))
            .findFirst();

        if (changeSection.isPresent()
            && changeSection.get().getDistance() <= lineStation.getDistance()
        ) {
            System.out.println("===");
            // 변경할께 있으면,
            // 길이 비교해서 미만이어야 통과 아니면 다 실패
            throw new InvalidParameterException("길이가 불편함");
        }
        System.out.println("===1");
        if (!changeSection.isPresent()) {
            addLastLineStation(lineStation);
        }
        System.out.println("===2");
        changeSection.ifPresent(value -> value.nextStationIdUpdate(lineStation.getStationId()));

        lineStations.add(lineStation);
    }

    private void addLastLineStation(LineStation lineStation) {
        Optional<LineStation> optionalLastSection = getLastLineStation();

        if (!optionalLastSection.isPresent()) {
            lineStations.add(LineStation.lastOf(lineStation));
            return;
        }

        LineStation lastLineStation = optionalLastSection.get();
        if (Objects.equals(lastLineStation.getStationId(), lineStation.getStationId())) {
            lastLineStation.stationIdUpdate(lineStation.getNextStationId());
        }
    }

    public List<LineStation> getStations() {
        List<LineStation> result = new ArrayList<>();
        Optional<LineStation> nextLineStation = getLastLineStation();

        while (nextLineStation.isPresent()) {
            LineStation lineStation = nextLineStation.get();
            result.add(lineStation);

            nextLineStation = lineStations.stream()
                .filter(it -> lineStation.getStationId().equals(it.getNextStationId()))
                .findFirst();
        }

        Collections.reverse(result);
        return result;
    }

    public void delete() {
        for (LineStation lineStation : lineStations) {
            lineStation.delete();
        }
    }

    private Optional<LineStation> getLastLineStation() {
        return lineStations.stream()
            .filter(it -> Objects.isNull(it.getNextStationId()))
            .findFirst();
    }
}
