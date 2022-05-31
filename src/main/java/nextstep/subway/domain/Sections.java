package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.factory.SectionFactory;

@Embeddable
public class Sections {

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id", nullable = false)
    @OrderBy("section_order ASC")
    private List<Section> sectionList = new ArrayList<>();

    protected Sections() {

    }

    public List<Section> getSectionList() {
        return Collections.unmodifiableList(sectionList);
    }

    public void add(Section section) {
        sectionList.add(section);
        section.setSectionOrder(sectionList.size());
    }

    public void insertSection(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        if (upStation.getId().equals(downStation.getId())) {
            throw new CannotAddSectionException("구간 추가 불가능");
        }
        SectionOperation sectionOperation = getSectionOperation(upStation, downStation);
        sectionOperation.insert(newSection);
        this.setOrder();
    }

    private SectionOperation getSectionOperation(Station upStation, Station downStation) {
        Optional<Section> upMatch = matchUpStation(upStation);
        Optional<Section> downMatch = matchDownStation(downStation);
        verifyAlreadyExistSection(upMatch.isPresent(), downMatch.isPresent());

        if (upMatch.isPresent()) {
            return new SectionOperation(upMatch.get(), SectionMatchType.UP_STATION);
        }
        if (downMatch.isPresent()) {
            return new SectionOperation(downMatch.get(), SectionMatchType.DOWN_STATION);
        }
        if (isInsertHead(downStation.getId())) {
            return new SectionOperation(sectionList.get(0), SectionMatchType.INSERT_HEAD);
        }
        throw new CannotAddSectionException("대상 역을 찾을 수 없음");

    }

    private Optional<Section> matchUpStation(Station upStation) {
        return sectionList.stream().filter((section) -> section.getUpStation().getId().equals(upStation.getId()))
                .findFirst();
    }

    private Optional<Section> matchDownStation(Station downStation) {
        return sectionList.stream().filter((section) -> {
            if (section.getDownStation() == null) {
                return false;
            }
            return section.getDownStation().getId().equals(downStation.getId());
        }).findFirst();
    }

    private void verifyAlreadyExistSection(boolean isUpStationMatch, boolean isDownStationMatch) {
        if (isUpStationMatch && isDownStationMatch) {
            throw new CannotAddSectionException("이미 있는 구간입니다.");
        }
    }

    private boolean isInsertHead(Long downStationId) {
        return sectionList.get(0).getUpStation().getId() == downStationId;
    }

    private void setOrder() {
        for (int index = 0; index < sectionList.size(); index++) {
            sectionList.get(index).setSectionOrder(index + 1);
        }
    }

    private enum SectionMatchType {
        UP_STATION {
            @Override
            void insert(Section targetSection, Section newSection, List<Section> sectionList) {
                int targetIndex = sectionList.indexOf(targetSection);
                boolean isLastSection = targetIndex == sectionList.size() - 1;
                if (isLastSection) {
                    sectionList.set(targetIndex, newSection);
                    sectionList.add(SectionFactory.createSectionAtLastOfLine(newSection.getDownStation()));
                    return;
                }
                insertForward(targetSection, newSection, sectionList);
            }

            void insertForward(Section targetSection, Section newSection, List<Section> sectionList) {
                int targetIndex = sectionList.indexOf(targetSection);
                Long diffDistance = targetSection.getDistance() - newSection.getDistance();
                if (diffDistance <= 0) {
                    throw new CannotAddSectionException("새 구간의 길이는 기존 역사이의 거리보다 길수 없습니다.");
                }
                Section nextSection = sectionList.get(targetIndex + 1);
                sectionList.set(targetIndex, newSection);
                sectionList.add(targetIndex + 1, SectionFactory.createSectionAtMiddleOfLine(newSection.getDownStation(),
                        nextSection.getUpStation(), diffDistance));
            }
        }, DOWN_STATION {
            @Override
            void insert(Section targetSection, Section newSection, List<Section> sectionList) {
                int targetIndex = sectionList.indexOf(targetSection);
                Long diffDistance = targetSection.getDistance() - newSection.getDistance();
                if (diffDistance <= 0) {
                    throw new CannotAddSectionException("구간 추가 불가능");
                }
                sectionList.set(targetIndex, SectionFactory.createSectionAtMiddleOfLine(targetSection.getUpStation(),
                        newSection.getUpStation(), diffDistance));
                sectionList.add(targetIndex + 1, newSection);
            }
        }, INSERT_HEAD {
            @Override
            void insert(Section targetSection, Section newSection, List<Section> sectionList) {
                sectionList.add(0, newSection);
            }
        };

        void insert(Section targetSection, Section newSection, List<Section> sectionList) {
        }

        ;
    }

    private class SectionOperation {
        private Section targetSection;
        private SectionMatchType sectionMatchType;

        public SectionOperation(Section targetSection, SectionMatchType sectionMatchType) {
            this.targetSection = targetSection;
            this.sectionMatchType = sectionMatchType;
        }

        public void insert(Section newSection) {
            sectionMatchType.insert(targetSection, newSection, sectionList);
        }
    }
}
