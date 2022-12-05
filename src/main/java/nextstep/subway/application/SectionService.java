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
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(EntityNotFoundException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(EntityNotFoundException::new);
        Line line = lineRepository.findById(lineId)
                .orElseThrow(EntityNotFoundException::new);

        LineStation lineStation = new LineStation(downStation, upStation, sectionRequest.getDistance(), line);
        line.addLineStation(lineStation);
        return SectionResponse.of(lineStationRepository.save(lineStation));
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
