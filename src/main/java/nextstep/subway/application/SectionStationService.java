package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionStationService {

    private SectionRepository sectionRepository;
    private StationService stationService;

    public SectionStationService (SectionRepository sectionRepository, StationService stationService) {
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public void initLineSectionAndAddSections(Line persistLine, LineRequest lineRequest) {
        Station upStation = stationService.getStationById(lineRequest.getUpStationId());
        Station downStation = stationService.getStationById(lineRequest.getDownStationId());
        persistLine.addSection(sectionRepository.save(
                new Section(persistLine,null, upStation, 0)));
        persistLine.addSection(sectionRepository.save(
                new Section(persistLine, downStation, null, 0)));
        persistLine.addSection(sectionRepository.save(
                new Section(persistLine, upStation, downStation, lineRequest.getDistance())));
    }

    public void deleteSectionsByLine(Line line) {
        sectionRepository.deleteByLine(line);
    }
}
