package com.online_course_registration.main.service;


import java.sql.SQLException;

import com.online_course_registration.main.entity.Admin;
import com.online_course_registration.main.entity.Course;

public interface CourseManagerAdminService {

    boolean createAdmin(Admin admin) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException;

    Admin signInAdmin(String email, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

    void createCourse(Course course) throws SQLException, ClassNotFoundException;

    void displayCoursesOfAdmin(int adminId) throws SQLException, ClassNotFoundException;

}

