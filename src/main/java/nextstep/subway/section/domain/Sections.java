package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {
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

    public void validation(Section newSection) {
        if (sections.stream().anyMatch(oldSection -> oldSection.equals(newSection))) {
            throw new RuntimeException("동일한 구간 추가 요청");
        }
        if (sections.stream().noneMatch(oldSection -> oldSection.hasStation(newSection))) {
            throw new RuntimeException("등록되어 있지 않은 구간 추가 요청");
        }
        if (sections.stream()
                .filter(oldSection -> oldSection.getUpStation() == newSection.getUpStation())
                .anyMatch(oldSection -> oldSection.getDistance() <= newSection.getDistance())) {
            throw new RuntimeException("구간 길이 오류");
        }
        if (sections.stream()
                .filter(oldSection -> oldSection.getStation() == newSection.getStation()
                        && !oldSection.isFirstSection())
                .anyMatch(oldSection -> oldSection.getDistance() <= newSection.getDistance())) {
            throw new RuntimeException("구간 길이 오류");
        }
    }

    public void updateSection(Section newSection) {
        sections.stream().filter(oldSection -> oldSection.getUpStation() == newSection.getUpStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    Section section = new Section(newSection.getStation(), oldSection.getStation(),
                            oldSection.getDistance() - newSection.getDistance());
                    section.addLine(oldSection.getLine());
                    oldSection.removeLine();
                });
        sections.stream().filter(oldSection -> oldSection.getStation() == newSection.getStation())
                .findFirst()
                .ifPresent(oldSection -> {
                    Section section = new Section(oldSection.getUpStation(), newSection.getUpStation(),
                            oldSection.isFirstSection() ? 0 : oldSection.getDistance() - newSection.getDistance());
                    section.addLine(oldSection.getLine());
                    oldSection.removeLine();
                });
    }

    public Optional<Section> findSectionWithUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.getUpStation() == station)
                .findFirst();
    }
}
