package nextstep.subway.section.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section newSection) {
        try {
            validateSection(newSection);
            sections.add(newSection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeSection(Long stationId) {
        try {
            validateRemoveSection();
            List<Section> sections = findSectionByStationId(stationId);
            if (sections.size() == 1) { //종점인 경우
                this.sections.remove(sections.get(0));
            }
            if (sections.size() == 2) { //중간역인 경우
                removeMiddleSection(sections, stationId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeMiddleSection(List<Section> sections, Long stationId) {
        Section upSection = sections.stream().filter(section -> section.getDownStationId().equals(stationId)).findFirst().get();
        Section downSection = sections.stream().filter(section -> section.getUpStationId().equals(stationId)).findFirst().get();
        Section newSection = new Section(upSection.getLine(), upSection.getUpStationId(), downSection.getDownStationId(),
                upSection.getDistance() + downSection.getDistance());

        this.sections.remove(upSection);
        this.sections.remove(downSection);
        addSection(newSection);
    }

    public List<Section> findSectionByStationId(Long stationId) throws Exception {
        List<Section> findSections = this.sections.stream()
                .filter(section -> section.hasStation(stationId))
                .collect(Collectors.toList());
        if (findSections.size() != 0) {
            return findSections;
        }
        throw new Exception("해당 역이 존재하는 구간을 찾을 수 없습니다.");
    }

    public void validateRemoveSection() throws Exception {
        if (sections.size() <= 1) {
            throw new Exception("구간이 하나인 노선에서는 구간을 삭제할 수 없습니다.");
        }
    }

    public void validateSection(Section newSection) throws Exception {
        Optional<Section> validatedSection = sections.stream()
                .filter(section -> section.validateAddSection(newSection)).findAny();
        if (!validatedSection.isPresent()) {
            throw new Exception("구간을 등록할 수 없습니다.");
        }
    }
}
