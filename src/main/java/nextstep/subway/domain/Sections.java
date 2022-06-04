package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    // TODO: add validations
    public void add(Section newSection) {
        validateDuplication(newSection);

        if (list.isEmpty()) {
            list.add(newSection);
            return;
        }

        updateWhenSameUpStation(newSection);

        updateWhenSameDownStation(newSection);

        list.add(newSection);
    }

    public List<Station> getStationsInOrder() {
        Optional<Section> nextSection = list.stream()
                .filter(it -> it.isFirstSection())
                .findFirst();

        List<Station> result = new ArrayList<>();
        while (nextSection.isPresent()) {
            Section currentSection = nextSection.get();
            result.add(currentSection.getDownStation());
            nextSection = list.stream()
                    .filter(it -> it.isNextSectionOf(currentSection))
                    .findFirst();
        }

        return result;
    }

    private void updateWhenSameDownStation(Section newSection) {
        list.stream()
                .filter(it -> it.hasSameDownStationAs(newSection))
                .findFirst()
                .ifPresent(it -> it.updateDownStationToUpStationOf(newSection));
    }

    private void updateWhenSameUpStation(Section newSection) {
        list.stream()
                .filter(it -> it.hasSameUpStationAs(newSection))
                .findFirst()
                .ifPresent(it -> it.updateUpStationToDownStationOf(newSection));
    }

    private void validateDuplication(Section newSection) {
        list.stream()
                .filter(section -> section.hasSameStations(newSection))
                .findAny()
                .ifPresent(it -> {
                    throw new IllegalArgumentException("구간 중복");
                });
    }
}
