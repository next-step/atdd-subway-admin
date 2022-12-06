package nextstep.subway.application;

import nextstep.subway.constants.ErrorCode;
import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStationById(lineRequest.getUpStationId());
        Station downStation = findStationById(lineRequest.getDownStationId());
        return LineResponse.from(lineRepository.save(lineRequest.toLine(upStation, downStation)));
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = findLine(id);
        return LineResponse.from(line);
    }

    @Transactional
    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        Line line = findLine(id);
        line.modify(lineRequest.getName(), lineRequest.getColor());
        return LineResponse.from(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = findLine(id);
        lineRepository.delete(line);
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Station reqDeleteStation = findStationById(stationId);
        Line line = findLine(lineId);
        line.deleteStation(reqDeleteStation);
    }

    private Station findStationById(long stationId) {
        return stationRepository
                .findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SUCH_STATION_EXCEPTION.getErrorMessage()));
    }

    public Line findLine(Long id) {
        return lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SUCH_LINE_EXCEPTION.getErrorMessage()));
    }

    public Line findLine(String name) {
        return lineRepository
                .findByName(name)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SUCH_LINE_EXCEPTION.getErrorMessage()));
    }
}
