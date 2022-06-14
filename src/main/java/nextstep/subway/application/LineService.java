package nextstep.subway.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.LineStation;
import nextstep.subway.domain.LineStationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final LineStationRepository lineStationRepository;
    private final StationService stationService;
    private final LineStationService lineStationService;

    public LineService(LineRepository lineRepository, LineStationRepository lineStationRepository,
                       StationService stationService, LineStationService lineStationService) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationService = stationService;
        this.lineStationService = lineStationService;
    }

    public LineResponse readLine(Long id) throws NotFoundException {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        LineResponse lineResponse = LineResponse.from(line);

        List<LineStation> byLineId = lineStationRepository.findByLineId(id);
        for (LineStation lineStation : byLineId) {
            lineResponse.addStationResponses(stationService.findStation(lineStation.getStationId()));
        }
        return lineResponse;
    }

    public List<LineResponse> readLines() {
        return lineRepository.findAll().stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {
        Line persistLine = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));

        lineStationService.addSection(
                new SectionRequest(lineRequest.getUpStationId(), lineRequest.getDownStationId(),
                        lineRequest.getDistance()), persistLine.getId());
        return LineResponse.from(persistLine);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) throws NotFoundException {
        Line line = lineRepository.findById(id).orElseThrow(NotFoundException::new);
        line.update(lineRequest);
    }

}
