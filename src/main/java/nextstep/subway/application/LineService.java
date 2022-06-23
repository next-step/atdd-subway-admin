package nextstep.subway.application;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.UpdateLineRequest;
import nextstep.subway.dto.line.CreateLineRequest;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.line.section.CreateSectionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(
        LineRepository lineRepository,
        StationRepository stationRepository
    ) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(CreateLineRequest createLineRequest) {
        Station upStation = stationRepository.findById(createLineRequest.getUpStationId())
            .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(createLineRequest.getDownStationId())
            .orElseThrow(IllegalArgumentException::new);
        return LineResponse.of(lineRepository.save(createLineRequest.toEntity(upStation, downStation)));
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
            .map(LineResponse::of)
            .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        return LineResponse.of(findLine(id));
    }

    @Transactional
    public void updateLine(Long id, UpdateLineRequest updateLineRequest) {
        Line line = findLine(id);
        line.updateColor(updateLineRequest);
    }

    @Transactional
    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findLine(Long id) {
        return lineRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 노선입니다."));
    }

    @Transactional
    public LineResponse addSection(Long id, CreateSectionRequest createSectionRequest) {
        Line line = findLine(id);
        List<Long> queryParams = Arrays.asList(createSectionRequest.getUpStationId(), createSectionRequest.getDownStationId());
        List<Station> stations = stationRepository.findAllById(queryParams);
        Station upStation = findUpStationById(stations, createSectionRequest.getUpStationId());
        Station downStation = findDownStationById(stations, createSectionRequest.getDownStationId());

        line.addSection(createSectionRequest.toEntity(line, upStation, downStation));
        return LineResponse.of(line);
    }

    private Station findUpStationById(List<Station> stations, Long upStationId) {
        return stations.stream()
            .filter(it -> it.getId().equals(upStationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("요청에 해당하는 상행역을 찾을 수 없습니다."));
    }

    private Station findDownStationById(List<Station> stations, Long downStationId) {
        return stations.stream()
            .filter(it -> it.getId().equals(downStationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("요청에 해당하는 하행역을 찾을 수 없습니다."));
    }
}
