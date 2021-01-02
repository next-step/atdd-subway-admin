package nextstep.subway.section.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Embeddable
public class Sections {
    private static final int MINIMUN_STATION_NUMBER = 2;
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void updateSection(Section newSection) {
        updateValidation(newSection);
        updateWithUpStation(newSection);
        updateWithDownStation(newSection);
    }

    public void deleteSection(Station station) {
        deleteValidation(station);
        Section upSection = getSection(station);
        deleteStation(station, upSection);
        upSection.removeLine();
    }

    public void updateValidation(Section newSection) {
        if (hasSection(newSection)) {
            throw new RuntimeException("동일한 구간 추가 요청");
        }
        if (sections.stream().noneMatch(oldSection -> oldSection.hasStation(newSection))) {
            throw new RuntimeException("등록되어 있지 않은 구간 추가 요청");
        }
    }

    public boolean hasSection(Section newSection) {
        return sections.stream().anyMatch(oldSection -> oldSection.equals(newSection));
    }

    private void updateWithDownStation(Section newSection) {
        sections.stream().filter(oldSection -> oldSection.getStation() == newSection.getStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    Section section = new Section(oldSection.getUpStation(), newSection.getUpStation(),
                            oldSection.isFirstSection() ? 0 : subtractDistance(oldSection.getDistance(), newSection.getDistance()));
                    section.addLine(oldSection.getLine());
                    oldSection.removeLine();
                });
    }

    private void updateWithUpStation(Section newSection) {
        sections.stream().filter(oldSection -> oldSection.getUpStation() == newSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    Section section = new Section(newSection.getStation(), oldSection.getStation(),
                            subtractDistance(oldSection.getDistance(), newSection.getDistance()));
                    section.addLine(oldSection.getLine());
                    oldSection.removeLine();
                });
    }

    private void deleteStation(Station station, Section upSection) {
        sections.stream().filter(oldSection -> oldSection.getUpStation() == station)
                .findFirst()
                .ifPresent(oldSection -> {
                    Section section = new Section(upSection.getUpStation(), oldSection.getStation(),
                            addDistance(oldSection.getDistance(), upSection.getDistance()));
                    section.addLine(oldSection.getLine());
                    oldSection.removeLine();
                });
    }

    private Section getSection(Station station) {
        return sections.stream().filter(oldSection -> oldSection.getStation() == station)
                    .findFirst().orElseThrow(NoSuchElementException::new);
    }

    private void deleteValidation(Station station) {
        if(getSections().size() <= MINIMUN_STATION_NUMBER) {
            throw new RuntimeException("최소 구간에 등록된 역입니다.");
        }
        if(sections.stream().noneMatch(section -> section.getStation().equals(station))) {
            throw new RuntimeException("등록된 역이 아닙니다.");
        }
    }

    private int addDistance(int oldDistance1, int oldDistance2) {
        return oldDistance1 + oldDistance2;
    }

    private int subtractDistance(int oldDistance, int newDistance) {
        if(oldDistance <= newDistance) {
            throw new RuntimeException("구간 길이 오류");
        }
        return oldDistance - newDistance;
    }

    public Optional<Section> findSectionWithUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation() == station)
                .findFirst();
    }
}
