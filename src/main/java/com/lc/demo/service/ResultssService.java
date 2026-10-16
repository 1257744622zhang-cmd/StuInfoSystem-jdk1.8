package com.lc.demo.service;

import com.lc.demo.bean.Rank;
import com.lc.demo.bean.Resultss;
import com.lc.demo.bean.ClassAvgScore;
import com.lc.demo.bean.ScoreDistribution;
import java.util.List;
import com.lc.demo.bean.ClassSubjectAvg;

import java.util.Map;


public interface ResultssService {

    List<Resultss> getAllResult();

    List<Resultss> selectByStuId(String stuId);

    List<Resultss> selectByStuIdAndResTerm(String stuId,String resTerm);

    int addResult(Resultss resultss);

    int deleteResultById(int resId);

    Resultss selectResultByStuIdAndSubName(String stuId,String subName);

    Resultss selectResultByResId(int resId);

    List<Rank> selectRankByTerm(String resTerm);

    List<Rank> selectRankByTermAndStuClass(String resTerm,String stuClass);

    List<Rank> selectRankBySubject(String subName);

    List<ClassAvgScore> selectClassAvgScoreByClassName(String className);
    double selectTotalAvgScoreByClassName(String className);

    ScoreDistribution getScoreDistribution(String subName);

    List<ClassSubjectAvg> getClassSubjectAvgScores(String className);

    List<Map<String, Object>> getGradeChartDataByStuId(String stuId);


}
