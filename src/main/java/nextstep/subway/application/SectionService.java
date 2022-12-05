package nextstep.subway.application;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import nextstep.subway.constants.ErrorMessage;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.LineStation;
import nextstep.subway.domain.line.LineStationRepository;
import nextstep.subway.domain.line.LineStations;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationPosition;
import nextstep.subway.domain.station.StationRegisterStatus;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.SectionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private LineRepository lineRepository;
    private LineStationRepository lineStationRepository;

    private StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, LineStationRepository lineStationRepository,
            StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.lineStationRepository = lineStationRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public SectionResponse saveSection(Long lineId, SectionRequest sectionRequest) {
        //노선에 지하철역 등록
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);
        LineStations lineStations = line.getLineStations();
        if (!lineStations.isEmpty()) {
            //유효성 검증
            StationRegisterStatus upStationStatus = lineStations.getStationRegisterStatus(upStation);
            StationRegisterStatus downStationStatus = lineStations.getStationRegisterStatus(downStation);
            if (upStationStatus.isEmpty() && downStationStatus.isEmpty()) {
                throw new IllegalArgumentException(ErrorMessage.BOTH_STATIONS_NOT_REGISTERED);
            }
            if (!upStationStatus.isEmpty() && !downStationStatus.isEmpty()) {
                throw new IllegalArgumentException(ErrorMessage.ALREADY_REGISTERED_SECTION);
            }
            upStationStatus.validate(StationPosition.UPSTATION, sectionRequest.getDistance());
            downStationStatus.validate(StationPosition.DOWNSTATION, sectionRequest.getDistance());

        }

        LineStation lineStation = new LineStation(downStation, upStation, sectionRequest.getDistance(), line);
        LineStation persistLineStation = lineStationRepository.save(lineStation);
        return SectionResponse.of(persistLineStation);
    }

    public List<SectionResponse> findLineStationsByLineId(Long id) {
        Line line = lineRepository.findById(id).get();
        List<LineStation> findResults = lineStationRepository.findByLine(line);
        return findResults.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }

    public List<SectionResponse> findLineStationByStationId(Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(NoSuchElementException::new);
        List<LineStation> findResults = new ArrayList<>();
        findResults.addAll(lineStationRepository.findByStation(station));
        findResults.addAll(lineStationRepository.findByPreStation(station));
        return findResults.stream()
                .map(SectionResponse::of)
                .collect(Collectors.toList());
    }
}
