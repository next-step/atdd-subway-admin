package nextstep.subway.domain;

import nextstep.subway.exception.SameSectionRegistrationException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Embeddable
public class Sections implements Iterable<Section> {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            this.sections.add(newSection);
            return;
        }

        duplicateRegistrationValidate(newSection);

        sections.forEach(section -> section.calculate(newSection));
        sections.add(newSection);
    }

    private void duplicateRegistrationValidate(Section newSection) {
        sections.forEach(
                section -> {
                    List<Station> upDownStations = section.upDownStationPair();
                    if (upDownStations.contains(newSection.getUpStation()) && upDownStations.contains(newSection.getDownStation())) {
                        throw new SameSectionRegistrationException("상행역과 하행역이 이미 등록되어있습니다.");
                    }
                }
        );
    }

    @Override
    public Iterator<Section> iterator() {
        return sections.iterator();
    }

}
