package nextstep.subway.line.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;
import org.apache.commons.lang3.ObjectUtils;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Sections {
    private static final int HEAD = 0;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    @OrderColumn
    private List<Section> sections = new LinkedList<>();

    public Sections(Section upSection, Section downSection) {
        sections.add(upSection);
        sections.add(downSection);
    }

    private void addSection(int idx,Section section) {
        checkArgument(ObjectUtils.isNotEmpty(section), "section should not be null");
        sections.add(idx,section);
    }

    public void addSection(Section upSection, Section downSection) {
        verifyNewSection(upSection, downSection);

        // 사이에 있는경우 기존 distance보다 넓지 않은지 체크
        // 하행선 끝일 경우 distance를 세팅하고 추가
        // 상행선 끝일 경우 distance를 세팅하고 추가
        if (sections.contains(upSection)) {
            int upIdx = sections.indexOf(upSection);
            addNextSection(upIdx+1,upSection,downSection);
        } else if( sections.contains(downSection)) {
            int downIdx = sections.indexOf(downSection);
            addPrevSection(downIdx,upSection,downSection);
        }

    }

    private void verifyNewSection(Section upSection, Section downSection) {
        checkArgument(ObjectUtils.isNotEmpty(upSection), "section should not be null");
        checkArgument(ObjectUtils.isNotEmpty(downSection), "section should not be null");
        // distance정보가 없다면
        if( upSection.getDistance() == 0L) {
            throw new IllegalArgumentException("distance should be not 0 {}"
                    .replaceFirst("\\{}", ""+ upSection.getDistance()));
        }

        // 모두 등록 되어있다면
        if( sections.contains(upSection) && sections.contains(downSection)) {
            throw new IllegalArgumentException("a section should be in line {} {}"
                    .replaceFirst("\\{}", ""+ upSection.getStation().getId())
                    .replaceFirst("\\{}", ""+ downSection.getStation().getId()));
        }
        // 모두 등록 되어있지 않다면
        if( !sections.contains(upSection) && !sections.contains(downSection)) {
            throw new IllegalArgumentException("a section should be in line {} {}"
                    .replaceFirst("\\{}", ""+ upSection.getStation().getId())
                    .replaceFirst("\\{}", ""+ downSection.getStation().getId()));
        }
    }

    private void addPrevSection(int insertIdx, Section upSection, Section downSection) {
        if( insertIdx == HEAD) {
            addSection(HEAD,upSection);
            return;
        }
        Section existSection = sections.get(insertIdx-1);
        existSection.verifyDistance(upSection);
        Long prevDistance = existSection.getDistance() - upSection.getDistance();
        existSection.setDistance(prevDistance);
        addSection(insertIdx,upSection);
    }

    private void addNextSection(int insertIdx, Section upSection, Section downSection) {
        // tail 에 추가
        Section existSection = sections.get(insertIdx-1);
        if( insertIdx < sections.size()) {
            existSection.verifyDistance(upSection);
        }
        Long nextDistance = existSection.getDistance() - upSection.getDistance();
        existSection.setDistance(upSection.getDistance());
        downSection.setDistance(nextDistance);
        addSection(insertIdx,downSection);
    }

    public List<Station> getStations() {
        return sections.stream().map(section->section.getStation()).collect(Collectors.toList());
    }
}
