package nextstep.subway.section.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public SectionResponse addSection(Long lineId, SectionRequest request) {

        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다. lineId = " + lineId));

        List<Station> stations = stationRepository.findAllById(request.toIds());

        Section section = line.addSection(
                stations.stream().filter(s -> s.matchId(request.getUpStationId())).findFirst().orElseThrow(() -> new IllegalArgumentException("상행역이 존재하지 않습니다.")),
                stations.stream().filter(s -> s.matchId(request.getDownStationId())).findFirst().orElseThrow(() -> new IllegalArgumentException("하행역이 존재하지 않습니다.")),
                request.getDistance()
        );

        return SectionResponse.of(section);
    }

    @Transactional
    public void removeSection(Long lineId, Long stationId) {

        Line line = lineRepository.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("노선이 존재하지 않습니다. lineId = " + lineId));

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("지하철역이 존재하지 않습니다. stationId = " + stationId));

        line.removeSection(station);

    }
}
