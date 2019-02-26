package com.syhdoctor.webserver.service.lecturer;

import com.syhdoctor.webserver.base.controller.BaseController;
import com.syhdoctor.webserver.mapper.lecturer.LecturerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LecturerBaseService extends BaseController {

    @Autowired
    private LecturerMapper lecturerMapper;


    public boolean addUpdateLecture(int id, String name, String photo, String phone, String titleName, String hospital, String department, String expertise, String abstracts, long doctorId) {
        if (id > 0) {
            return lecturerMapper.updateLecture(id, name, photo, phone, titleName, hospital, department, expertise, abstracts, doctorId);
        } else {
            return lecturerMapper.addLecture(name, photo, phone, titleName, hospital, department, expertise, abstracts, doctorId);
        }
    }

    public List<Map<String, Object>> getLecturerInfoList(String name, String phone, int pageIndex, int pageSize) {
        return lecturerMapper.getLecturerInfoList(name, phone, pageIndex, pageSize);
    }

    public Map<String, Object> getLecturerInfoById(int id) {
        return lecturerMapper.getLecturerInfoById(id);
    }

    public long getLecturerInfoTotal(String name, String phone) {
        return lecturerMapper.getLecturerInfoTotal(name, phone);
    }


    public Map<String, Object> getLecturerByDoctor(long doctorid) {
        return lecturerMapper.getLecturerByDoctor(doctorid);
    }

    public Map<String, Object> getLecturer(long id) {
        return lecturerMapper.getLecturer(id);
    }

    public Map<String, Object> getLecturer(String phone) {
        return lecturerMapper.getLecturer(phone);
    }
    public Map<String, Object> getDoctorByPhone(String phone) {
        return lecturerMapper.getDoctorByPhone(phone);
    }

    public boolean updateLecturer(String name, String photo, String phone, String title, String hospital, String department, String expertise, String abstracts, long userId, long doctorId) {
        return lecturerMapper.updateLecturer(name, photo, phone, title, hospital, department, expertise, abstracts, userId, doctorId);
    }
}
