package nextstep.subway.application;

import nextstep.subway.domain.*;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.dto.*;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        Line persistLine = lineRepository.save(lineRequest.toLine());
        List<StationResponse> stationsResponse = toStationsResponse(lineRequest);

        return LineResponse.of(persistLine, stationsResponse);
    }

    private List<StationResponse> toStationsResponse(LineRequest lineRequest) {
        List<Station> stations = new ArrayList<>();
        stations.add(stationRepository.findById(lineRequest.getUpStationId()).get());
        stations.add(stationRepository.findById(lineRequest.getDownStationId()).get());

        return stations.stream().map(StationResponse::of).collect(Collectors.toList());
    }
}
