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

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStation(lineRequest.getUpStationId());
        Station downStation = getStation(lineRequest.getDownStationId());
        return LineResponse.of(lineRepository.save(lineRequest.toLine(upStation, downStation)));
    }

    public List<LineResponse> findAllLines() {
        Lines lines = new Lines(lineRepository.findAll());

        return lines.asList().stream()
                .map(line -> LineResponse.of(line))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SUCH_LINE_EXCEPTION.getErrorMessage()));
        return LineResponse.of(line);
    }

    @Transactional
    public LineResponse modifyLine(Long id, LineRequest lineRequest) {
        Line line = lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SUCH_LINE_EXCEPTION.getErrorMessage()));
        line.modify(lineRequest.getName(), lineRequest.getColor());
        return LineResponse.of(lineRepository.save(line));
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ErrorCode.NO_SUCH_LINE_EXCEPTION.getErrorMessage()));
        lineRepository.delete(line);
    }

    private Station getStation(long stationId) {
        return stationRepository.getById(stationId);
    }
}
