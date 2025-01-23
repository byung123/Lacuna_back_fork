package LacunaMatata.Lacuna.service.admin;

import LacunaMatata.Lacuna.dto.request.admin.statistic.ReqGetUseCountDto;
import LacunaMatata.Lacuna.dto.request.admin.statistic.ReqGetStatisticCountsDto;
import LacunaMatata.Lacuna.dto.response.admin.statistic.*;
import LacunaMatata.Lacuna.entity.user.UserCount;
import LacunaMatata.Lacuna.exception.admin.NotFoundDataException;
import LacunaMatata.Lacuna.repository.admin.StatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    // 날짜에 따라 이용통계 데이터 주기
    public RespTotalAndUseCountDto getUseCount(ReqGetUseCountDto dto) {
        String startDate = dto.getStartDate() == null || dto.getStartDate() == "" ? "1900-01-01" : dto.getStartDate();
        String endDate = dto.getEndDate() == null || dto.getEndDate() == "" ? "2100-12-12" : dto.getEndDate();
        Map<String, Object> params = Map.of(
            "startDate", startDate,
            "endDate", endDate
        );

        List<UserCount> userCountList = statisticsMapper.getUseCountByDate(params);
        if(userCountList.isEmpty()) {
            throw new NotFoundDataException("조건에 해당하는 데이터가 존재하지 않습니다.");
        }

        List<RespServiceCountDto> respServiceCountDtoList = new ArrayList<>();
        int totalCount = 0;
        for(UserCount userCount : userCountList) {
            totalCount += userCount.getSumServiceCount();
            RespServiceCountDto resp = RespServiceCountDto.builder()
                    .serviceName(userCount.getServiceName())
                    .serviceCount(userCount.getSumServiceCount())
                    .build();
            respServiceCountDtoList.add(resp);
        }

        RespTotalAndUseCountDto useCount = RespTotalAndUseCountDto.builder()
                .totalCount(totalCount)
                .serviceCount(respServiceCountDtoList)
                .build();
        return useCount;
    }

    // mbti 통계 날짜에 따라 데이터 주기
    public RespMbtiStatisticCountAndRankDto getMbtiStatisticCounts(ReqGetStatisticCountsDto dto) {
        List<RespMbtiStatisticCountDto> respMbtiStatisticCountDtos = statisticsMapper.getStatisticCountByStartDateAndEndDate(dto);
        List<RespMbtiStatisticRankDto> respMbtiStatisticRankDtos = statisticsMapper.problemStatistic(dto);

        if(respMbtiStatisticCountDtos.isEmpty() && respMbtiStatisticRankDtos.isEmpty()) {
            throw new NotFoundDataException("조건에 해당하는 데이터가 존재하지 않습니다.");
        }

        return RespMbtiStatisticCountAndRankDto.builder()
                .mbtiStatisticCounts(respMbtiStatisticCountDtos)
                .mbtiStatisticRanks(respMbtiStatisticRankDtos)
                .build();
    }
}
