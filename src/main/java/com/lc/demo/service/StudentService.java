package com.lc.demo.service;

import com.lc.demo.bean.Student;

import java.util.List;


public interface StudentService {
     Student login(String stuId,String stuPass);

     List<Student> getAllStudent();

     int addStudent(Student student);

     int addStudentHavePass(Student student);

     Student selectById(String stuId);

     int deleStu(String stuId);

     List<Student> seleStuByClassName(String stuClass);

    void updateStudent(Student student, String originalStuId);

    void updateStudent(Student student);

     boolean isStuIdExists(String stuId, String excludeStuId);

}
