package nextstep.subway.section.application;

import java.util.Optional;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.station.application.StationQueryService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class SectionQueryService {

    private final StationQueryService stationQueryService;
    private final SectionRepository sectionRepository;

    public SectionQueryService(StationQueryService stationQueryService,
                               SectionRepository sectionRepository) {
        this.stationQueryService = stationQueryService;
        this.sectionRepository = sectionRepository;
    }

    public Section findById(Long sectionId) {
        return sectionRepository.findById(sectionId)
                                .orElseThrow(() -> new IllegalArgumentException("section을 찾을 수 없습니다."));
    }

    public Optional<Section> findByUpStationAndDownStation(Long upStationId, Long downStationId) {

        Station upStation = stationQueryService.findById(upStationId);
        Station downStation = stationQueryService.findById(downStationId);

        return sectionRepository.findByUpStationAndDownStation(upStation, downStation);
    }
}
