package com.lc.demo.service.impl;

import com.lc.demo.bean.Subject;
import com.lc.demo.mapper.SubjectMapper;
import com.lc.demo.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectMapper subjectMapper;

    @Override
    public List<Subject> getAllSubjects() {
        return subjectMapper.selectAllSubjects();
    }

    @Override
    public Subject getSubjectById(String subId) {
        return subjectMapper.selectSubjectById(subId);
    }

    @Override
    public List<Subject> getSubjectsByName(String subName) {
        return subjectMapper.selectSubjectsByName(subName);
    }

    @Override
    public List<Subject> getSubjectsByTerm(String subTerm) {
        return subjectMapper.selectSubjectsByTerm(subTerm);
    }
}