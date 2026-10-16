package com.lc.demo.service;

import com.lc.demo.bean.Classes;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @ClassName ClassService
 * @Deacription TODO
 * @Author daier
 * @Date 2021/1/6 22:14
 * @Version 1.0
 **/
public interface ClassService {

    List<Classes> getAllClass();

    Classes selectById(String classId);

    Classes selectByName(String className);

    int deleteById(String classId);

    int addClass(Classes classes);



    @Update("UPDATE classes SET class_id = #{newClass.classId}, class_name = #{newClass.className} WHERE class_id = #{originalClassId}")
    void updateClass(@Param("newClass") Classes newClass, @Param("originalClassId") String originalClassId);

}
