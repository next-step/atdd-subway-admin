package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Distance;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.request.SectionRequest;
import nextstep.subway.dto.response.SectionResponse;
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

        Section appendSection = Section.of(upStation, downStation, line,
            new Distance(sectionRequest.getDistance()));
        Section targetSection = null;
        Station addStation = null;
        if (lineStationRepository.findAllByStationAndLine(upStation, line).isPresent()) {
            targetSection = sectionRepository.findAllByUpStationAndLine(upStation, line)
                .orElse(null);
            addStation = downStation;
        } else if (lineStationRepository.findAllByStationAndLine(downStation, line).isPresent()) {
            targetSection = sectionRepository.findAllByDownStationAndLine(downStation, line)
                .orElse(null);
            addStation = upStation;
        }
        /* 성복 - 수지구청 - 미금 - 정자*/
        /* 수지 동*/
        lineStationRepository.save(new LineStation(line, addStation));
        line.addSection(appendSection, targetSection);
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
        Line line = lineRepository.findById(lineId)
            .orElseThrow(LineNotFoundException::new);

        return sectionRepository.findAllByLine(line)
            .stream()
            .map(section -> SectionResponse.from(section))
            .collect(Collectors.toList());
    }
}
