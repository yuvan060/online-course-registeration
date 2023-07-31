package com.online_course_registration.main.service;

import java.sql.SQLException;
import java.util.List;

import com.online_course_registration.main.entity.Course;
import com.online_course_registration.main.entity.User;

public interface CourseManagerUserService {

    boolean createUser(User user) throws SQLException, ClassNotFoundException;

    User signInUser(String email, String password) throws SQLException, ClassNotFoundException;

    void displayAllCourses() throws ClassNotFoundException;

    void registerUserForCourse(int userId, int courseId) throws SQLException, ClassNotFoundException;

    List<Course> getCoursesByUserId(int userId) throws SQLException, ClassNotFoundException;

}
