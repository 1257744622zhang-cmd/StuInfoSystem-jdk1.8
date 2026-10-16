package com.lc.demo.mapper;

import com.lc.demo.bean.Classes;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassMapper {

    @Select("select * from class")  // 保持使用 class
    List<Classes> selectAllClass();

    @Select("select * from class where class_id=#{classId}")  // 保持使用 class
    Classes selectClassById(String classId);

    @Delete("DELETE from class where class_id=#{classId}")  // 保持使用 class
    int delectClassById(String classId);

    @Insert("insert into class values(#{classId},#{className})")  // 保持使用 class
    int insertClass(Classes clas);

    @Select("select * from class where class_name=#{className}")  // 保持使用 class
    Classes selectClassByName(String className);

    @Update("UPDATE class SET class_id = #{classes.classId}, class_name = #{classes.className} WHERE class_id = #{originalClassId}")
    int updateClass(@Param("classes") Classes classes, @Param("originalClassId") String originalClassId);
}