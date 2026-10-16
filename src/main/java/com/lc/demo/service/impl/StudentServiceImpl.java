package com.lc.demo.service.impl;

import com.lc.demo.bean.Student;
import com.lc.demo.mapper.StudentMapper;
import com.lc.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName StudentServiceImpl
 * @Deacription TODO
 * @Author daier
 * @Date 2021/1/4 23:51
 * @Version 1.0
 **/
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;
    @Override
    public Student login(String stuId, String stuPass)
    {
        return studentMapper.selectStudentByIdAndPass(stuId,stuPass);
    }

    @Override
    public List<Student> getAllStudent() {
        return studentMapper.selectAllStudent();
    }

    @Override
    public int addStudent(Student student) {
        return studentMapper.insertStudent(student);
    }

    @Override
    public Student selectById(String stuId) {
        return studentMapper.selectStudentByStuId(stuId);
    }

    @Override
    public int deleStu(String stuId) {
        return studentMapper.deleteStuById(stuId);
    }

    @Override
    public List<Student> seleStuByClassName(String stuClass) {
        return studentMapper.selectStuByStuclass(stuClass);
    }

    @Override
    public int addStudentHavePass(Student student) {
        return studentMapper.insertStudentHavaPass(student);
    }

    @Override
    public void updateStudent(Student student, String originalStuId) {
        studentMapper.updateStudent(
                student.getStuId(),
                student.getStuName(),
                student.getStuPass(),
                student.getStuClass(),
                student.getStuSex(),
                student.getStuTele(),
                originalStuId
        );
    }

    @Override
    public void updateStudent(Student student) {
        // 重载方法，默认使用当前stuId作为originalStuId
        updateStudent(student, student.getStuId());
    }

    @Override
    public boolean isStuIdExists(String stuId, String excludeStuId) {
        // 如果排除的ID与新ID相同，说明没有修改，不应该视为存在
        if (stuId.equals(excludeStuId)) {
            return false;
        }
        Student student = studentMapper.selectById(stuId);
        return student != null;
    }
}
