package nextstep.subway.line.application;

import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.line.domain.Line.Line;
import nextstep.subway.line.domain.Line.LineRepository;
import nextstep.subway.line.domain.LineStation.LineStation;
import nextstep.subway.line.domain.LineStation.LineStationRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineStationRequest;
import nextstep.subway.line.exception.LineNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static nextstep.subway.common.utils.ValidationUtils.isNull;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final LineStationRepository lineStationRepository;

    public LineService(LineRepository lineRepository, LineStationRepository lineStationRepository,
                       StationRepository stationRepository, LineStationRepository lineStationRepository1) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineStationRepository = lineStationRepository1;
    }

    public Line findLine(Long id) {
        Optional<Line> findLine = findLineById(id);
        return findLine.orElse(null);
    }

    public List<Line> findLine() {
        List<Line> lines = findLineAll();
        return lines;
    }

    @Transactional
    public LineResponse saveLineAndTerminalStation(LineRequest request) {
        Line persistLine = saveLine(request.toLine());
        saveTerminalStation(persistLine, request.getPreStationId(), request.getNextStationId(), request.getDistance());
        return LineResponse.of(persistLine);
    }

    private Line saveLine(Line line) {
        return lineRepository.save(line);
    }

    private void saveTerminalStation(Line line, Long preStationId, Long nextStationId, int distance) {
        Station preStation = findOneStation(preStationId).orElse(null);
        Station nextStation = findOneStation(nextStationId).orElse(null);
        line.addLineStation(new LineStation(line, preStation, nextStation, distance));
    }

    private Optional<Station> findOneStation(Long stationId) {
        if (isNull(stationId)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "검색할 역 아이디 입력은 필수입니다.");
        }
        return stationRepository.findById(stationId);
    }

    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findLineById(id).orElseThrow(() ->
                        new LineNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "노선이 존재하지 않습니다."))
                .update(lineRequest.toLine());

        lineRepository.save(line);
    }

    public void deleteLine(Long id) {
        Line line = findLineById(id).orElseThrow(() ->
                new LineNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "노선이 존재하지 않습니다."));

        lineRepository.delete(line);
    }

    @Transactional
    public void addLineStation(Long id, LineStationRequest lineStationRequest) {
        Line persistLine = findLineById(id).orElse(null);

        saveTerminalStation(persistLine, lineStationRequest.getPreStationId(),
                lineStationRequest.getNextStationId(), lineStationRequest.getDistance());
    }

    private Optional<Line> findLineById(Long id) {
        if (isNull(id)) {
            throw new LineNotFoundException(ErrorCode.NOT_FOUND_ARGUMENT, "검색할 노선 아이디 입력은 필수입니다.");
        }
        return lineRepository.findOneWithStations(id);
    }

    private List<Line> findLineAll() {
        return lineRepository.findAllWithStations();
    }

}
