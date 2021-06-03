package nextstep.subway.section.application;

import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionDto;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SectionService {
    private static final String NOT_EXIST_STATION = "존재하지 않는 역: ";
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(StationRepository stationRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public void createSection(SectionDto dto) {
        if (dto.checkStationIdExist()) {
            final Station upStation = findStation(dto.getUpStationId());
            final Station downStation = findStation(dto.getDownStationId());
            sectionRepository.save(new Section(dto.getLine(), upStation, downStation, dto.getDistance()));
        }
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(NOT_EXIST_STATION + stationId));
    }
}
