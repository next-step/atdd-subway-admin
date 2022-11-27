package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.Stations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
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
    public LineResponse saveStation(LineRequest lineRequest) {
        Line line = lineRequest.toLine();
        Long upStationId = lineRequest.getUpStationId();
        Long downStationId = lineRequest.getDownStationId();
        Stations stations = new Stations(stationRepository.findAllById(Arrays.asList(upStationId, downStationId)));
        line.addSection(stations.get(upStationId), stations.get(downStationId), lineRequest.toDistance());
        Line saveLine = lineRepository.save(line);
        return LineResponse.of(saveLine);
    }

    public List<LineResponse> findLines() {
        return lineRepository.findAll()
                .stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        return LineResponse.of(lineRepository.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    @Transactional
    public void updateLine(Long id, LineUpdateRequest lineUpdateRequest) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        line.update(lineUpdateRequest.toName(), lineUpdateRequest.toColor());
    }

    @Transactional
    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        lineRepository.delete(line);
    }
}
