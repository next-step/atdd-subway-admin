package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.application.SectionCommandService;
import nextstep.subway.section.application.SectionQueryService;
import nextstep.subway.section.domain.Section;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LineCommandService {

    private final LineQueryService lineQueryService;
    private final SectionCommandService sectionCommandService;
    private final SectionQueryService sectionQueryService;

    private final LineRepository lineRepository;

    public LineCommandService(LineQueryService lineQueryService,
                              SectionCommandService sectionCommandService,
                              SectionQueryService sectionQueryService,
                              LineRepository lineRepository) {
        this.lineQueryService = lineQueryService;
        this.sectionCommandService = sectionCommandService;
        this.sectionQueryService = sectionQueryService;
        this.lineRepository = lineRepository;
    }

    public Long save(Line nonPersistLine, Long upStationId, Long downStationId, int distance) {

        Line persistLine = lineRepository.save(nonPersistLine);
        Long sectionId = sectionCommandService.save(upStationId, downStationId, distance);
        persistLine.addSection(sectionQueryService.findById(sectionId));

        return persistLine.getId();
    }

    public void update(Long lineId, Line updateLine) {
        Line line = lineQueryService.findById(lineId);
        line.update(updateLine);

        lineRepository.save(line);
    }

    public void deleteById(Long lineId) {
        Line line = lineQueryService.findById(lineId);
        lineRepository.delete(line);
    }

    public void addSection(Long lineId, Long sectionId) {

        Line persistLine = lineQueryService.findById(lineId);
        Section section = sectionQueryService.findById(sectionId);

        persistLine.addSection(section);
        lineRepository.save(persistLine);
    }
}
