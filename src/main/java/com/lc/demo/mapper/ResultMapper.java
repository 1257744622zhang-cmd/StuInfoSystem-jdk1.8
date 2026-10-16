package com.lc.demo.mapper;

import com.lc.demo.bean.ClassAvgScore;
import com.lc.demo.bean.ClassSubjectAvg;
import com.lc.demo.bean.Rank;
import com.lc.demo.bean.Resultss;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ResultMapper
 * @Deacription TODO
 * @Author daier
 * @Date 2021/1/9 11:32
 * @Version 1.0
 **/
@Mapper
public interface ResultMapper {

    @Select("select * from result where  stu_id=#{stuId}")
    List<Resultss> selectResultByStuId(String stuId);

    @Select("select * from result")
    List<Resultss> selectAllResult();

    @Select("select * from result where stu_id=#{stuId} And res_term=#{resTerm}")
    List<Resultss> selectResultByStuIdAndTerm(@Param("stuId") String stuId, @Param("resTerm") String resTerm);

    @Insert("insert into result (stu_id,sub_name,res_num,res_term)values(#{stuId},#{subName},#{resNum},#{resTerm})")
    int insertResult(Resultss resultss);

    @Delete("DELETE from result where res_id=#{resId}")
    int deleteResultById(int resId);

    @Select("select * from result where stu_id=#{stuId} And sub_name=#{subName}")
    Resultss selectResultByStuIdAndSubName(@Param("stuId")String stuId,@Param("subName")String subName );

    @Select("select * from result where res_id=#{resId}")
    Resultss selectResultByResId(int resId);



    @Select("select @rownum:=@rownum+1 AS rownum,stu_id,stu_allres,res_term from (SELECT @rownum:=0) r" +
            ",(select res_term,stu_id,sum(res_num)as'stu_allres' from result,(SELECT @rownum:=0) r\n" +
            "where  res_term=#{resTerm} GROUP BY stu_id ORDER BY sum(res_num) DESC) w")
    List<Rank> selectRankByTerm(String resTerm);


    @Select("select sub_name,res_num from result where stu_id=#{stuId} and res_term=#{resTerm}")
    List<Map<String,Integer>> selectResultMap(@Param("stuId") String stuId,@Param("resTerm") String resTerm);

    @Select({"<script>",
            "select @rownum:=@rownum+1 AS rownum,stu_id,stu_allres,res_term from (SELECT @rownum:=0) r" +
                    ",(select res_term,stu_id,sum(res_num)as'stu_allres' from result,(SELECT @rownum:=0) r\n" +
                    "where  res_term=#{resTerm} and stu_id in",
            "<foreach collection='stuId' item='stuId' open='(' separator=',' close=')'>",
            "#{stuId}",
            "</foreach>",
            "GROUP BY stu_id ORDER BY sum(res_num) DESC) w",
            "</script>"})
    List<Rank> selectRankByTermAndStuId(@Param("stuId") List<String> stuId,@Param("resTerm") String resTerm);

    @Select("SELECT r.stu_id,s.stu_name, r.sub_name,r.res_num " +
            "FROM result r " +
            "JOIN student s ON r.stu_id = s.stu_id " +
            "WHERE r.sub_name = #{subName} and r.stu_id = s.stu_id " +
            "ORDER BY r.res_num DESC")
    List<Rank> selectRankBySubject(String subName);

    @Select("SELECT r.sub_name, AVG(r.res_num) as avg_score " +
            "FROM result r " +
            "JOIN student s ON r.stu_id = s.stu_id " +
            "WHERE s.stu_class = #{className} " +
            "GROUP BY r.sub_name " +
            "ORDER BY r.sub_name")
    List<ClassAvgScore> selectClassAvgScoreByClassName(String className);

    @Select("SELECT AVG(r.res_num) as total_avg_score " +
            "FROM result r " +
            "JOIN student s ON r.stu_id = s.stu_id " +
            "WHERE s.stu_class = #{className}")
    Double selectTotalAvgScoreByClassName(String className);

    @Select("SELECT res_num FROM result WHERE sub_name = #{subName} ORDER BY res_num")
    List<Integer> selectScoresBySubject(String subName);

    @Select("SELECT s.stu_class as className, r.sub_name as subName, AVG(r.res_num) as avgScore " +
            "FROM result r " +
            "JOIN student s ON r.stu_id = s.stu_id " +
            "WHERE s.stu_class = #{className} " +
            "GROUP BY r.sub_name " +
            "ORDER BY AVG(r.res_num) DESC")
    List<ClassSubjectAvg> selectClassSubjectAvgScores(String className);
    @Select("SELECT res_term as term, sub_name as subject, res_num as grade " +
            "FROM result WHERE stu_id = #{stuId} ORDER BY res_term, sub_name")
    List<Map<String, Object>> getGradeChartDataByStuId(String stuId);
}
