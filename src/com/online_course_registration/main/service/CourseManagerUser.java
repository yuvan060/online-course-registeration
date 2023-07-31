package com.online_course_registration.main.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.online_course_registration.main.entity.Course;
import com.online_course_registration.main.entity.User;

public class CourseManagerUser implements CourseManagerUserService {

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
    public boolean createUser(User user) {
        try {
            connectToDatabase();
            String query = "INSERT INTO user (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPassword());

            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while creating user: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeDatabaseConnection();
        }
    }

    @Override
    public User signInUser(String email, String password) {
        try {
            connectToDatabase();
            String query = "SELECT * FROM user WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;

            if (resultSet.next()) {
                int userId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                user = new User(userId, name, email, password);
            }

            resultSet.close();
            preparedStatement.close();

            return user;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while signing in user: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeDatabaseConnection();
        }
    }

    @Override
    public void displayAllCourses() {
        try {
            connectToDatabase();
            List<Course> allCourses = getAllCourses();

            System.out.println("\nAll Courses:");
            System.out.println("---------------------------------------");
            for (Course course : allCourses) {
                System.out.println(course.toString() + "\n");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while fetching all courses: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseConnection();
        }
    }

    private List<Course> getAllCourses() throws SQLException {
        List<Course> allCourses = new ArrayList<>();

        String query = "SELECT * FROM courses";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            Course course = new Course(
                    resultSet.getString("title"),
                    resultSet.getString("instructor"),
                    resultSet.getString("description"),
                    0,
                    resultSet.getInt("capacity"),
                    0
            );
            course.setId(resultSet.getInt("id"));
            allCourses.add(course);
        }

        resultSet.close();
        statement.close();

        return allCourses;
    }

    @Override
    public void registerUserForCourse(int userId, int courseId) {
        try {
            connectToDatabase();

            if (isUserRegisteredForCourse(userId, courseId)) {
                System.out.println("User is already registered for this course.");
                return;
            }

            if (isFull(courseId)) {
                System.out.println("Course capacity exceeded. Registration not possible.");
                return;
            }

            String query = "INSERT INTO user_courses (user_id, course_id) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, courseId);

            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("UPDATE courses SET enrolled_students = enrolled_students + 1 WHERE id = ? ");
            preparedStatement.setInt(1, courseId);

            preparedStatement.executeUpdate();

            System.out.println("User successfully registered for the course.");
            preparedStatement.close();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while registering user for the course: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseConnection();
        }
    }

    @Override
    public List<Course> getCoursesByUserId(int userId) {
        try {
            connectToDatabase();
            List<Course> userCourses = new ArrayList<>();
            String query = "SELECT * FROM courses JOIN user_courses ON courses.id = user_courses.course_id WHERE user_courses.user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Course course = new Course();
                course.setTitle(resultSet.getString("title"));
                course.setInstructor(resultSet.getString("instructor"));
                course.setDescription(resultSet.getString("description"));
                course.setId(resultSet.getInt("id"));
                userCourses.add(course);
            }

            resultSet.close();
            preparedStatement.close();

            return userCourses;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while fetching user courses: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            closeDatabaseConnection();
        }
    }

    private boolean isUserRegisteredForCourse(int userId, int courseId) throws SQLException {
        String query = "SELECT * FROM user_courses WHERE user_id = ? AND course_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, userId);
        preparedStatement.setInt(2, courseId);

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isRegistered = resultSet.next();

        resultSet.close();
        preparedStatement.close();

        return isRegistered;
    }

    private boolean isFull(int courseId) throws SQLException {
        String query = "SELECT enrolled_students, capacity FROM courses WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, courseId);

        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isFull = false;
        if (resultSet.next()) {
            isFull = resultSet.getInt("enrolled_students") >= resultSet.getInt("capacity");
        }

        resultSet.close();
        preparedStatement.close();

        return isFull;
    }

    public void withdrawCourse(int userId, int courseId) {
        try {
            connectToDatabase();
            String query = "DELETE FROM user_courses WHERE user_id = ? AND course_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, courseId);

            int rowsAffected = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (rowsAffected > 0) {
                System.out.println("User has successfully withdrawn from the course.");
                PreparedStatement updateCourseStatement = connection.prepareStatement(
                        "UPDATE courses SET enrolled_students = enrolled_students - 1 WHERE id = ?");
                updateCourseStatement.setInt(1, courseId);
                updateCourseStatement.executeUpdate();
                updateCourseStatement.close();
            } else {
                System.out.println("User is not registered for the course.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error while withdrawing course: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseConnection();
        }
    }
}
