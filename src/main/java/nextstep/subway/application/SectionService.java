package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.SectionRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    private SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository,
                          StationRepository stationRepository,
                          SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        sectionRequest.checkValidationParameter();

        Line line = getLine(lineId);
        Section section = sectionRepository.save(
                        new Section(
                                getStation(sectionRequest.getUpStationId()),
                                getStation(sectionRequest.getDownStationId()),
                                sectionRequest.getDistance()
                        ));

        line.infixSection(section);

        return LineResponse.of(line);
    }

    @Transactional(readOnly = true)
    public SectionResponse findById(Long id) {
        return SectionResponse.of(getSection(id));
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Line", id));
    }

    private Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station", id));
    }

    private Section getSection(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Section", id));
    }

}
