package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.response.SectionResponse;
import nextstep.subway.exception.LineNotFoundException;
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


    public List<SectionResponse> findAllByLine(Long lineId) {
        Line line = lineRepository.findById(lineId)
            .orElseThrow(LineNotFoundException::new);

        return sectionRepository.findAllByLine(line)
            .stream()
            .map(section -> SectionResponse.from(section))
            .collect(Collectors.toList());
    }
}
