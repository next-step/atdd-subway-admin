package nextstep.subway.application;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.exception.CannotFindException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.constant.Message.*;

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
    public LineResponse saveLine(LineRequest lineRequest) {
        Station upStation  = stationRepository.findById(Long.valueOf(lineRequest.getUpLastStationId()))
                .orElseThrow(() -> new CannotFindException(NOT_FOUND_UP_STATION_ERR));
        Station downStation  = stationRepository.findById(Long.valueOf(lineRequest.getUpLastStationId()))
                .orElseThrow(() -> new CannotFindException(NOT_FOUND_DOWN_STATION_ERR));
        Line persistLine = lineRepository.save(lineRequest.toLine(upStation, downStation));
        return LineResponse.of(persistLine);
    }
    public StationResponse saveStation(StationRequest stationRequest) {
        Station persistStation = stationRepository.save(stationRequest.toStation());
        return StationResponse.of(persistStation);
    }

    public List<StationResponse> findAllStations() {
        List<Station> stations = stationRepository.findAll();

        return stations.stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public StationResponse findStationById(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new CannotFindException(NOT_FOUND_STATION_ERR));
        return StationResponse.of(station);
    }

    @Transactional
    public void deleteStationById(Long id) {
        stationRepository.deleteById(id);
    }



}
