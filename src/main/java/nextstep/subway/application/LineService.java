package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.exception.LineException;
import nextstep.subway.exception.StationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.LineExceptionMessage.NONE_EXISTS_LINE;
import static nextstep.subway.exception.StationExceptionMessage.NONE_EXISTS_STATION;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    public LineService(LineRepository lineRepository,StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());
        Line persistStation = lineRepository.save(lineRequest.toLine(upStation,downStation));
        return LineResponse.of(persistStation);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(() -> new LineException(NONE_EXISTS_LINE.getMessage()));
        return LineResponse.of(line);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public LineResponse modifyLine(LineRequest lineRequest) {
        lineRepository.findById(lineRequest.getId()).orElseThrow(() -> new LineException(NONE_EXISTS_LINE.getMessage()));
        Station upStation = findStation(lineRequest.getUpStationId());
        Station downStation = findStation(lineRequest.getDownStationId());
        return LineResponse.of(lineRequest.toLine(upStation,downStation));
    }

    private Station findStation(Long id){
        return stationRepository.findById(id).orElseThrow(() -> new StationException(NONE_EXISTS_STATION.getMessage()));
    }
}

