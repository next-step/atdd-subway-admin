package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.domain.Sections;
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
    public void saveSection(Line line, Station upStation, Station downStation, int distance) {
        Section persistSection = sectionRepository.save(Section.of(upStation, downStation, distance));
        persistSection.toLine(line);
    }

    @Transactional
    public void deleteSections(Sections sections) {
        sectionRepository.deleteAll(sections);
    }

    @Transactional
    public void deleteSection(Section section) {
        sectionRepository.delete(section);
    }
}
