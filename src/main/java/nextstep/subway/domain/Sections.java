package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.*;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
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

    public List<Section> getActualSectionList() {
        return sectionList.stream()
                .filter((section -> section.getDownStation() != null))
                .collect(toList());
    }

    public void add(Section section) {
        sectionList.add(section);
        section.setSectionOrder(sectionList.size());
    }

    public void insertSection(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        if (upStation.getId().equals(downStation.getId())) {
            throw new CannotAddSectionException("지하철구간의 시작역과 종착역이 동일할 수 없습니다.");
        }
        InsertSectionOperation sectionOperation = getInsertSectionOperation(upStation, downStation);
        sectionOperation.insert(newSection);
        this.setOrder();
    }

    private InsertSectionOperation getInsertSectionOperation(Station upStation, Station downStation) {
        Optional<Section> upMatch = matchUpStation(upStation);
        Optional<Section> downMatch = matchDownStation(downStation);
        verifyAlreadyExistSection(upMatch.isPresent(), downMatch.isPresent());

        if (upMatch.isPresent()) {
            return new UpStationMatchSectionOperation(upMatch.get());
        }
        if (downMatch.isPresent()) {
            return new DownStationMatchSectionOperation(downMatch.get());
        }
        if (isInsertHead(downStation.getId())) {
            return new InsertHeadSectionOperation(sectionList.get(0));
        }
        throw new CannotAddSectionException("구간 등록 기준점을 찾을 수 없음");
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
            throw new CannotAddSectionException("이미 존재하는 구간입니다.");
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

    public void deleteSection(Station deleteStation) {
        Optional<Section> upMatch = matchUpStation(deleteStation);
        if(!upMatch.isPresent()){
            throw new CannotDeleteSectionException("삭제하고자하는 역이 존재하지 않거나 노선에 포함되어 있지 않습니다.");
        }
        DeleteSectionOperation sectionOperation = new UpStationMatchSectionOperation(upMatch.get());
        sectionOperation.delete();
        this.setOrder();
    }


    private interface InsertSectionOperation{
        void insert(Section newSection);

        default void verifyDiffDistance(Long diffDistance){
            if (diffDistance <= 0) {
                throw new CannotAddSectionException("새 구간의 길이는 기존 역사이의 거리보다 길수 없습니다.");
            }
        }
    }

    private interface DeleteSectionOperation{
        void delete();
    }

    private class SectionOperation {
        protected Section targetSection;

        protected int targetSectionIndex;

        public SectionOperation(Section targetSection) {
            this.targetSection = targetSection;
            this.targetSectionIndex = sectionList.indexOf(targetSection);
        }

        public boolean isLastSection(){
            return targetSectionIndex == sectionList.size() - 1;
        }

        public boolean isFirstSection(){
            return targetSectionIndex == 0;
        }
    }

    private class UpStationMatchSectionOperation extends SectionOperation implements InsertSectionOperation, DeleteSectionOperation{
        public UpStationMatchSectionOperation(Section targetSection) {
            super(targetSection);
        }

        @Override
        public void insert(Section newSection) {
            if (isLastSection()) {
                sectionList.set(targetSectionIndex, newSection);
                sectionList.add(SectionFactory.createSectionAtLastOfLine(newSection.getDownStation()));
                return;
            }
            insertForward(newSection);
        }

        void insertForward(Section newSection) {
            Long diffDistance = targetSection.getDistance() - newSection.getDistance();
            verifyDiffDistance(diffDistance);

            int nextSectionIndex = targetSectionIndex + 1;
            Section nextSection = sectionList.get(nextSectionIndex);
            sectionList.set(targetSectionIndex, newSection);
            sectionList.add(nextSectionIndex, SectionFactory.createSectionAtMiddleOfLine(newSection.getDownStation(),
                    nextSection.getUpStation(), diffDistance));
        }

        @Override
        public void delete() {
            if(isFirstSection()){
                sectionList.remove(targetSection);
                return;
            }
            if(isLastSection()){
                deleteLastSection();
                return;
            }
            deleteMiddleSection();
            return;
        }

        private void deleteLastSection() {
            Section prevSection = sectionList.get(targetSectionIndex-1);
            sectionList.remove(targetSection);
            sectionList.remove(prevSection);
            sectionList.add(SectionFactory.createSectionAtLastOfLine(prevSection.getUpStation()));
        }

        private void deleteMiddleSection(){
            int prevSectionIndex = targetSectionIndex - 1;
            Section prevSection = sectionList.get(prevSectionIndex);

            Long mergeDistance = prevSection.getDistance() + targetSection.getDistance();
            sectionList.set(prevSectionIndex,SectionFactory.createSectionAtMiddleOfLine(
                    prevSection.getUpStation(),
                    targetSection.getDownStation(),
                    mergeDistance));
            sectionList.remove(targetSection);
        }
    }

    private class DownStationMatchSectionOperation extends SectionOperation implements InsertSectionOperation{
        public DownStationMatchSectionOperation(Section targetSection) {
            super(targetSection);
        }

        @Override
        public void insert(Section newSection) {
            Long diffDistance = targetSection.getDistance() - newSection.getDistance();
            verifyDiffDistance(diffDistance);

            int nextSectionIndex = targetSectionIndex + 1;
            sectionList.set(targetSectionIndex, SectionFactory.createSectionAtMiddleOfLine(targetSection.getUpStation(),
                    newSection.getUpStation(), diffDistance));
            sectionList.add(nextSectionIndex, newSection);
        }
    }

    private class InsertHeadSectionOperation extends SectionOperation implements InsertSectionOperation{
        public InsertHeadSectionOperation(Section targetSection) {
            super(targetSection);
        }

        @Override
        public void insert(Section newSection) {
            sectionList.add(0, newSection);
        }
    }
}
