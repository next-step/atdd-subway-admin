package nextstep.subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
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
        Line persistLine = lineRepository.save(lineRequest.toLine());
        addStation(persistLine, lineRequest.getUpStationId());
        addStation(persistLine, lineRequest.getDownStationId());
        return LineResponse.of(persistLine);
    }

    private void addStation(Line line, Long stationId){
        line.addStation(getStation(stationId));
    }

    private Station getStation(Long stationId) {
        return stationRepository.getById(stationId);
    }

    private List<StationResponse> findStationsByLineId(Long lineId){
        return stationRepository.findByLine_Id(lineId).stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        List<LineResponse> lineResponses = new ArrayList<>();
        for(Line line : lines){
            LineResponse lineResponse = LineResponse.of(line);
            lineResponses.add(lineResponse);
        }
        return lineResponses;
    }

    public LineResponse findById(Long lineId) {
        return LineResponse.of(lineRepository.findById(lineId).orElseThrow(NotFoundException::new));
    }

    @Transactional
    public void updateLineById(Long lineId, LineRequest lineRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(NotFoundException::new);
        line.updateLine(lineRequest);
    }

    @Transactional
    public void delete(Long lineId) {
        lineRepository.delete(lineRepository.findById(lineId).orElseThrow(NotFoundException::new));
    }
}
