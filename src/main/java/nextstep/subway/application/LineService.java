package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id).get();

        return LineResponse.of(line, toStationsResponse(toStationIds(line)));
    }

    private List<Long> toStationIds(Line line) {
        return Arrays.asList(line.getUpStation().getId(), line.getDownStation().getId());
    }

    private List<StationResponse> toStationsResponse(List<Long> stationIds) {
        List<Station> stations = new ArrayList<>();
        stationIds.forEach(stationId -> stations.add(stationRepository.findById(stationId).get()));

        return stations.stream().map(StationResponse::of).collect(Collectors.toList());
    }
}
