package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.AddSectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.domain.Stations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public void addSection(Long lineId, AddSectionRequest addSectionRequest) {
        Stations stations = new Stations(stationRepository.findAllById(Arrays.asList(addSectionRequest.getUpStationId(), addSectionRequest.getDownStationId())));
        Line line = lineRepository.findById(lineId).orElseThrow(EntityNotFoundException::new);
        line.addSection(stations.get(addSectionRequest.getUpStationId()), stations.get(addSectionRequest.getDownStationId()), addSectionRequest.toDistance());
    }

    @Transactional
    public void deleteSection(Long id, Long stationId) {
        Station station = stationRepository.findById(stationId).orElseThrow(EntityNotFoundException::new);
        Line line = lineRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        line.removeSection(station);
    }
}
