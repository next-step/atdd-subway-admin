package nextstep.subway.application;

import static nextstep.subway.constant.Constant.NOT_FOUND_LINE;
import static nextstep.subway.constant.Constant.NOT_FOUND_STATION;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineStationRequest;
import nextstep.subway.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation = getStationById(lineRequest.getUpStationId());
        Station downStation = getStationById(lineRequest.getDownStationId());
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.from(persistLine);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long lineId) {
        return LineResponse.from(getLineById(lineId));
    }

    @Transactional
    public void updateLineById(Long lineId, LineRequest lineRequest) {
        Line line = getLineById(lineId);
        line.updateLine(lineRequest);
    }

    @Transactional
    public void delete(Long lineId) {
        lineRepository.delete(getLineById(lineId));
    }

    @Transactional
    public void addLineStation(Long lineId, LineStationRequest lineStationRequest) {
        Station upStation = getStationById(lineStationRequest.getUpStationId());
        Station downStation = getStationById(lineStationRequest.getDownStationId());
        Line line = getLineById(lineId);
        line.addLineStation(
                lineStationRequest.toLineStation(line, upStation, downStation, lineStationRequest.getDistance()));
    }

    @Transactional
    public void removeSectionByStationId(Long lineId, Long stationId) {
        Station removeStation = getStationById(stationId);
        Line line = getLineById(lineId);
        line.remove(removeStation);
    }

    private Line getLineById(Long lineId){
        return lineRepository.findById(lineId).orElseThrow(() -> new NotFoundException(NOT_FOUND_LINE));
    }

    private Station getStationById(Long stationId){
        return stationRepository.findById(stationId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STATION));
    }
}