package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Embeddable
public class Sections {
    public static String DUPLICATE_SECTION_ERROR = "중복된 지하철 노선을 등록할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void addSections(Section section) {
        validate(section);
        checkUpStation(section);
        checkDownStation(section);
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    private void validate(Section section) {
        if (sections.contains(section)) {
            throw new IllegalArgumentException(DUPLICATE_SECTION_ERROR);
        }
    }

    private void checkUpStation(Section section) {
        Station upStation = section.getUpStation();

        if (matchUpStation(upStation)) {
            Section existingUpStation = existingUpStation(upStation);
            existingUpStation.updateUpStation(section.getDownStation(), section.getDistance());
        }
    }

    private void checkDownStation(Section section) {
        Station downStation = section.getDownStation();

        if (matchDownStation(downStation)) {
            Section existingUpStation = existingDownStation(downStation);
            existingUpStation.updateDownStation(section.getUpStation());
        }
    }

    private boolean matchUpStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsUpStation(station));
    }

    private Section existingUpStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("상행역을 찾을 수 없습니다."));
    }

    private boolean matchDownStation(Station station) {
        return sections.stream().anyMatch(section -> section.isEqualsDownStation(station));
    }

    private Section existingDownStation(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualsUpStation(station))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("하행역을 찾을 수 없습니다."));
    }
}
