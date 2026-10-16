package com.lc.demo.mapper;

import com.lc.demo.bean.Student;
import com.lc.demo.bean.ClassSubjectAvg;
import com.lc.demo.bean.Teacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @ClassName TeacherMapper
 * @Deacription TODO
 * @Author daier
 * @Date 2021/1/8 18:40
 * @Version 1.0
 **/
@Mapper
public interface TeacherMapper {

    @Select("select * from teacher where  tea_id=#{teaId} and tea_pass=#{teaPass}")
    Teacher selectTeacherByIdAndPass(@Param("teaId") String teaId, @Param("teaPass") String teaPass);

    @Select("select * from teacher")
    List<Teacher> selectAllTeacher();

    @Insert("insert into teacher (tea_id,tea_name,tea_sex,tea_tele) values(#{teaId},#{teaName},#{teaSex},#{teaTele})")
    int insertTeacher(Teacher tea);

    @Insert("insert into teacher  values(#{teaId},#{teaName},#{teaPass},#{teaSex},#{teaTele})")
    int insertTeacherHavaPass(Teacher tea);

    @Select("select * from teacher where  tea_id=#{teaId} ")
    Teacher selectTeacherByStuId(String teaId);

    @Delete("DELETE from teacher where tea_id=#{teaId}")
    int deleteTeaById(String teaId);

    @Update("UPDATE teacher SET tea_id = #{teaId}, tea_name = #{teaName}, tea_pass = #{teaPass}, " +
            "tea_sex = #{teaSex}, tea_tele = #{teaTele} " +
            "WHERE tea_id = #{originalTeaId}")
    int updateTeacher(@Param("teaId") String teaId,
                      @Param("teaName") String teaName,
                      @Param("teaPass") String teaPass,
                      @Param("teaSex") String teaSex,
                      @Param("teaTele") String teaTele,
                      @Param("originalTeaId") String originalTeaId);

    @Select("SELECT * FROM teacher WHERE tea_id = #{teaId}")
    Teacher selectById(String teaId);
}
