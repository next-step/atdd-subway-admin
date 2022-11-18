package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.exception.EntityNotFoundException;
import nextstep.subway.exception.InvalidParameterException;
import nextstep.subway.repository.LineRepository;
import nextstep.subway.repository.LineStationRepository;
import nextstep.subway.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SectionService {

    private LineRepository lineRepository;

    private StationRepository stationRepository;

    private LineStationRepository lineStationRepository;

    public SectionService(LineRepository lineRepository,
                          StationRepository stationRepository,
                          LineStationRepository lineStationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository;
    }

    @Transactional
    public LineResponse saveSection(SectionRequest sectionRequest, Long lineId) {
        checkValidationParameter(sectionRequest);

        Line line = getLine(lineId);

        LineStation lineStation = lineStationRepository.save(
                        new LineStation(getStation(sectionRequest.getUpStationId()),
                        getStation(sectionRequest.getDownStationId()),
                        sectionRequest.getDistance()));

        line.getLineStations().infixSection(lineStation);

        return LineResponse.of(line);
    }

    private void checkValidationParameter(SectionRequest sectionRequest) {
        if (sectionRequest.getUpStationId() == null || sectionRequest.getDownStationId() == null) {
            throw new InvalidParameterException("상행역과 하행역이 모두 등록되어 있어야 합니다.");
        }
    }

    private Line getLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Line", id));
    }

    private Station getStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station", id));
    }

}
