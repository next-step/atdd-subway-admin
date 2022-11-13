package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
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
        LineResponse lineResponse = LineResponse.of(persistLine);
        lineResponse.setStations(findStationsByLineId(persistLine.getId()));
        return lineResponse;
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

        return lines.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }
}
