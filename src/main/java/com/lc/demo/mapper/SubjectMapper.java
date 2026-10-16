package com.lc.demo.mapper;

import com.lc.demo.bean.Subject;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SubjectMapper {

    @Select("SELECT sub_id, sub_name, sub_term FROM subject ORDER BY sub_name")
    List<Subject> selectAllSubjects();

    @Select("SELECT sub_id, sub_name, sub_term FROM subject WHERE sub_id = #{subId}")
    Subject selectSubjectById(String subId);

    @Select("SELECT sub_id, sub_name, sub_term FROM subject WHERE sub_name = #{subName}")
    List<Subject> selectSubjectsByName(String subName);

    @Select("SELECT sub_id, sub_name, sub_term FROM subject WHERE sub_term = #{subTerm}")
    List<Subject> selectSubjectsByTerm(String subTerm);
}