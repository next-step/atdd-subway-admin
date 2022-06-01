package nextstep.subway.application;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.request.SectionRequest;
import nextstep.subway.dto.response.SectionResponse;
import nextstep.subway.exception.DupSectionException;
import nextstep.subway.exception.LineNotFoundException;
import nextstep.subway.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;
    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;


    public SectionService(StationRepository stationRepository,
        LineStationRepository lineStationRepository, LineRepository lineRepository,
        SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void createSection(long lineId, SectionRequest sectionRequest) {
        Line line = getLineOrThrow(lineId);
        Station upStation = getStationOrThrow(sectionRequest.getUpStationId());
        Station downStation = getStationOrThrow(sectionRequest.getDownStationId());
        sectionRepository.findAllByLine(line);

        validateDupSection(line, upStation, downStation);
        Section appendSection = Section.of(upStation, downStation, line,
            new Distance(sectionRequest.getDistance()));

        line.addSection(appendSection);
    }

    private void validateDupSection(Line line, Station upStation, Station downStation) {
        if (sectionRepository.findAllByUpStationAndDownStationAndLine(upStation, downStation, line)
            .isPresent()) {
            throw new DupSectionException();
        }
    }

    private Station getStationOrThrow(long stationId) {
        return stationRepository.findById(stationId)
            .orElseThrow(StationNotFoundException::new);
    }

    private Line getLineOrThrow(long lineId) {
        return lineRepository.findById(lineId)
            .orElseThrow(LineNotFoundException::new);
    }


    public List<SectionResponse> findAllByLine(Long lineId) {
        Line line = lineRepository.findById(lineId).orElseThrow(LineNotFoundException::new);

        return sectionRepository.findAllByLine(line)
            .stream()
            .map(Section::toResponse)
            .collect(Collectors.toList());
    }
}
