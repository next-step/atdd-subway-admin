package nextstep.subway.section.application;

import nextstep.subway.exception.DataNotFoundException;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionService(SectionRepository sectionRepository, StationRepository stationRepository) {
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public SectionResponse findSectionById(Long id) {
        return SectionResponse.of(findById(id));
    }

    public Section findById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("일치하는 구간을 찾을 수 없습니다."));
    }

    public SectionResponse findSectionByUpStationId(Long upStationId) {
        Station station = stationRepository.findById(upStationId).get();
        return SectionResponse.of(findByUpStationId(station));
    }

    public Section findByUpStationId(Station station) {
        return sectionRepository.findByUpStation(station)
                .orElseThrow(() -> new DataNotFoundException("일치하는 구간을 찾을 수 없습니다."));
    }
}
