package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
        List<StationResponse> stationsResponse = toStationsResponse(Arrays.asList(lineRequest.getUpStationId(), lineRequest.getDownStationId()));
        Line persistLine = lineRepository.save(lineRequest.toLine(new Station(stationsResponse.get(0)), new Station(stationsResponse.get(1))));

        return LineResponse.of(persistLine, stationsResponse);
    }

    public List<LineResponse> findAllLines() {
        List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(line -> LineResponse.of(line, toStationsResponse(toStationIds(line))))
                .collect(Collectors.toList());
    }

    public LineResponse findLineById(Long id) {
        Line line = findById(id);
        return LineResponse.of(line, toStationsResponse(toStationIds(line)));
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        Line line = findById(id);
        line.modify(lineRequest);
        lineRepository.flush();
    }

    public void deleteLineById(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findById(Long id) {
        return lineRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 지하철 노선을 찾을 수 없습니다."));
    }

    private List<Long> toStationIds(Line line) {
        return Arrays.asList(line.getUpStation().getId(), line.getDownStation().getId());
    }

    private List<StationResponse> toStationsResponse(List<Long> stationIds) {
        List<Station> stations = new ArrayList<>();
        stationIds.forEach(stationId -> stations.add(stationRepository.findById(stationId).orElseThrow(() -> new NoSuchElementException("해당 지하철 역을 찾을 수 없습니다."))));

        return stations.stream().map(StationResponse::of).collect(Collectors.toList());
    }
}
