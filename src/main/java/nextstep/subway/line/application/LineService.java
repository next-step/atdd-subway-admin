package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.application.exception.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.line.application.exception.LineNotFoundException.error;
import static nextstep.subway.station.application.StationService.NOT_FOUND_STATION;

@Service
@Transactional(readOnly = true)
public class LineService {
    public static final String NOT_FOUND_LINE = "지하철 노선을 찾을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineCreateResponse saveLine(LineRequest request) {
        Station upStation = findByStation(request.getUpStationId());
        Station downStation = findByStation(request.getDownStationId());

        Line persistLine = lineRepository.save(request.toLine(upStation, downStation));
        return LineCreateResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return LineResponse.ofList(lines);
    }

    public LineResponse findOne(Long id) {
        Line line = findLine(id);
        return LineResponse.of(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest request) {
        Line line = findLine(id);
        line.update(request.toLine());
    }

    @Transactional
    public void deleteLind(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> error(NOT_FOUND_LINE));
    }

    private Station findByStation(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> StationNotFoundException.error(NOT_FOUND_STATION));
    }
}