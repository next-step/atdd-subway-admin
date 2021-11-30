package nextstep.subway.section.application;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Distance;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository,
        SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public long saveSection(long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Section added = makeSection(sectionRequest);

        sectionRepository.save(added);
        line.updateSections(added);

        return added.getId();
    }

    private Line findLineById(long id) {
        return lineRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }

    private Section makeSection(SectionRequest request) {
        Station upStation = findStationById(request.getUpStationId());
        Station downStation = findStationById(request.getDownStationId());

        return new Section(upStation, downStation, new Distance(request.getDistance()));
    }

    private Station findStationById(long id) {
        return stationRepository.findById(id)
            .orElseThrow(NoSuchElementException::new);
    }
}
