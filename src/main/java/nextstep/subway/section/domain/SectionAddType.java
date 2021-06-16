package nextstep.subway.section.domain;

import nextstep.subway.exception.SectionCreateFailException;
import nextstep.subway.section.dto.SectionAddModel;

import java.util.Arrays;

public enum SectionAddType {

    UP_STATION_BEFORE(StationMatch.NOT_MATCHED, StationMatch.UP_STATION) {
        @Override
        public Section addSection(SectionAddModel sectionAddModel) {
            return sectionAddModel.line().upStationBeforeAdd(sectionAddModel);
        }
    },
    UP_STATION_AFTER(StationMatch.UP_STATION, StationMatch.NOT_MATCHED) {
        @Override
        public Section addSection(SectionAddModel sectionAddModel) {
            return sectionAddModel.line().upStationAfterAdd(sectionAddModel);
        }
    },
    DOWN_STATION_BEFORE(StationMatch.NOT_MATCHED, StationMatch.DOWN_STATION) {
        @Override
        public Section addSection(SectionAddModel sectionAddModel) {
            return sectionAddModel.line().downStationBeforeAdd(sectionAddModel);
        }
    },
    DOWN_STATION_AFTER(StationMatch.DOWN_STATION, StationMatch.NOT_MATCHED) {
        @Override
        public Section addSection(SectionAddModel sectionAddModel) {
            return sectionAddModel.line().downStationAfterAdd(sectionAddModel);
        }
    };

    StationMatch upStationMatch;
    StationMatch downStationMatch;

    SectionAddType(StationMatch upStationMatch, StationMatch downStationMatch) {
        this.upStationMatch = upStationMatch;
        this.downStationMatch = downStationMatch;
    }

    public abstract Section addSection(SectionAddModel sectionAddModel);

    public static SectionAddType getSectionAddType(StationMatch upStationMatch, StationMatch downStationMatch) {
        return Arrays.stream(SectionAddType.values())
                .filter(sectionAddType -> sectionAddType.upStationMatch == upStationMatch)
                .filter(sectionAddType -> sectionAddType.downStationMatch == downStationMatch)
                .findFirst()
                .orElseThrow(SectionCreateFailException::new);
    }
}
