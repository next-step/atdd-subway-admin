package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import nextstep.subway.exception.CannotRegisterException;
import nextstep.subway.exception.ExceptionType;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> items = new ArrayList<>();

    public void add(Section newSection) {
        if (items.isEmpty()) {
            items.add(newSection);
            return;
        }

        validateSections(newSection);
        if (isEndPointSection(newSection)) {
            items.add(newSection);
            return;
        }

        registerSection(newSection);
    }

    private boolean isEndPointSection(Section newSection) {
        return this.items.stream()
            .noneMatch(item -> item.isStationConnectable(newSection));
    }

    private void registerSection(Section newSection) {
        Section connectedSection = getConnectedSection(newSection);
        Section createdSection = connectedSection.addSection(newSection);
        items.add(createdSection);
    }

    private Section getConnectedSection(Section newSection) {
        return this.items.stream()
            .filter(x -> x.isStationConnectable(newSection))
            .findAny()
            .orElseThrow(RuntimeException::new);
    }

    private void validateSections(Section section) {
        boolean upStationContains = isContainsStation(section.getUpStation());
        boolean downStationContains = isContainsStation(section.getDownStation());

        if (upStationContains && downStationContains) {
            throw new CannotRegisterException(ExceptionType.IS_EXIST_BOTH_STATIONS);
        }

        if (!upStationContains && !downStationContains) {
            throw new CannotRegisterException(ExceptionType.IS_NOT_EXIST_BOTH_STATIONS);
        }
    }

    private boolean isContainsStation(Station station) {
        return this.items.stream()
            .anyMatch(item -> item.isContains(station));
    }

    public List<Section> getItems() {
        return items;
    }
}
