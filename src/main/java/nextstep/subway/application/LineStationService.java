package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.dto.LineStationResponse;
import nextstep.subway.exception.BadRequestException;
import nextstep.subway.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LineStationService {
    private final LineStationRepository lineStationRepository;
    private final LineService lineService;
    private final StationService stationService;

    public LineStationService(LineStationRepository lineStationRepository, LineService lineStationService, StationService stationService) {
        this.lineStationRepository = lineStationRepository;
        this.lineService = lineStationService;
        this.stationService = stationService;
    }

    public LineStationResponse createLineStation(LineStationRequest lineStationRequest) {
        Line line = lineService.findLineById(lineStationRequest.getLineId());
        List<LineStation> lineStations = line.getLineStations();

        Station upStation = stationService.findStationById(lineStationRequest.getUpStationId());
        Station downStation = stationService.findStationById(lineStationRequest.getDownStationId());
        LineStation newLineStation = LineStation.of(line, upStation, downStation, lineStationRequest.getDistance());

        validateCreationLineStation(lineStations, newLineStation);

        return new LineStationResponse();
    }

    private LineStation saveLineStation(LineStation lineStation) {
        return lineStationRepository.save(lineStation);
    }

    // TODO: 벨리데이션 처리 service -> domain
    private void validateCreationLineStation(List<LineStation> lineStations, LineStation newLineStation) {
        lineStations.forEach(lineStation -> {
            validateOverlapLineStation(lineStation, newLineStation);
            validateNewLineStation(lineStation, newLineStation);
        });
    }

    private void validateOverlapLineStation(LineStation lineStation, LineStation newLineStation) {
        if (lineStation.isSame(newLineStation)) {
            throw new ConflictException("상행역과 하행역이 이미 노선에 모두 등록되어있습니다.");
        }
    }

    private void validateNewLineStation(LineStation lineStation, LineStation newLineStation) {
        if (
                newLineStation.getUpStation().isSame(lineStation.getUpStation(), newLineStation.getUpStation()) ||
                        newLineStation.getUpStation().isSame(lineStation.getDownStation(), newLineStation.getDownStation())
        ) {
            validateLineStationDistance(lineStation, newLineStation);
        }
        throw new BadRequestException("새로운 구간의 역이 상행역과 하행역 둘 중 하나에 포함되어야 합니다.");
    }

    private void validateLineStationDistance(LineStation lineStation, LineStation newLineStation) {
        if (lineStation.isSmallerThanNewLineDistance(newLineStation)) {
            throw new ConflictException("새로운 구간의 길이가 기존 구간의 길이보다 작아야합니다.");
        }
    }
}
