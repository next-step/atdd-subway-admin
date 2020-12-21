package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections;

    Sections() {
        this(new ArrayList<>());
    }

    Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public int size() {
        return sections.size();
    }

    public void initFirstSection(final Section section) {
        this.sections.add(section);
    }

    public List<Long> getStationIds() {
        return sections.stream()
                .flatMap(it -> it.getStationIds().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void addSection(final Section section) {
        if (sections.size() == 0) {
            throw new InvalidSectionsActionException("초기화되지 않은 Sections에 Section을 추가할 수 없습니다.");
        }
        List<Section> sameUpStationSections = sections.stream()
                .filter(it -> it.isSameUpStation(section))
                .collect(Collectors.toList());
        if (sameUpStationSections.size() > 1) {
            throw new InvalidSectionsActionException("수행할 수 없는 동작입니다.");
        }
        if (sameUpStationSections.size() == 1) {
            this.sections.add(section);
            sameUpStationSections.get(0).updateUpStation(section);
        }
    }

    boolean contains(final Section section) {
        return this.sections.contains(section);
    }

    public List<Section> findCandidateSections(final Section section) {
        return this.sections.stream()
                .filter(it -> it.isSameUpStation(section) || it.isSameDownStation(section))
                .collect(Collectors.toList());
    }
}
