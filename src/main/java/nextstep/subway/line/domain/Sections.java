package nextstep.subway.line.domain;

import nextstep.subway.exception.SectionDuplicateException;
import nextstep.subway.exception.StationNotExistException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    private static final String STATION_NOT_EXIST_MESSAGE = "상행역과 하행역 둘 중 하나도 포함되어있지 않습니다.";
    private static final String SECTION_DUPLICATE_MESSAGE = "상행역과 하행역이 노선에 모두 등록되어 있습니다.";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections;
    }

    public void add(Section section) {
        validate(section);

        sections.stream()
                .filter(it -> it.getUpStation() == section.getUpStation())
                .findFirst()
                .ifPresent(it -> it.updateUpStationTo(section.getDownStation(), section.getDistance()));

        sections.stream()
                .filter(it -> it.getDownStation() == section.getDownStation())
                .findFirst()
                .ifPresent(it -> it.updateDownStationTo(section.getUpStation(), section.getDistance()));

        sections.add(section);
    }

    private void validate(Section section) {
        if (!sections.isEmpty()) {
            validateDuplicate(section);
            validateExist(section);
        }
    }

    private void validateExist(Section section) {
        if (isNoneMatch(section)) {
            throw new StationNotExistException(STATION_NOT_EXIST_MESSAGE);
        }
    }

    private void validateDuplicate(Section section) {
        if (isAllMatch(section)) {
            throw new SectionDuplicateException(SECTION_DUPLICATE_MESSAGE);
        }
    }

    private boolean isNoneMatch(Section section) {
        return sections.stream()
                .noneMatch(it -> it.getUpStation() == section.getUpStation() || it.getUpStation() == section.getDownStation()
                        || it.getDownStation() == section.getUpStation() || it.getDownStation() == section.getDownStation()
                );
    }

    private boolean isAllMatch(Section section) {
        return sections.stream()
                .allMatch(it -> it.isDuplicate(section));
    }

    public void delete(Station station) {
        Section section = sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst()
                .orElseThrow(RuntimeException::new);

        sections.stream()
                .filter(it -> it.getDownStation() == station)
                .findFirst()
                .ifPresent(it -> it.mergeStation(section));

        sections.remove(section);
    }
}
