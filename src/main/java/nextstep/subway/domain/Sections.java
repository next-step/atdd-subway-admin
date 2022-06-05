package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> list = new ArrayList<>();

    // TODO: add validations
    public void add(Section newSection) {

        validate(newSection);

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

    private void validateSectionIsNull(Section newSection) {
        if (Objects.isNull(newSection)) {
            throw new IllegalArgumentException("추가할 구간이 null");
        }
    }

    private void checkDuplication(Section newSection) {
        list.stream()
                .filter(section -> section.hasSameStations(newSection))
                .findAny()
                .ifPresent(it -> {
                    throw new IllegalArgumentException("구간 중복");
                });
    }

    private void validateDistance(Section newSection) {
        list.stream()
                .filter(section -> section.hasSameUpStationAs(newSection) ||
                        section.hasSameDownStationAs(newSection))
                .findFirst()
                .ifPresent(foundSection -> {
                    if (foundSection.isFirstSection()) {
                        return;
                    }

                    if (!foundSection.canInsert(newSection)) {
                        throw new IllegalArgumentException("길이 오류");
                    }
                });
    }

    private void validateStations(Section newSection) {
        list.stream()
                .filter(section -> section.equalsAtLeastOneStation(newSection))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("추가 불가 - 일치 역 없음"));
    }

    private void validate(Section newSection) {
        validateSectionIsNull(newSection);

        if (list.isEmpty()) {
            return;
        }

        checkDuplication(newSection);
        validateDistance(newSection);
        validateStations(newSection);
    }
}
