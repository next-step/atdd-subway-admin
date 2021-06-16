package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new LinkedList<>();

    //TODO (3단계) 구간 추가시 순서대로 넣기
    public void add(Section newSection) {
        boolean isAdded = false;
        isAdded = addNewUpStation(newSection);

        addEqualUpStation(newSection);
        addEqualDownStation(newSection);

        if (!isAdded) {
            this.sections.add(newSection);
        }
    }

    private boolean addNewUpStation(Section newSection) {
        if (sections.size() != 0 && getFirstStation().equals(newSection.getDownStation())) {
            sections.add(0, newSection);
            return true;
        }
        return false;
    }

    private void addEqualDownStation(Section newSection) {
        sections.stream()
                .filter(section -> section.getDownStation().equals(newSection.getDownStation()))
                .findFirst()
                .ifPresent(section -> section.changeDownStation(newSection.getUpStation()));
    }

    private void addEqualUpStation(Section newSection) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> {
                    Station tmpStation = section.getDownStation();
                    section.changeDownStation(newSection.getDownStation());
                    newSection.changeUpStation(section.getDownStation());
                    newSection.changeDownStation(tmpStation);
                });
        //this.sections.add(newSection);
    }

    private void addEqualUpStation_ref(Section newSection) {
        //상행역에 동일 역이 존재하는경우
        for (Section section : sections) {
            if (section.getUpStation().equals(newSection.getUpStation())) {
                if (newSection.getDistance() >= section.getDistance()) {
                    throw new IllegalArgumentException();
                } else {
                    Station tmpStation = section.getDownStation();
                    section.changeDownStation(newSection.getDownStation());
                    newSection.changeUpStation(section.getDownStation());
                    newSection.changeDownStation(tmpStation);
                }
            }
        }
        this.sections.add(newSection);
    }

    public List<Station> getSortedStations() {
        List<Station> stationList = new ArrayList<>();
        stationList.add(getFirstStation());
        for (Section section : sections) {
            stationList.add(section.getDownStation());
        }
        return stationList;
    }

    private Station getFirstStation() {
        return sections.get(0).getUpStation();
    }

    public void add_refactor(Section newSection) {
        제일처음_구간_추가(newSection);
        새로운_역을_상행_종점으로_등록할_경우(newSection);
        새로운_역을_하행_종점으로_등록할_경우(newSection);
        역사이에_새로운_역을_등록할_경우(newSection);
    }

    private void 역사이에_새로운_역을_등록할_경우(Section newSection) {
        sections.stream()
                .filter(section -> section.getUpStation().equals(newSection.getUpStation()))
                .findFirst()
                .ifPresent(section -> {
                    Station tmpStation = section.getDownStation();
                    section.changeDownStation(newSection.getDownStation());
                    newSection.changeUpStation(newSection.getDownStation());
                    newSection.changeDownStation(tmpStation);
                    this.sections.add(newSection);
                });
    }

    private void 새로운_역을_하행_종점으로_등록할_경우(Section newSection) {
        if (sections.get(sections.size() - 1).getDownStation().equals(newSection.getUpStation())) {
            this.sections.add(newSection);
        }
    }

    private void 새로운_역을_상행_종점으로_등록할_경우(Section newSection) {
        if (sections.get(0).getUpStation().equals(newSection.getDownStation())) {
            this.sections.add(0, newSection);
        }
    }

    private void 제일처음_구간_추가(Section newSection) {
        if (sections.size() == 0) {
            this.sections.add(newSection);
        }
    }
}
