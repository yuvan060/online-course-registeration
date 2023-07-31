package com.online_course_registration.main.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.online_course_registration.main.entity.Admin;
import com.online_course_registration.main.entity.Course;

public class CourseManagerAdmin implements CourseManagerAdminService {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/courses_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@iamYUVAN060";
    private Connection connection = null;

    private void connectToDatabase() throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        }
    }

    private void closeDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error while closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean createAdmin(Admin admin) {
        try {
            connectToDatabase();
            PreparedStatement createAdminStatement = connection.prepareStatement(
                    "INSERT INTO admin (id, name, email, password) VALUES (?, ?, ?, ?)"
            );
            createAdminStatement.setInt(1, admin.getId());
            createAdminStatement.setString(2, admin.getName());
            createAdminStatement.setString(3, admin.getEmail());
            createAdminStatement.setString(4, admin.getPassword());

            createAdminStatement.executeUpdate();

            System.out.println("Admin added into the database: " + admin.getName());
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while creating admin: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeDatabaseConnection();
        }
    }

    @Override
    public Admin signInAdmin(String email, String password) {
        try {
            connectToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM admin WHERE email = ? AND password = ?"
            );
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            Admin admin = null;

            if (resultSet.next()) {
                int adminId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                admin = new Admin(adminId, name, email, password);
            }

            resultSet.close();
            preparedStatement.close();
            return admin;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while signing in admin: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeDatabaseConnection();
        }
    }

    @Override
    public void createCourse(Course course) {
        try {
            connectToDatabase();
            PreparedStatement createCourseStatement = connection.prepareStatement(
                    "INSERT INTO courses (title, instructor, description, enrolled_students, capacity, admin_id) " +
                            "VALUES (?, ?, ?, ?, ?, ?)"
            );
            createCourseStatement.setString(1, course.getTitle());
            createCourseStatement.setString(2, course.getInstructor());
            createCourseStatement.setString(3, course.getDescription());
            createCourseStatement.setInt(4, course.getEnrolledStudents());
            createCourseStatement.setInt(5, course.getCapacity());
            createCourseStatement.setInt(6, course.getAdminId());

            createCourseStatement.executeUpdate();

            System.out.println("Course added into the database: " + course.getTitle());
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while creating course: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseConnection();
        }
    }
    
    public  void editCourse(int courseId, Course updatedCourse) {
        try {
        	connectToDatabase();
            String query = "UPDATE courses SET title = ?, instructor = ?, description = ?, capacity = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, updatedCourse.getTitle());
            preparedStatement.setString(2, updatedCourse.getInstructor());
            preparedStatement.setString(3, updatedCourse.getDescription());
            preparedStatement.setInt(4, updatedCourse.getCapacity());
            preparedStatement.setInt(5, courseId);

            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (rowsAffected > 0) {
                System.out.println("Course with ID " + courseId + " has been updated successfully.");
            } else {
                System.out.println("Course with ID " + courseId + " not found or no changes were made.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while updating course: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseConnection();
        }
    }

    
    @Override
    public void displayCoursesOfAdmin(int adminId) {
        try {
            connectToDatabase();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT * FROM courses WHERE admin_id = ?"
            );
            preparedStatement.setInt(1, adminId);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Course> courses = new ArrayList<>();
            while (resultSet.next()) {
                Course course = new Course(
                        resultSet.getString("title"),
                        resultSet.getString("instructor"),
                        resultSet.getString("description"),
                        resultSet.getInt("enrolled_students"),
                        resultSet.getInt("capacity"),
                        resultSet.getInt("admin_id")
                );
                course.setId(resultSet.getInt("id"));
                courses.add(course);
            }

            if (courses.isEmpty()) {
                System.out.println("\nNo Courses Available");
                return;
            }

            System.out.println("\nAll Courses:");
            System.out.println("---------------------------------------");
            for (Course course : courses) {
                System.out.println(course + "\n");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while retrieving courses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseConnection();
        }
    }
}
