package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(lineRequestToLine(lineRequest));
        return LineResponse.of(persistLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream().map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        return lineRepository.findById(id).map(LineResponse::of)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
    }

    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
        line.updateRequest(lineUpdateRequest);
        lineRepository.save(line);
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line lineRequestToLine(LineRequest lineRequest) {
        return new Line(lineRequest.getName(), lineRequest.getColor(),
                getStationById(lineRequest.getUpStationId()),
                getStationById(lineRequest.getDownStationId()),
                lineRequest.getDistance());
    }

    private Station getStationById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.LINE_NO_FIND_BY_ID.getMessage()));
    }
}
