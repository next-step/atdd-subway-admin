package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineModifyRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String MESSAGE_ILLEGAL_LINE_ID = "부적절한 노선 식별자 입니다";

    private final LineRepository lineRepository;

    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse createLine(LineRequest request) {
        Station upStationProxy = stationRepository.getById(request.getUpStationId());
        Station downStationProxy = stationRepository.getById(request.getDownStationId());
        Line line = request.toLine();
        line.setStations(upStationProxy, downStationProxy);
        Line save = lineRepository.save(line);
        return LineResponse.of(save);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long lineId) {
        Line line = findLineOrThrowException(lineId);
        return LineResponse.of(line);
    }

    public Line findLineOrThrowException(Long lineId) {
        return lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public void modifyLine(Long lineId, LineModifyRequest request) {
        Line line = findLineOrThrowException(lineId);
        line.modifyLine(request.getName(),request.getColor());
    }

    @Transactional
    public void deleteLine(Long lineId) {
        if(Objects.isNull(lineId) || lineId <= 0){
            throw new IllegalArgumentException(MESSAGE_ILLEGAL_LINE_ID);
        }
        lineRepository.delete(findLineOrThrowException(lineId));
    }
}
