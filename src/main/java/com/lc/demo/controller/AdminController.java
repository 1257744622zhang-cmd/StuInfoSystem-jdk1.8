package com.lc.demo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.demo.bean.*;
import com.lc.demo.service.AdminService;
import com.lc.demo.service.ClassService;
import com.lc.demo.service.StudentService;
import com.lc.demo.service.TeacherService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @ClassName AdminController
 * @Deacription TODO
 * @Author daier
 * @Date 2021/1/6 0:46
 * @Version 1.0
 **/
@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ClassService classService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @PostMapping(value = "/adm/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password, Map<String,Object> map, HttpSession session)
    {
        Admin adm=adminService.adminLogin(username,password);
        if(adm!=null)
        {
            List<Classes> classes=classService.getAllClass();
            session.setAttribute("loginUser",username);
            return "redirect:/admmain.html";
        }
        else
        {
            map.put("msg","用户名或密码错误");
            return  "login";
        }
    }

    //返回首页
    @GetMapping(value = "/adm/toindex")
    public String toindex(){
        return "redirect:/admmain.html";
    }

    //返回学生管理首页
    @GetMapping(value = "/adm/tostudmin/{pn}")
    public String tostudmin(@PathVariable("pn") Integer pn,Model model)
    {
        PageHelper.startPage(pn, 6);
        List<Student> students=studentService.getAllStudent();
        List<Classes> classes = classService.getAllClass();
        PageInfo<Student> page = new PageInfo<Student>(students, 5);
        model.addAttribute("classes",classes);
        model.addAttribute("pageInfo",page);
        return "forward:/stuadmin.html";
    }

    //返回学生添加页面
    @GetMapping(value = "/adm/stuadd")
    public String stutoaddpage(Model model)
    {
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes",classes);
        return "adm/addstu";
    }

    //处理学生添加事务
    //处理学生添加事务
    @PostMapping(value = "/adm/stuAdd")
    public String stuAdd(@Valid Student student, BindingResult bindingResult, Model model)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();
        List<Classes> classes = classService.getAllClass();

        // 自定义验证：检查性别是否为空
        if (student.getStuSex() == null || student.getStuSex().trim().isEmpty()) {
            errmsg.add(new MyError("请选择性别"));
        }

        if(errmsg.size() == 0 && allErrors.size() == 0)
        {
            Student studentVail = studentService.selectById(student.getStuId());
            if(studentVail == null)
            {
                studentService.addStudent(student);
                return "redirect:/adm/tostudmin/1";
            }
            else{
                errmsg.add(new MyError("已存在该学号的学生"));
                model.addAttribute("errors", errmsg);
                model.addAttribute("stu", student);
                model.addAttribute("classes", classes);
                return "adm/addstu";
            }
        }
        else
        {
            // 将BindingResult中的错误信息也添加到errmsg中
            for (ObjectError error : allErrors) {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors", errmsg);
            model.addAttribute("stu", student);
            model.addAttribute("classes", classes);
            return "adm/addstu";
        }
    }

    //处理删除学生事务
    @DeleteMapping(value = "/adm/stu/{stuId}")
    public String delestu(@PathVariable("stuId") String stuId)
    {
        studentService.deleStu(stuId);
        return "redirect:/adm/tostudmin/1";
    }

    //返回学生修改页面
    @GetMapping(value = "/adm/stu/{stuId}")
    public String updateStuPage(@PathVariable("stuId") String stuId,Model model)
    {
        Student stu=studentService.selectById(stuId);
        List<Classes> classes=classService.getAllClass();
        model.addAttribute("stu",stu);
        model.addAttribute("classes",classes);
        return "adm/updatestu";
    }

    //更新学生信息操作
    @PutMapping(value = "/adm/stu")
    public String updateStu(@Valid Student student, BindingResult bindingResult, Model model,
                            @RequestParam(value = "originalStuId", required = false) String originalStuId)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();
        List<Classes> classes = classService.getAllClass();

        // 添加密码非空验证
        if (student.getStuPass() == null || student.getStuPass().trim().isEmpty()) {
            errmsg.add(new MyError("密码不能为空"));
        }

        if(errmsg.size() == 0 && allErrors.size()==0) {
        // 检查新学号是否已被其他学生使用（排除自己）
        if (!student.getStuId().equals(originalStuId) &&
                studentService.isStuIdExists(student.getStuId(), originalStuId)) {
            errmsg.add(new MyError("该学号已存在"));
            model.addAttribute("errors", errmsg);
            model.addAttribute("stu", student);
            model.addAttribute("classes", classes);
            return "adm/updatestu";
        }

        // 使用包含原始ID的更新方法
        studentService.updateStudent(student, originalStuId);
        return "redirect:/adm/tostudmin/1";
    }
        else
        {
            for (ObjectError error:allErrors)
            {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors",errmsg);
            model.addAttribute("stu",student);
            model.addAttribute("classes",classes);
            return "adm/updatestu";
        }
    }

    // 根据ID查询学生
    @GetMapping(value = "/adm/selectById")
    public String selectById(@Param("stuId") String stuId, Model model)
    {
        Student student = studentService.selectById(stuId);
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);

        if (student == null) {
            model.addAttribute("errorMsg", "该学号不存在");
        } else {
            model.addAttribute("stus", student);
        }

        return "adm/stubyid";
    }

    //根据班级查询学生
    @GetMapping(value = "/adm/selectByClass/{pn}")
    public String selectStuByClass(@PathVariable("pn") Integer pn,@Param("className") String className,Model model)
    {
        PageHelper.startPage(pn, 6);
        List<Student> stus=studentService.seleStuByClassName(className);
        List<Classes> classes=classService.getAllClass();
        PageInfo<Student> page = new PageInfo<Student>(stus, 5);
        model.addAttribute("pageInfo",page);
        model.addAttribute("classes",classes);
        model.addAttribute("className",className);
        return "adm/stubyclass";
    }

    //    @GetMapping(value = "/adm/selectByClass/{pn}")
    //处理删除学生事务从根据班级查找页面发送来的
    @DeleteMapping(value = "/adm/stubyclass/{stuId}")
    public String delestubyclass(@PathVariable("stuId") String stuId)
    {
        Student student = studentService.selectById(stuId);
        studentService.deleStu(stuId);
        try {
            return "redirect:/adm/selectByClass/1?className="+ URLEncoder.encode(student.getStuClass(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:/adm/toclassdmin/1";
    }

    //返回学生修改页面从根据班级查找页面发送来的
    @GetMapping(value = "/adm/stubyclass/{stuId}")
    public String updateStuPagebyclass(@PathVariable("stuId") String stuId,Model model)
    {
        Student stu=studentService.selectById(stuId);
        List<Classes> classes=classService.getAllClass();
        model.addAttribute("stu",stu);
        model.addAttribute("classes",classes);
        model.addAttribute("ininclass",stu.getStuClass());
        return "adm/updatestubyclass";
    }

    //更新学生信息操作从根据班级查找页面发送来的
    @PutMapping(value = "/adm/stubyclass")
    public String updateStubyclass(@Valid Student student, BindingResult bindingResult, Model model,
                                   @RequestParam(value = "originalStuId", required = false) String originalStuId,
                                   @RequestParam("ininclass") String ininclass)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();
        List<Classes> classes = classService.getAllClass();

        // 添加密码非空验证
        if (student.getStuPass() == null || student.getStuPass().trim().isEmpty()) {
            errmsg.add(new MyError("密码不能为空"));
        }

        if(errmsg.size() == 0 && allErrors.size()==0)
        {
            // 获取原始学号
            String originalId = originalStuId != null ? originalStuId : student.getStuId();

            // 检查新学号是否已被其他学生使用（排除自己）
            // 检查新学号是否已被其他学生使用（排除自己）
            if (!student.getStuId().equals(originalId) &&
                    studentService.isStuIdExists(student.getStuId(), originalId)) {
                errmsg.add(new MyError("该学号已存在"));
                model.addAttribute("errors", errmsg);
                model.addAttribute("stu", student);
                model.addAttribute("classes", classes);
                model.addAttribute("ininclass", ininclass);
                return "adm/updatestubyclass";
            }

            // 直接更新学生信息
            studentService.updateStudent(student);
            try {
                return "redirect:/adm/selectByClass/1?className="+URLEncoder.encode(ininclass,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return "redirect:/adm/toclassdmin/1";
        }
        else
        {
            for (ObjectError error:allErrors)
            {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors",errmsg);
            model.addAttribute("stu",student);
            model.addAttribute("classes",classes);
            model.addAttribute("ininclass", ininclass);
            return "adm/updatestubyclass";
        }
    }

    //返回教师管理首页
    @GetMapping(value = "/adm/toteadmin/{pn}")
    public String toteadmin(@PathVariable("pn") Integer pn,Model model)
    {
        PageHelper.startPage(pn, 6);
        List<Teacher> teachers=teacherService.getAllTeacher();
        PageInfo<Teacher> page = new PageInfo<Teacher>(teachers, 5);
        model.addAttribute("pageInfo",page);
        return "adm/tealist";
    }


    //返回教师添加页面
    @GetMapping(value = "/adm/teaadd")
    public String teatoaddpage()
    {
        return "adm/addtea";
    }

    //处理教师添加事务
    @PostMapping(value = "/adm/teaAdd")
    public String teaAdd(@Valid Teacher teacher, BindingResult bindingResult, Model model)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();

        // 自定义验证：检查性别是否为空
        if (teacher.getTeaSex() == null || teacher.getTeaSex().trim().isEmpty()) {
            errmsg.add(new MyError("请选择性别"));
        }

        if(errmsg.size() == 0 && allErrors.size() == 0)
        {
            Teacher teacherVail = teacherService.selectById(teacher.getTeaId());
            if(teacherVail == null)
            {
                teacherService.addTeacher(teacher);
                return "redirect:/adm/toteadmin/1";
            }
            else{
                errmsg.add(new MyError("已存在该工号的教师"));
                model.addAttribute("errors", errmsg);
                model.addAttribute("tea", teacher);
                return "adm/addtea";
            }
        }
        else {
            // 将BindingResult中的错误信息也添加到errmsg中
            for (ObjectError error : allErrors) {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors", errmsg);
            model.addAttribute("tea", teacher);
            return "adm/addtea";
        }
    }


    //返回教师修改页面
    @GetMapping(value = "/adm/tea/{teaId}")
    public String updateTeaPage(@PathVariable("teaId") String teaId,Model model)
    {
        Teacher tea=teacherService.selectById(teaId);
        model.addAttribute("tea",tea);
        return "adm/upadtetea";
    }


    //更新教师信息操作
    @PutMapping(value = "/adm/tea")
    public String updateTea(@Valid Teacher teacher, BindingResult bindingResult, Model model,
                            @RequestParam("originalTeaId") String originalTeaId) // 确保这里接收参数
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();

        // 添加密码非空验证
        if (teacher.getTeaPass() == null || teacher.getTeaPass().trim().isEmpty()) {
            errmsg.add(new MyError("密码不能为空"));
        }

        if(errmsg.size() == 0 && allErrors.size()==0)
        {
            // 检查新工号是否已被其他教师使用（排除自己）
            if (!teacher.getTeaId().equals(originalTeaId) &&
                    teacherService.isTeaIdExists(teacher.getTeaId(), originalTeaId)) {
                errmsg.add(new MyError("该工号已存在"));
                model.addAttribute("errors", errmsg);
                model.addAttribute("tea", teacher);
                return "adm/upadtetea";
            }

            // 使用包含原始ID的更新方法
            teacherService.updateTeacher(teacher, originalTeaId);
            return "redirect:/adm/toteadmin/1";
        }
        else
        {
            for (ObjectError error:allErrors)
            {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors",errmsg);
            model.addAttribute("tea",teacher);
            return "adm/upadtetea";
        }
    }

    // 根据ID查询教师
    @GetMapping(value = "/adm/selectTeaById")
    public String selectTeaById(@Param("teaId") String teaId, Model model)
    {
        Teacher teacher = teacherService.selectById(teaId);

        if (teacher == null) {
            model.addAttribute("errorMsg", "该工号不存在");
        } else {
            model.addAttribute("tea", teacher);
        }

        return "adm/teabyid";
    }

    //处理删除教师事务
    @DeleteMapping(value = "/adm/tea/{teaId}")
    public String deletea(@PathVariable("teaId") String teaId)
    {
        teacherService.deleTea(teaId);
        return "redirect:/adm/toteadmin/1";
    }

    //返回班级管理首页
    @GetMapping(value = "/adm/toclassdmin/{pn}")
    public String toclassdmin(@PathVariable("pn") Integer pn,Model model)
    {
        PageHelper.startPage(pn, 6);
        List<Classes> classes=classService.getAllClass();
        PageInfo<Classes> page = new PageInfo<Classes>(classes, 5);
        model.addAttribute("pageInfo",page);
        return "adm/classlist";
    }

    //返回班级添加页面
    @GetMapping(value = "/adm/classadd")
    public String classToAddPage()
    {
        return "adm/addclass";
    }

    //处理班级添加事务
    @PostMapping(value = "/adm/classAdd")
    public String classAdd(@Valid Classes classes, BindingResult bindingResult,Model model)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();
        if(allErrors.size()==0)
        {
            if(classService.selectByName(classes.getClassName())==null) {
                Classes classVail = classService.selectById(classes.getClassId());
                if (classVail == null) {
                    classService.addClass(classes);
                    return "redirect:/adm/toclassdmin/1";
                } else {
                    errmsg.add(new MyError("已存在该班级号的班级"));
                    model.addAttribute("errors", errmsg);
                    model.addAttribute("class", classes);
                    return "adm/addclass";
                }
            }
            else
            {
                errmsg.add(new MyError("已存在该班级名字的班级"));
                model.addAttribute("errors", errmsg);
                model.addAttribute("class", classes);
                return "adm/addclass";
            }
        }
        else {
            for (ObjectError error:allErrors)
            {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors",errmsg);
            model.addAttribute("class",classes);
            return "adm/addclass";
        }
    }


    //返回班级修改页面
    @GetMapping(value = "/adm/class/{classId}")
    public String updateClassPage(@PathVariable("classId") String classId,Model model)
    {
        Classes classes=classService.selectById(classId);
        model.addAttribute("class",classes);
        return "adm/upadteclass";
    }


    //更新班级信息操作
    @PutMapping(value = "/adm/class")
    public String updateClass(@Valid Classes classes, BindingResult bindingResult, Model model,
                              @RequestParam(value = "originalClassId", required = false) String originalClassId)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();

        if(allErrors.size() == 0)
        {
            // 获取原始班级号
            String originalId = originalClassId != null ? originalClassId : classes.getClassId();

            // 检查新班级号是否已被其他班级使用（排除自己）
            if (!classes.getClassId().equals(originalId)) {
                Classes existingClassById = classService.selectById(classes.getClassId());
                if (existingClassById != null) {
                    errmsg.add(new MyError("该班级号已存在"));
                    model.addAttribute("errors", errmsg);
                    model.addAttribute("class", classes);
                    return "adm/upadteclass";
                }
            }

            // 检查新班级名是否已被其他班级使用（排除自己）
            Classes existingClassByName = classService.selectByName(classes.getClassName());
            if (existingClassByName != null && !existingClassByName.getClassId().equals(originalId)) {
                errmsg.add(new MyError("该班级名已存在"));
                model.addAttribute("errors", errmsg);
                model.addAttribute("class", classes);
                return "adm/upadteclass";
            }

            // 直接更新班级信息
            classService.updateClass(classes, originalId);
            return "redirect:/adm/toclassdmin/1";
        }
        else
        {
            for (ObjectError error : allErrors)
            {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors", errmsg);
            model.addAttribute("class", classes);
            return "adm/upadteclass";
        }
    }

    // 根据Name查询班级
    @GetMapping(value = "/adm/selectClassByName")
    public String selectClassById(@Param("className") String className, Model model)
    {
        Classes classes = classService.selectByName(className);

        if (classes == null) {
            model.addAttribute("errorMsg", "该班级不存在");
        } else {
            model.addAttribute("class", classes);
        }

        return "adm/classbyid";
    }


    //处理删除班级事务
    @DeleteMapping(value = "/adm/class/{classId}")
    public String deleClass(@PathVariable("classId") String classId)
    {
        classService.deleteById(classId);
        return "redirect:/adm/toclassdmin/1";
    }

}
