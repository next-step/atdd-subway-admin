package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void saveFirstSection(Line line, Station station) {
        Section persistSection = sectionRepository.save(Section.first(station));
        mapToLine(line, persistSection);
    }

    @Transactional
    public void saveSection(Line line, Station station, int distance) {
        Section persistSection = sectionRepository.save(new Section(station, distance));
        mapToLine(line, persistSection);
    }

    private void mapToLine(Line line, Section section) {
        section.toLine(line);
    }
}
