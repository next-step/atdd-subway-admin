package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    private boolean isFindSameStation = false;

    public List<Section> getSections() {
        return sections;
    }

    public void addDefaultSection(Line line, int distance, Station upStation, Station downStation) {
        sections.add(new Section(line, distance, upStation, downStation));
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void addSection(int distance, Station upStation, Station downStation) {
        validateAlreadyExistsStation(upStation, downStation);
        validateNotExistsStation(upStation, downStation);

        addBetweenByUpStation(upStation, downStation, distance);
        prependUpStation(upStation, downStation, distance);
        addBetweenByDownStation(upStation, downStation, distance);
        appendDownStation(upStation, downStation, distance);
    }

    private Optional<Section> findSameUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst();
    }

    private Optional<Section> findSameDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsDownStation(station))
                .findFirst();
    }

    private void validateAlreadyExistsStation(Station upStation, Station downStation) {
        sections.forEach(section -> section.validateAlreadyExistsStation(upStation, downStation));
    }

    private void validateNotExistsStation(Station upStation, Station downStation) {
        sections.forEach(section -> section.validateNotExistsStation(upStation, downStation));
    }

    private void prependUpStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameUpStation(downStation).ifPresent(section -> {
            sections.add(section.createNewSection(distance, section.getSortNo() - 1, upStation, downStation));
            isFindSameStation = true;
        });
    }

    private void addBetweenByUpStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameUpStation(upStation).ifPresent(section -> {
            section.validateLength(distance);
            sections.add(section.createNewSection(distance, section.getSortNo(), upStation, downStation));
            sections.add(section.createNewDownSection(distance, section.getSortNo() + 1, downStation));
            removeSection(section);
            isFindSameStation = true;
        });
    }

    private void appendDownStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameDownStation(upStation).ifPresent(section -> {
            sections.add(section.createNewSection(distance, section.getSortNo() + 1, upStation, downStation));
            isFindSameStation = true;
        });
    }

    private void addBetweenByDownStation(Station upStation, Station downStation, int distance) {
        if (isFindSameStation) {
            return;
        }
        findSameDownStation(downStation).ifPresent(section -> {
            section.validateLength(distance);
            sections.add(section.createNewSection(distance, section.getSortNo() + 1, upStation, downStation));
            sections.add(section.createNewUpSection(distance, section.getSortNo(), upStation));
            removeSection(section);
            isFindSameStation = true;
        });
    }
}
