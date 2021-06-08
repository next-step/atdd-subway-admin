package nextstep.subway.section.application;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.Line;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.SectionRepository;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import nextstep.subway.station.StationNotFoundException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public SectionResponse registerSection(final SectionRequest request, final Line line) {
        Station upStation = stationRepository.findById(request.getUpStationId())
                .orElseThrow(()->new StationNotFoundException());

        Station downStation = stationRepository.findById(request.getDownStationId())
                .orElseThrow(()->new StationNotFoundException());

        Section persistSection = sectionRepository.save(request.toSection(line, upStation, downStation));

        return SectionResponse.of(persistSection);
    }
}
