package com.lc.demo.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.demo.bean.*;
import com.lc.demo.service.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ResultssService resultssService;

    @Autowired
    private ClassService classService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SubjectService subjectService;


    @PostMapping(value = "/tea/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password, Map<String,Object> map, HttpSession session)
    {
        Teacher tea=teacherService.login(username,password);
        if(tea!=null)
        {
            session.setAttribute("loginUser",username);
            return "redirect:/teamain.html";
        }
        else
        {
            map.put("msg","用户名或密码错误");
            return  "login";
        }
    }

    //返回首页
    @GetMapping(value = "/tea/toindex")
    public String teaToIndex(){
        return "redirect:/teamain.html";
    }


    //返回教师信息修改页面
    @GetMapping(value = "/tea/toUpdateMsgPage")
    public String teaToUpdateMsgPage(HttpSession httpSession, Model model)
    {

        Teacher tea= teacherService.selectById((String) httpSession.getAttribute("loginUser"));
        model.addAttribute("tea",tea);
        return "tea/updatetea";
    }

    //更新教师信息操作
    @PutMapping(value = "/tea/updateTeaMsg")
    public String updateTeaMsg(@Valid Teacher teacher, BindingResult bindingResult, Model model, HttpSession httpSession)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();
        if(allErrors.size()==0)
        {
            Teacher teacherInit=teacherService.selectById((String) httpSession.getAttribute("loginUser"));
            teacher.setTeaId(teacherInit.getTeaId());
            teacher.setTeaName(teacherInit.getTeaName());
            teacher.setTeaSex(teacherInit.getTeaSex());

            teacherService.deleTea(teacherInit.getTeaId());
            teacherService.addTeacherHavePass(teacher);
            return "redirect:/updateTeaSucc.html";
        }
        else
        {
            for (ObjectError error:allErrors)
            {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors",errmsg);
            model.addAttribute("tea",teacher);
            return "tea/updatetea";
        }
    }

    //返回成绩管理首页
    @GetMapping(value = "/tea/toteadmin/{pn}")
    public String toteadmin(@PathVariable("pn") Integer pn, Model model)
    {
        PageHelper.startPage(pn, 6);
        List<Resultss> resultsses=resultssService.getAllResult();
        PageInfo<Resultss> page = new PageInfo<Resultss>(resultsses, 5);
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes",classes);
        model.addAttribute("pageInfo",page);
        return "tea/tearesultlist";
    }

    //返回成绩添加页面
    @GetMapping(value = "/tea/resadd")
    public String toResAddPage(Model model)
    {
        // 获取所有课程列表
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "tea/addResult";
    }

    // 处理成绩添加事务
    @PostMapping(value = "/tea/resAdd")
    public String resAdd(@Valid Resultss resultss, BindingResult bindingResult, Model model) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();
        Resultss resultssVail = null;

        // 自定义学期格式验证
        if (resultss.getResTerm() == null || resultss.getResTerm().trim().isEmpty()) {
            errmsg.add(new MyError("学期不能为空"));
        } else if (!resultss.getResTerm().matches("^\\d{2}-\\d{4}-\\d{1}$")) {
            errmsg.add(new MyError("学期格式应为\"年级-年份-学期\"，例如: 22-2023-1，且必须为数字"));
        }

        // 自定义课程名验证
        if (resultss.getSubName() == null || resultss.getSubName().trim().isEmpty()) {
            errmsg.add(new MyError("课程名不能为空"));
        }

        if (allErrors.size() == 0 && errmsg.size() == 0) {
            if (studentService.selectById(resultss.getStuId()) != null) {
                resultssVail = resultssService.selectResultByStuIdAndSubName(resultss.getStuId(), resultss.getSubName());
                if (resultssVail == null) {
                    resultssService.addResult(resultss);
                    return "redirect:/tea/toteadmin/1";
                } else {
                    errmsg.add(new MyError("已存在该学生的此成绩信息"));
                    model.addAttribute("errors", errmsg);
                    model.addAttribute("res", resultss);
                    return "tea/addResult";
                }
            } else {
                errmsg.add(new MyError("不存在该学号的学生"));
                model.addAttribute("errors", errmsg);
                model.addAttribute("res", resultss);
                return "tea/addResult";
            }
        } else {
            // 添加原有的验证错误
            for (ObjectError error : allErrors) {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors", errmsg);
            model.addAttribute("res", resultss);
            return "tea/addResult";
        }
    }

    //返回成绩修改页面
    @GetMapping(value = "/tea/res/{resId}")
    public String updateResPage(@PathVariable("resId") int resId, Model model)
    {
        Resultss resultss = resultssService.selectResultByResId(resId);
        // 获取所有课程列表
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        model.addAttribute("res", resultss);
        model.addAttribute("resId", resId);
        return "tea/updateResult";
    }

    //更新成绩信息操作
    @PutMapping(value = "/tea/res")
    public String updateRes(@Valid Resultss resultss, BindingResult bindingResult, Model model)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();

        // 自定义验证
        if (resultss.getResTerm() == null || resultss.getResTerm().trim().isEmpty()) {
            errmsg.add(new MyError("学期不能为空"));
        } else if (!resultss.getResTerm().matches("^\\d{2}-\\d{4}-\\d{1}$")) {
            errmsg.add(new MyError("学期格式应为\"年级-年份-学期\"，例如: 22-2023-1，且必须为数字"));
        }

        if (resultss.getSubName() == null || resultss.getSubName().trim().isEmpty()) {
            errmsg.add(new MyError("课程名不能为空"));
        }

        if (resultss.getStuId() == null || resultss.getStuId().trim().isEmpty()) {
            errmsg.add(new MyError("学号不能为空"));
        } else if (!resultss.getStuId().matches("\\d+")) {
            errmsg.add(new MyError("学号必须为数字"));
        }

        if (resultss.getResNum() < 0 || resultss.getResNum() > 100) {
            errmsg.add(new MyError("成绩必须在0-100之间"));
        }

        // 数据库验证
        if (errmsg.size() == 0 && allErrors.size() == 0) {
            // 验证学号是否存在
            if (studentService.selectById(resultss.getStuId()) == null) {
                errmsg.add(new MyError("不存在该学号的学生"));
            }
        }

        if(allErrors.size() == 0 && errmsg.size() == 0)
        {
            System.out.println(resultss);
            resultssService.deleteResultById(resultss.getResId());
            resultssService.addResult(resultss);
            return "redirect:/tea/toteadmin/1";
        }
        else
        {
            // 添加原有的验证错误
            for (ObjectError error : allErrors) {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors", errmsg);
            model.addAttribute("res", resultss);
            model.addAttribute("resId", resultss.getResId());
            return "tea/updateResult";
        }
    }

    //处理删除成绩信息事务
    @DeleteMapping(value = "/tea/res/{resId}")
    public String deleres(@PathVariable("resId") int stuId)
    {
        resultssService.deleteResultById(stuId);
        return "redirect:/tea/toteadmin/1";
    }

    //根据ID查询学生的成绩
    @GetMapping(value = "/tea/selectById/{pn}")
    public String selectResultByStuId(@PathVariable("pn") Integer pn, @Param("stuId") String stuId, Model model) {
        // 检查学号是否为空或格式不正确
        if (stuId == null || stuId.trim().isEmpty() || !stuId.matches("\\d+")) {
            model.addAttribute("error", "学号格式错误或不存在该学生");
            model.addAttribute("pageInfo", new PageInfo<Resultss>());
            return "tea/tearesultlistbystuid";
        }

        PageHelper.startPage(pn, 6);
        List<Resultss> resultsses = resultssService.selectByStuId(stuId);

        // 检查查询结果是否为空
        if (resultsses == null || resultsses.isEmpty()) {
            model.addAttribute("error", "学号格式错误或不存在该学生");
            model.addAttribute("pageInfo", new PageInfo<Resultss>());
            return "tea/tearesultlistbystuid";
        }

        PageInfo<Resultss> page = new PageInfo<Resultss>(resultsses, 5);
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);
        model.addAttribute("pageInfo", page);
        model.addAttribute("stuId", stuId);
        return "tea/tearesultlistbystuid";
    }

    //返回成绩修改页面从根据学生号查询的页面发送来的
    @GetMapping(value = "/tea/resbyid/{resId}")
    public String updateResPageById(@PathVariable("resId") int resId, Model model)
    {
        Resultss resultss = resultssService.selectResultByResId(resId);
        // 获取所有课程列表
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        model.addAttribute("res", resultss);
        model.addAttribute("resId", resId);
        return "tea/updateResultByid";
    }

    //更新成绩信息操作从根据学生号查询的页面发送来的
    @PutMapping(value = "/tea/resbyid")
    public String updateResById(@Valid Resultss resultss, BindingResult bindingResult, Model model)
    {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        List<MyError> errmsg = new ArrayList<>();

        // 自定义验证
        if (resultss.getResTerm() == null || resultss.getResTerm().trim().isEmpty()) {
            errmsg.add(new MyError("学期不能为空"));
        } else if (!resultss.getResTerm().matches("^\\d{2}-\\d{4}-\\d{1}$")) {
            errmsg.add(new MyError("学期格式应为\"年级-年份-学期\"，例如: 22-2023-1，且必须为数字"));
        }

        if (resultss.getSubName() == null || resultss.getSubName().trim().isEmpty()) {
            errmsg.add(new MyError("课程名不能为空"));
        }

        if (resultss.getStuId() == null || resultss.getStuId().trim().isEmpty()) {
            errmsg.add(new MyError("学号不能为空"));
        } else if (!resultss.getStuId().matches("\\d+")) {
            errmsg.add(new MyError("学号必须为数字"));
        }

        if (resultss.getResNum() < 0 || resultss.getResNum() > 100) {
            errmsg.add(new MyError("成绩必须在0-100之间"));
        }

        // 数据库验证
        if (errmsg.size() == 0 && allErrors.size() == 0) {
            // 验证学号是否存在
            if (studentService.selectById(resultss.getStuId()) == null) {
                errmsg.add(new MyError("不存在该学号的学生"));
            }
        }

        if(allErrors.size() == 0 && errmsg.size() == 0)
        {
            System.out.println(resultss);
            resultssService.deleteResultById(resultss.getResId());
            resultssService.addResult(resultss);
            return "redirect:/tea/selectById/1?stuId="+resultss.getStuId();
        }
        else
        {
            // 添加原有的验证错误
            for (ObjectError error : allErrors) {
                errmsg.add(new MyError(error.getDefaultMessage()));
            }
            model.addAttribute("errors", errmsg);
            model.addAttribute("res", resultss);
            model.addAttribute("resId", resultss.getResId());
            return "tea/updateResultByid";
        }
    }

    //处理删除成绩信息事务从根据学生号查询的页面发送来的
    @DeleteMapping(value = "/tea/resbyid/{resId}")
    public String deleresById(@PathVariable("resId") int resId)
    {
        Resultss resultss=resultssService.selectResultByResId(resId);
        resultssService.deleteResultById(resId);
        return "redirect:/tea/selectById/1?stuId="+resultss.getStuId();
    }

    //返回查询学生排名主页
    @GetMapping(value ="/tea/torank")
    public String torankpage()
    {
        return "tea/rank";
    }

    //处理查询学生排名事务
    @GetMapping(value = "/tea/selectRank")
    public String selectRank(@Param("resTerm") String resTerm, Model model)
    {
        List<MyError> errmsg = new ArrayList<>();

        // 学期格式验证 - 要求所有x都是数字
        if (resTerm == null || resTerm.trim().isEmpty()) {
            errmsg.add(new MyError("学期不能为空"));
            model.addAttribute("errors", errmsg);
            return "tea/rank";
        } else if (!resTerm.matches("^\\d{2}-\\d{4}-\\d{1}$")) {
            errmsg.add(new MyError("学期格式错误，学期格式应为\"年级-年份-学期\"，例如: 22-2023-1，且必须为数字"));
            model.addAttribute("errors", errmsg);
            return "tea/rank";
        }

        List<Rank> ranks=resultssService.selectRankByTerm(resTerm);

        // 检查查询结果是否为空
        if (ranks == null || ranks.isEmpty()) {
            errmsg.add(new MyError("该学期没有成绩"));
            model.addAttribute("errors", errmsg);
            model.addAttribute("resTerm", resTerm);
            return "tea/rank";
        }

        model.addAttribute("ranks",ranks);
        model.addAttribute("resTerm",resTerm);
        return "tea/ranklist";
    }

    // 返回根据班级查询学生排名主页
    @GetMapping(value ="/tea/torankbyclass")
    public String torankbyclasspage(Model model)
    {
        // 获取所有班级列表
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);
        return "tea/rankbyclass";
    }

    //处理根据班级查询学生排名事务
    @GetMapping(value = "/tea/selectRankbyclass")
    public String selectRankbyclass(@Param("resTerm") String resTerm, @Param("className") String className, Model model)
    {
        List<MyError> errmsg = new ArrayList<>();

        // 检查参数是否为空
        if(resTerm == null || resTerm.trim().isEmpty() || className == null || className.trim().isEmpty())
        {
            errmsg.add(new MyError("请输入学期信息以及班级信息"));
            model.addAttribute("errors", errmsg);
            // 获取所有班级列表
            List<Classes> classes = classService.getAllClass();
            model.addAttribute("classes", classes);
            return "tea/rankbyclass";
        }

        // 学期格式验证
        if (!resTerm.matches("^\\d{2}-\\d{4}-\\d{1}$")) {
            errmsg.add(new MyError("学期格式错误，学期格式应为\"年级-年份-学期\"，例如: 22-2023-1，且必须为数字"));
            model.addAttribute("errors", errmsg);
            // 获取所有班级列表
            List<Classes> classes = classService.getAllClass();
            model.addAttribute("classes", classes);
            model.addAttribute("resTerm", resTerm);
            model.addAttribute("className", className);
            return "tea/rankbyclass";
        }

        List<Rank> ranks = resultssService.selectRankByTermAndStuClass(resTerm, className);

        // 检查查询结果是否为空
        if (ranks == null || ranks.isEmpty()) {
            errmsg.add(new MyError("该学期没有成绩"));
            model.addAttribute("errors", errmsg);
            // 获取所有班级列表
            List<Classes> classes = classService.getAllClass();
            model.addAttribute("classes", classes);
            model.addAttribute("resTerm", resTerm);
            model.addAttribute("className", className);
            return "tea/rankbyclass";
        }

        model.addAttribute("ranks", ranks);
        model.addAttribute("resTerm", resTerm);
        model.addAttribute("className", className);
        // 获取所有班级列表
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);
        return "tea/ranklistbyclass";
    }

    // 返回各科成绩查询页面
    @GetMapping(value = "/tea/toSubjectRank")
    public String toSubjectRankPage(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "tea/subjectRank";
    }

    // 处理各科成绩查询
    @GetMapping(value = "/tea/subjectRank")
    public String getSubjectRank(@RequestParam("subName") String subName, Model model) {
        List<MyError> errmsg = new ArrayList<>();
        if (subName == null || subName.trim().isEmpty()) {
            errmsg.add(new MyError("请选择课程"));
            model.addAttribute("errors", errmsg);
            List<Subject> subjects = subjectService.getAllSubjects();
            model.addAttribute("subjects", subjects);
            return "tea/subjectRank";
        }

        List<Rank> subjectRanks = resultssService.selectRankBySubject(subName);
        if (subjectRanks == null || subjectRanks.isEmpty()) {
            errmsg.add(new MyError("该课程暂无成绩数据"));
            model.addAttribute("errors", errmsg);
            List<Subject> subjects = subjectService.getAllSubjects();
            model.addAttribute("subjects", subjects);
            return "tea/subjectRank";
        }

        // 计算平均分
        double avgScore = subjectRanks.stream()
                .mapToInt(Rank::getResNum)
                .average()
                .orElse(0.0);

        model.addAttribute("subjectRanks", subjectRanks);
        model.addAttribute("subName", subName);
        model.addAttribute("avgScore", String.format("%.2f", avgScore));
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "tea/subjectRank";
    }

    // 返回班级平均分查询页面
    @GetMapping(value = "/tea/toClassAvgScore")
    public String toClassAvgScorePage(Model model) {
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);
        return "tea/classAvgScore";
    }

    // 处理班级平均分查询
    @GetMapping(value = "/tea/classAvgScore")
    public String getClassAvgScore(@RequestParam("className") String className, Model model) {
        List<MyError> errmsg = new ArrayList<>();
        if (className == null || className.trim().isEmpty()) {
            errmsg.add(new MyError("请选择班级"));
            model.addAttribute("errors", errmsg);
            List<Classes> classes = classService.getAllClass();
            model.addAttribute("classes", classes);
            return "tea/classAvgScore";
        }

        List<ClassAvgScore> classAvgScores = resultssService.selectClassAvgScoreByClassName(className);
        if (classAvgScores == null || classAvgScores.isEmpty()) {
            errmsg.add(new MyError("该班级暂无成绩数据"));
            model.addAttribute("errors", errmsg);
            List<Classes> classes = classService.getAllClass();
            model.addAttribute("classes", classes);
            model.addAttribute("className", className);
            return "tea/classAvgScore";
        }

        double totalAvgScore = resultssService.selectTotalAvgScoreByClassName(className);

        model.addAttribute("classAvgScores", classAvgScores);
        model.addAttribute("className", className);
        model.addAttribute("totalAvgScore", totalAvgScore);
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);
        return "tea/classAvgScore";
    }

    // 返回成绩分布查询页面
    @GetMapping(value = "/tea/toScoreDistribution")
    public String toScoreDistributionPage(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "tea/scoreDistribution";
    }

    // 处理成绩分布查询
    @GetMapping(value = "/tea/scoreDistribution")
    public String getScoreDistribution(@RequestParam("subName") String subName, Model model) {
        List<MyError> errmsg = new ArrayList<>();
        if (subName == null || subName.trim().isEmpty()) {
            errmsg.add(new MyError("请选择课程"));
            model.addAttribute("errors", errmsg);
            List<Subject> subjects = subjectService.getAllSubjects();
            model.addAttribute("subjects", subjects);
            return "tea/scoreDistribution";
        }

        ScoreDistribution scoreDistribution = resultssService.getScoreDistribution(subName);
        if (scoreDistribution == null) {
            errmsg.add(new MyError("该课程暂无成绩数据"));
            model.addAttribute("errors", errmsg);
            List<Subject> subjects = subjectService.getAllSubjects();
            model.addAttribute("subjects", subjects);
            model.addAttribute("subName", subName);
            return "tea/scoreDistribution";
        }

        model.addAttribute("scoreDistribution", scoreDistribution);
        model.addAttribute("subName", subName);
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "tea/scoreDistribution";
    }



    // 返回班级各科平均分图表页面
    @GetMapping(value = "/tea/toClassSubjectAvgChart")
    public String toClassSubjectAvgChartPage(Model model) {
        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);
        return "tea/classSubjectAvgChart";
    }

    // 处理班级各科平均分图表查询
    @GetMapping(value = "/tea/classSubjectAvgChart")
    public String getClassSubjectAvgChart(@RequestParam("className") String className, Model model) {
        List<MyError> errmsg = new ArrayList<>();
        if (className == null || className.trim().isEmpty()) {
            errmsg.add(new MyError("请选择班级"));
            model.addAttribute("errors", errmsg);
            List<Classes> classes = classService.getAllClass();
            model.addAttribute("classes", classes);
            return "tea/classSubjectAvgChart";
        }

        List<ClassSubjectAvg> classSubjectAvgs = resultssService.getClassSubjectAvgScores(className);
        if (classSubjectAvgs == null || classSubjectAvgs.isEmpty()) {
            errmsg.add(new MyError("该班级暂无成绩数据"));
            model.addAttribute("errors", errmsg);
            List<Classes> classes = classService.getAllClass();
            model.addAttribute("classes", classes);
            model.addAttribute("className", className);
            return "tea/classSubjectAvgChart";
        }

        model.addAttribute("classSubjectAvgs", classSubjectAvgs);
        model.addAttribute("className", className);

        // 计算统计信息
        double totalAvgScore = classSubjectAvgs.stream()
                .mapToDouble(ClassSubjectAvg::getAvgScore)
                .average()
                .orElse(0.0);

        // 找到最高分和最低分的科目
        ClassSubjectAvg highestAvg = classSubjectAvgs.stream()
                .max((a, b) -> Double.compare(a.getAvgScore(), b.getAvgScore()))
                .orElse(null);

        ClassSubjectAvg lowestAvg = classSubjectAvgs.stream()
                .min((a, b) -> Double.compare(a.getAvgScore(), b.getAvgScore()))
                .orElse(null);

        model.addAttribute("totalAvgScore", totalAvgScore);
        model.addAttribute("subjectCount", classSubjectAvgs.size());
        model.addAttribute("highestAvgSubject", highestAvg != null ? highestAvg.getSubName() : "无");
        model.addAttribute("highestAvgScore", highestAvg != null ? highestAvg.getAvgScore() : 0);
        model.addAttribute("lowestAvgSubject", lowestAvg != null ? lowestAvg.getSubName() : "无");
        model.addAttribute("lowestAvgScore", lowestAvg != null ? lowestAvg.getAvgScore() : 0);

        List<Classes> classes = classService.getAllClass();
        model.addAttribute("classes", classes);

        return "tea/classSubjectAvgChart";
    }

}
