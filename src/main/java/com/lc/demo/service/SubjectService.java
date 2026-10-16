package com.lc.demo.service;

import com.lc.demo.bean.Subject;
import java.util.List;

public interface SubjectService {

    List<Subject> getAllSubjects();

    Subject getSubjectById(String subId);

    List<Subject> getSubjectsByName(String subName);

    List<Subject> getSubjectsByTerm(String subTerm);
}