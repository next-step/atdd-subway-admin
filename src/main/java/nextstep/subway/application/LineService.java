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

        Station station = lines.get(0).getUpStation();

        List<StationResponse> test = toStationsResponse(Arrays.asList(lines.get(0).getUpStation().getId(), lines.get(0).getDownStation().getId()));
        List<StationResponse> test2 = toStationsResponse(Arrays.asList(lines.get(1).getUpStation().getId(), lines.get(1).getDownStation().getId()));

        List<LineResponse> result =
        lines.stream()
                .map(line -> LineResponse.of(line, toStationsResponse(Arrays.asList(line.getUpStation().getId(), line.getDownStation().getId()))))
                .collect(Collectors.toList());

        return lines.stream()
                .map(line -> LineResponse.of(line, toStationsResponse(Arrays.asList(line.getUpStation().getId(), line.getDownStation().getId()))))
                .collect(Collectors.toList());
    }

    private List<StationResponse> toStationsResponse(List<Long> stationIds) {
        List<Station> stations = new ArrayList<>();
        stationIds.forEach(stationId -> stations.add(stationRepository.findById(stationId).get()));

        return stations.stream().map(StationResponse::of).collect(Collectors.toList());
    }

}
