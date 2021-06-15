package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private SectionRepository sectionRepository;
    private StationService stationService;

    public SectionService(SectionRepository sectionRepository,
                          StationService stationService){
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public Section saveSection(long upStationId, long downStationId, int distance, Line line){
        Station upStation = stationService.findById(upStationId);
        Station downStation = stationService.findById(downStationId);
        Section section = new Section(upStation, downStation, distance);
        section.setLine(line);
        return sectionRepository.save(section);
    }
}
