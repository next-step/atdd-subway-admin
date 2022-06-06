package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(Line line, Integer distance, Station ascendEndpoint, Station descendEndPoint) {
        checkSame(ascendEndpoint, descendEndPoint);
        checkDistance(distance);

        add(line, ascendEndpoint, 0, null, null);
        add(line, descendEndPoint, distance, ascendEndpoint, null);
    }

    public List<Section> getAllSorted() {
        List<Section> sorted = new ArrayList<>();
        Section section =
                this.sections.stream().filter(sectionIterator -> Objects.isNull(sectionIterator.getPrevious()))
                        .findFirst().orElseThrow(NoSuchElementException::new);
        while (section != null) {
            sorted.add(section);
            section = section.getNext();
        }
        return sorted;
    }

    public void addSection(Line line, Station station, Integer distance, Station previousStation, Station nextStation) {
        checkSame(previousStation, nextStation);
        checkDistance(distance);
        checkAlreadyAdded(station);

        add(line, station, distance, previousStation, nextStation);
    }

    private Section findByOrElseNull(Station station) {
        if (station == null) {
            return null;
        }

        return this.sections.stream().filter(section -> Objects.equals(station, section.getStation())).findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private void checkDistance(Integer distance) {
        if (distance < 1) {
            throw new IllegalArgumentException("거리값은 1 이상 되어야 합니다.");
        }
    }

    private void checkSame(Station previousStation, Station nextStation) {
        if (Objects.equals(previousStation, nextStation)) {
            throw new IllegalArgumentException("구간은 서로 같은 역을 포함할 수 없습니다.");
        }
    }

    private void checkAlreadyAdded(Station station) {
        if (this.sections.stream().anyMatch(section -> Objects.equals(section.getStation(), station))) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }

    private void calculateDistance(Section previousSection, Section nextSection, Integer distance) {
        if (previousSection == null || nextSection == null) {
            return;
        }

        int surplusDistance = previousSection.getDistance() + nextSection.getDistance();
        if (surplusDistance <= distance) {
            throw new IllegalArgumentException("distance 는 구간 내에 속할 수 있는 값이어야 합니다.");
        }
        int half = Math.floorDiv(surplusDistance, 2);

        previousSection.updateDistance(surplusDistance - half);
        nextSection.updateDistance(half);
    }

    private void add(Line line, Station station, Integer distance, Station previousStation, Station nextStation) {
        Section previousSection = findByOrElseNull(previousStation);
        Section nextSection = findByOrElseNull(nextStation);

        calculateDistance(previousSection, nextSection, distance);

        Section section = new Section(line, station, distance, previousSection, nextSection);
        this.sections.add(section);

        if (previousSection != null) {
            previousSection.updateNext(section);
        }

        if (nextSection != null) {
            nextSection.updatePrevious(section);
        }

        this.sections.add(section);
    }
}
