package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.security.DigestException;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {

    private static final String INVALID_DISTANCE_EXCEPTION = "유효하기 않은 거리로는 구간을 생성할 수 없습니다.";
    private static final String STATIONS_DO_NOT_EXIST_EXCEPTION = "상하행역 모두 존재하지 않아 구간을 생성할 수 없습니다.";
    private static final String STATIONS_ALREADY_EXIT_EXCEPTION = "상하행역 기존에 존재하므로 구간을 생성할 수 없습니다.";

    @OneToMany(mappedBy = "line")
    List<Section> sections = new ArrayList<>();

    public Sections addSection(Section newSection) {

        if (sections.isEmpty()) {
            setInitSection(newSection);
            return this;
        }

        validateNewSection(newSection);

        if (exitsUpStation(newSection)) {
            insertNewDownStation(newSection);
            return this;
        }

        if (exitsDownStation(newSection)) {
            insertNewUpStation(newSection);
            return this;
        }

        return this;
    }

    private void setInitSection(Section newSection) {
        sections.add(new Section(newSection.getLine(), null, newSection.getUpStation(), 0));
        sections.add(new Section(newSection.getLine(), newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance()));
        sections.add(new Section(newSection.getLine(), newSection.getDownStation(), null, 0));
    }

    private void insertNewDownStation(Section newSection) {
        Section section = findSectionWithUpStation(newSection.getUpStation());
        int newDistance = calculateDistance(section, newSection);
        Station preDownStation = section.getDownStation();
        section = section.switchDownStation(newSection);
        section.updateDistance(newSection.getDistance());
        addGeneratedSection(newSection.getLine(), newSection.getDownStation(), preDownStation, newDistance);
    }

    private void insertNewUpStation(Section newSection) {
        Section section = findSectionWithDownStation(newSection.getDownStation());
        int newDistance = calculateDistance(section, newSection);
        Station preUpStation = section.getUpStation();
        section = section.switchUpStation(newSection);
        section.updateDistance(newSection.getDistance());
        addGeneratedSection(newSection.getLine(), preUpStation, newSection.getUpStation(), newDistance);
    }

    private int calculateDistance(Section preSection, Section newSection) {

        if (preSection.getUpStation() == null || preSection.getDownStation() == null) {
            return 0;
        }

        if (preSection.getDistance() - newSection.getDistance() <= 0) {
            throw new IllegalArgumentException(INVALID_DISTANCE_EXCEPTION);
        }

        return preSection.getDistance() - newSection.getDistance();
    }

    private boolean exitsUpStation(Section section) {
        return findSectionWithUpStation(section.getUpStation()) != null;
    }

    private Section findSectionWithUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasUpStation(station))
                .findFirst()
                .orElse(null);
    }

    private boolean exitsDownStation(Section section) {
        return findSectionWithDownStation(section.getDownStation()) != null;
    }

    private Section findSectionWithDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.hasDownStation(station))
                .findFirst()
                .orElse(null);
    }

    private void validateNewSection(Section newSection) {
        if (exitsUpStation(newSection) && exitsDownStation(newSection)) {
            throw new IllegalArgumentException(STATIONS_ALREADY_EXIT_EXCEPTION);
        }

        if (!exitsUpStation(newSection) && !exitsDownStation(newSection)) {
            throw new IllegalArgumentException(STATIONS_DO_NOT_EXIST_EXCEPTION);
        }
    }

    private void addGeneratedSection(Line line, Station upStation, Station downStation, int distance) {
        sections.add(new Section(line, upStation, downStation, distance));
    }

    public List<Section> getSectionList() {
        return sections;
    }
}
