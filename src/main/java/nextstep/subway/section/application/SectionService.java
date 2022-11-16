package nextstep.subway.section.application;

import nextstep.subway.common.exception.ErrorMessageConstant;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.dto.SectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = findLineById(lineId);
        Station upStation = findStation(sectionRequest.getUpStationId());
        Station downStation = findStation(sectionRequest.getDownStationId());
        line.addSection(sectionRequest.toSection(upStation, downStation));
        lineRepository.save(line);
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteSection(Long lineId, Long stationId) {
        Line line = findLineById(lineId);
        Station station = findStation(stationId);
        line.deleteSection(station);
    }

    public Line findLineById(Long lineId) {
        return lineRepository.findById(lineId)
                .orElseThrow(() -> new RuntimeException(ErrorMessageConstant.NOT_EXISTS_LINE));
    }

    private Station findStation(Long sectionRequest) {
        return stationRepository.findById(sectionRequest)
                .orElseThrow(() -> new RuntimeException(ErrorMessageConstant.NOT_EXISTS_STATION));
    }
}
