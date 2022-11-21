package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 노선 구간 목록을 관리하는 일급 컬렉션
 */
@Embeddable
public class SectionLineUp {

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Section> sectionList = new ArrayList<>();

    protected SectionLineUp() {
    }

    public List<Section> getSectionList() {
        return sectionList;
    }

    public void init(Line line, Section section) {
        if (!sectionList.isEmpty()) {
            throw new IllegalStateException("새로 생성된 노선이 아닙니다");
        }
        sectionList.add(section);
        section.registerLine(line);
    }

    public void add(Line line, Section section) {
        if (!sectionList.isEmpty()) {
            validUnknownStation(section);
        }

        if (isExternalSection(line, section)) {
            createExternalSection(section, line);
        }

        if (isInternalSection(line, section)) {
            createInternalSection(section);
        }
    }

    /* 구간 종점 밖에 구간이 추가되는 경우 */
    private boolean isExternalSection(Line line, Section section) {
        return section.isSameUpStationId(line.getDownStationId()) ||
                section.isSameDownStationId(line.getUpStationId());
    }

    /* 구간 목록 내부에 구간이 추가되는 경우 */
    private boolean isInternalSection(Line line, Section section) {
        return section.isSameUpStationId(line.getUpStationId()) ||
                section.isSameDownStationId(line.getDownStationId());
    }

    private void validUnknownStation(Section section) {
        if (sectionList.stream().noneMatch(streamSection -> streamSection.isKnownSection(section))) {
            throw new IllegalArgumentException("상행역, 하행역이 노선에 포함되어 있지 않습니다. 상행역ID:" + section.getUpStationId() +
                    ", 하행역ID:" + section.getDownStationId());
        }
    }

    private void createExternalSection(Section section, Line line) {
        sectionList.add(section);
        section.registerLine(line);
    }

    private void createInternalSection(Section section) {
        validSameSection(section);
        List<Section> createUpdateSection = new ArrayList<>();

        /* 상행역과 연결 된 역을 추가할 경우 */
        sectionList.stream().filter(streamSection -> streamSection.isSameUpStation(section))
                .findFirst()
                .ifPresent(streamSection -> createUpdateSection.addAll(createUpSection(streamSection, section)));
        /* 하행역과 연결 된 역을 추가할 경우 */
        sectionList.stream().filter(streamSection -> streamSection.isSameDownStation(section))
                .findFirst()
                .ifPresent(streamSection -> createUpdateSection.addAll(createDownSection(streamSection, section)));

        /* 노선 목록을 lazy 하게 변경할 목적 */
        sectionList.addAll(createUpdateSection);
    }

    private List<Section> createUpSection(Section source, Section add) {
        sectionList.remove(source);
        source.updateDistance(source.minusDistance(add.getDistance()));
        source.updateUpStationId(add.getDownStationId());
        return Arrays.asList(source, add);
    }

    private List<Section> createDownSection(Section source, Section add) {
        sectionList.remove(source);
        source.updateDistance(source.minusDistance(add.getDistance()));
        source.updateDownStationId(add.getUpStationId());
        return Arrays.asList(source, add);
    }

    private void validSameSection(Section section) {
        if (sectionList.stream()
                .anyMatch(streamSection -> streamSection.isKnownStationId(section.getUpStationId())) &&
                sectionList.stream()
                        .anyMatch(streamSection -> streamSection.isKnownStationId(section.getDownStationId()))) {
            throw new IllegalArgumentException(
                    "이미 중복된 구간이 있습니다. 상행선id:" + section.getUpStationId()
                            + ", 하행선id:" + section.getDownStationId());
        }
    }
}
