package nextstep.subway.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.ConflictException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class LineStations {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<LineStation> lineStations;

    protected LineStations() {
        lineStations = new ArrayList<>();
    }

    public static LineStations empty() {
        return new LineStations();
    }

    public void add(LineStation newLineStation) {
        validateCreationLineStation(newLineStation);

        lineStations.forEach(lineStation -> {
            updateUpStation(newLineStation, lineStation);
            updateDownStation(newLineStation, lineStation);
        });

        System.out.println("ASdsadasd");

        lineStations.add(newLineStation);
    }

    private void updateUpStation(LineStation newLineStation, LineStation lineStation) {
        if (lineStation.getUpStation().isSame(newLineStation.getUpStation())) {
            lineStation.updateUpStation(newLineStation);
        }
    }

    private void updateDownStation(LineStation newLineStation, LineStation lineStation) {
        if (lineStation.getDownStation().isSame(newLineStation.getDownStation())) {
            lineStation.downUpStation(newLineStation);
        }
    }

    private void validateCreationLineStation(LineStation newLineStation) {
        if (lineStations.size() > 0) {
            validateOverlapLineStation(newLineStation);
            validateNotContainStation(newLineStation);
        }
    }

    private void validateOverlapLineStation(LineStation newLineStation) {
        if (lineStations.contains(newLineStation)) {
            throw new ConflictException("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
        }
    }

    private void validateNotContainStation(LineStation newLineStation) {
        if (
                lineStations.stream().noneMatch(lineStation -> lineStation.getUpStation().isSame(newLineStation.getUpStation())) &&
                        lineStations.stream().noneMatch(lineStation -> lineStation.getDownStation().isSame(newLineStation.getDownStation()))
        ) {
            throw new BadRequestException("새로운 구간의 역이 상행역과 하행역 둘 중 하나에 포함되어야 합니다.");
        }
    }

    public List<LineStation> get() {
        return lineStations;
    }
}
