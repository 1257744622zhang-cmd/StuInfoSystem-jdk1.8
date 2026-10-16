package com.lc.demo.service.impl;

import com.lc.demo.bean.Teacher;
import com.lc.demo.mapper.TeacherMapper;
import com.lc.demo.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName TeacherServiceImpl
 * @Deacription TODO
 * @Author daier
 * @Date 2021/1/6 1:15
 * @Version 1.0
 **/

@Service
public class TeacherServiceImpl  implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Teacher login(String teaId, String teaPass) {
        return teacherMapper.selectTeacherByIdAndPass(teaId,teaPass);
    }

    @Override
    public List<Teacher> getAllTeacher() {
        return teacherMapper.selectAllTeacher();
    }

    @Override
    public int addTeacher(Teacher teacher) {
        return teacherMapper.insertTeacher(teacher);
    }

    @Override
    public int addTeacherHavePass(Teacher teacher) {
        return teacherMapper.insertTeacherHavaPass(teacher);
    }

    @Override
    public Teacher selectById(String teaId) {
        return teacherMapper.selectTeacherByStuId(teaId);
    }

    @Override
    public int deleTea(String teaId) {
        return teacherMapper.deleteTeaById(teaId);
    }

    @Override
    public void updateTeacher(Teacher teacher, String originalTeaId) {
        teacherMapper.updateTeacher(
                teacher.getTeaId(),
                teacher.getTeaName(),
                teacher.getTeaPass(),
                teacher.getTeaSex(),
                teacher.getTeaTele(),
                originalTeaId
        );
    }

    @Override
    public void updateTeacher(Teacher teacher) {
        // 重载方法，默认使用当前teaId作为originalTeaId
        updateTeacher(teacher, teacher.getTeaId());
    }
    @Override
    public boolean isTeaIdExists(String teaId, String excludeTeaId) {
        // 如果排除的ID与新ID相同，说明没有修改，不应该视为存在
        if (teaId.equals(excludeTeaId)) {
            return false;
        }
        Teacher teacher = teacherMapper.selectById(teaId);
        return teacher != null;
    }
}
