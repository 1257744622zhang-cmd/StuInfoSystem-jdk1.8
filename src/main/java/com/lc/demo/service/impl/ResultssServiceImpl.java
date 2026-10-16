package com.lc.demo.service.impl;

import com.lc.demo.bean.*;
import com.lc.demo.mapper.ResultMapper;
import com.lc.demo.mapper.StudentMapper;
import com.lc.demo.service.ResultssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ResultssServiceImpl implements ResultssService {

    @Autowired
    private ResultMapper resultMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<Resultss> getAllResult() {
        return resultMapper.selectAllResult();
    }

    @Override
    public List<Resultss> selectByStuId(String stuId) {
        return resultMapper.selectResultByStuId(stuId);
    }

    @Override
    public List<Resultss> selectByStuIdAndResTerm(String stuId, String resTerm) {
        return resultMapper.selectResultByStuIdAndTerm(stuId,resTerm);
    }

    @Override
    public int addResult(Resultss resultss) {
        return resultMapper.insertResult(resultss);
    }

    @Override
    public int deleteResultById(int resId) {
        return resultMapper.deleteResultById(resId);
    }

    @Override
    public Resultss selectResultByStuIdAndSubName(String stuId, String subName) {
        return resultMapper.selectResultByStuIdAndSubName(stuId,subName);
    }

    @Override
    public Resultss selectResultByResId(int resId) {
        return resultMapper.selectResultByResId(resId);
    }

    @Override
    public List<Rank> selectRankByTerm(String resTerm) {
        List<Rank> ranks = resultMapper.selectRankByTerm(resTerm);
        for (Rank r :ranks) {
            Map<String, Integer> reamap=new HashMap<>();
            List<Map<String, Integer>> maps = resultMapper.selectResultMap(r.getStuId(), r.getResTerm());
            for (Map map:maps)
            {
                reamap.put((String)map.get("sub_name"),(Integer) map.get("res_num"));
            }
            r.setStuName(studentMapper.selectNameById(r.getStuId()));
            r.setResmap(reamap);
        }
        return ranks;
    }

    @Override
    public List<Rank> selectRankByTermAndStuClass(String resTerm, String stuClass) {
        List<Rank> ranks=resultMapper.selectRankByTermAndStuId(studentMapper.selectIdByClass(stuClass),resTerm);
        for (Rank r :ranks) {
            Map<String, Integer> reamap=new HashMap<>();
            List<Map<String, Integer>> maps = resultMapper.selectResultMap(r.getStuId(), r.getResTerm());
            for (Map map:maps)
            {
                reamap.put((String)map.get("sub_name"),(Integer) map.get("res_num"));
            }
            r.setStuName(studentMapper.selectNameById(r.getStuId()));
            r.setResmap(reamap);
        }
        return ranks;
    }

    @Override
    public List<Rank> selectRankBySubject(String subName) {
        return resultMapper.selectRankBySubject(subName);
    }

    @Override
    public List<ClassAvgScore> selectClassAvgScoreByClassName(String className) {
        return resultMapper.selectClassAvgScoreByClassName(className);
    }

    @Override
    public double selectTotalAvgScoreByClassName(String className) {
        return resultMapper.selectTotalAvgScoreByClassName(className);
    }

    @Override
    public ScoreDistribution getScoreDistribution(String subName) {
        // 获取该课程的所有成绩
        List<Integer> scores = resultMapper.selectScoresBySubject(subName);

        if (scores == null || scores.isEmpty()) {
            return null;
        }

        ScoreDistribution distribution = new ScoreDistribution();
        distribution.setSubName(subName);

        // 计算基本统计信息
        double sum = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for (int score : scores) {
            sum += score;
            if (score > max) max = score;
            if (score < min) min = score;
        }

        double avg = sum / scores.size();
        distribution.setAvgScore(avg);
        distribution.setMaxScore(max);
        distribution.setMinScore(min);
        distribution.setTotalCount(scores.size());

        // 计算标准差
        double variance = 0;
        for (int score : scores) {
            variance += Math.pow(score - avg, 2);
        }
        variance /= scores.size();
        double stdDev = Math.sqrt(variance);
        distribution.setStdDev(stdDev);

        // 创建分数段分布 - 这里只传递scores参数
        List<ScoreBucket> buckets = createScoreBuckets(scores);
        distribution.setScoreBuckets(buckets);

        return distribution;
    }

    private List<ScoreBucket> createScoreBuckets(List<Integer> scores) {
        List<ScoreBucket> buckets = new ArrayList<>();
        int total = scores.size();

        if (total == 0) {
            // 如果没有成绩数据，返回空的分数段
            buckets.add(new ScoreBucket("0-59", 0, 0));
            buckets.add(new ScoreBucket("60-70", 0, 0));
            buckets.add(new ScoreBucket("71-80", 0, 0));
            buckets.add(new ScoreBucket("81-90", 0, 0));
            buckets.add(new ScoreBucket("91-100", 0, 0));
            return buckets;
        }

        // 特殊处理：0-59分为一个分数段（不及格段）
        String range0_60 = "0-59";
        int count0_60 = 0;
        for (int score : scores) {
            if (score >= 0 && score <= 59) {
                count0_60++;
            }
        }
        double percentage0_60 = (double) count0_60 / total * 100;
        buckets.add(new ScoreBucket(range0_60, count0_60, percentage0_60));

        // 61-70分
        String range61_70 = "60-70";
        int count61_70 = 0;
        for (int score : scores) {
            if (score >= 60 && score <= 70) {
                count61_70++;
            }
        }
        double percentage61_70 = (double) count61_70 / total * 100;
        buckets.add(new ScoreBucket(range61_70, count61_70, percentage61_70));

        // 71-80分
        String range71_80 = "71-80";
        int count71_80 = 0;
        for (int score : scores) {
            if (score >= 71 && score <= 80) {
                count71_80++;
            }
        }
        double percentage71_80 = (double) count71_80 / total * 100;
        buckets.add(new ScoreBucket(range71_80, count71_80, percentage71_80));

        // 81-90分
        String range81_90 = "81-90";
        int count81_90 = 0;
        for (int score : scores) {
            if (score >= 81 && score <= 90) {
                count81_90++;
            }
        }
        double percentage81_90 = (double) count81_90 / total * 100;
        buckets.add(new ScoreBucket(range81_90, count81_90, percentage81_90));

        // 91-100分
        String range91_100 = "91-100";
        int count91_100 = 0;
        for (int score : scores) {
            if (score >= 91 && score <= 100) {
                count91_100++;
            }
        }
        double percentage91_100 = (double) count91_100 / total * 100;
        buckets.add(new ScoreBucket(range91_100, count91_100, percentage91_100));

        return buckets;
    }

    @Override
    public List<ClassSubjectAvg> getClassSubjectAvgScores(String className) {
        return resultMapper.selectClassSubjectAvgScores(className);
    }

    @Override
    public List<Map<String, Object>> getGradeChartDataByStuId(String stuId) {
        return resultMapper.getGradeChartDataByStuId(stuId);

    }

}
