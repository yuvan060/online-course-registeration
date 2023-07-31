package com.online_course_registration.main;

import com.online_course_registration.main.service.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.online_course_registration.main.entity.Admin;
import com.online_course_registration.main.entity.Course;
import com.online_course_registration.main.entity.User;

public class CourseManager {
    private static final Scanner scanner = new Scanner(System.in);
    
    private static CourseManagerAdmin courseManagerAdmin = new CourseManagerAdmin(); 
    private static CourseManagerUser courseManagerUser = new CourseManagerUser(); 
    
    private static User loggedInUser; 
    private static Admin loggedInAdmin; 

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("Welcome to Online Course Registration System!");
        int choice;
        do {
            System.out.println("\nSelect an option:");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    handleAdmin();
                    break;
                case 2:
                    handleUser();
                    break;
                case 3:
                    System.out.println("Exiting... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    private static void handleAdmin() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        int adminChoice;
        do {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Register Admin");
            System.out.println("2. Admin Sign In");
            System.out.println("3. Add Course");
            System.out.println("4. Edit Course");
            System.out.println("5. View All Courses");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            adminChoice = scanner.nextInt();
            scanner.nextLine();

            switch (adminChoice) {
                case 1:
                    registerAdmin();
                    break;
                case 2:
                    loggedInAdmin = adminSignIn();
                    break;
                case 3:
                    addCourse();
                    break;
                case 4:
                    editCourse();
                    break;
                case 5:
                    viewAllCourses();
                    break;
                case 6:
                    logOut(loggedInAdmin);
                    System.out.println("Exiting Admin Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (adminChoice != 6);
    }

    private static void handleUser() throws ClassNotFoundException, SQLException {
        int userChoice;
        do {
            System.out.println("\nUser Menu:");
            System.out.println("1. Register User");
            System.out.println("2. User Sign In");
            System.out.println("3. View Registered Courses");
            System.out.println("4. Register for Course");
            System.out.println("5. Withdraw from Course");
            System.out.println("6. View All Available Courses");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            userChoice = scanner.nextInt();
            scanner.nextLine();

            switch (userChoice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loggedInUser = userSignIn();
                    break;
                case 3:
                    viewUserCourses();
                    break;
                case 4:
                    userRegisterForCourse();
                    break;
                case 5:
                    userWithdrawCourse();
                    break;
                case 6:
                    viewAllCoursesForUser();
                    break;
                case 7:
                    logOut(loggedInUser);
                    System.out.println("Exiting User Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (userChoice != 7);
    }
    
    
	private static void registerAdmin() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("\nRegistering a new Admin...");
        System.out.print("Enter Admin Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Admin Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        Admin admin = new Admin(0, name, email, password);
        if (courseManagerAdmin.createAdmin(admin)) {
		    System.out.println("Admin registration successful!");
		} else {
		    System.out.println("Admin registration failed.");
		}
    }

    private static Admin adminSignIn() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("\nAdmin Sign In...");
        System.out.print("Enter Admin Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        Admin admin = courseManagerAdmin.signInAdmin(email, password);
        if (admin != null) {
            System.out.println("Admin Sign In Successful!");
            return admin;
        } else {
            System.out.println("Admin Sign In Failed. Invalid credentials.");
            return null;
        }
    }

    private static void addCourse() throws ClassNotFoundException {
        if (loggedInAdmin == null) {
            System.out.println("Please sign in as an admin first.");
            return;
        }

        System.out.println("\nAdding a new Course...");
        System.out.print("Enter Course Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Course Instructor: ");
        String instructor = scanner.nextLine();
        System.out.print("Enter Course Description: ");
        String description = scanner.nextLine();
        System.out.print("Enter Enrolled Students: ");
        int enrolledStudents = scanner.nextInt();
        System.out.print("Enter Course Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine(); 
        int adminId = loggedInAdmin.getId();

        Course course = new Course(title, instructor, description, enrolledStudents, capacity, adminId);
        courseManagerAdmin.createCourse(course);
    }

    private static void viewAllCourses() throws ClassNotFoundException {
    	if (loggedInAdmin == null) {
            System.out.println("Please sign in as an admin first.");
            return;
        }
        System.out.println("\nViewing all Courses...");
        courseManagerAdmin.displayCoursesOfAdmin(loggedInAdmin.getId());
    }

    private static void viewAllCoursesForUser() throws ClassNotFoundException {
    	System.out.println("\nViewing all Courses...");
    	courseManagerUser.displayAllCourses();
    }

    private static void registerUser() throws ClassNotFoundException {
        System.out.println("\nRegistering a new User...");
        System.out.print("Enter User Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter User Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter User Password: ");
        String password = scanner.nextLine();

        User user = new User(0, name, email, password);
        if (courseManagerUser.createUser(user)) {
		    System.out.println("User registration successful!");
		} else {
		    System.out.println("User registration failed.");
		}
    }

    private static User userSignIn() throws SQLException, ClassNotFoundException {
        System.out.println("\nUser Sign In...");
        System.out.print("Enter User Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter User Password: ");
        String password = scanner.nextLine();

        User user = courseManagerUser.signInUser(email, password);
        if (user != null) {
            System.out.println("User Sign In Successful!");
            return user;
        } else {
            System.out.println("User Sign In Failed. Invalid credentials.");
            return null;
        }
    }

    
    private static void editCourse() throws ClassNotFoundException {
        if (loggedInAdmin == null) {
            System.out.println("Please sign in as an admin first.");
            return;
        }

        System.out.println("\nEditing a Course...");
        viewAllCourses();
        System.out.print("Enter the Course ID to edit: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter the updated course details:");
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Instructor: ");
        String instructor = scanner.nextLine();
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Capacity: ");
        int capacity = scanner.nextInt();

        Course updatedCourse = new Course( title, instructor, description,0, capacity,loggedInAdmin.getId());
        courseManagerAdmin.editCourse(courseId, updatedCourse);
    }


    
    private static void userRegisterForCourse() throws ClassNotFoundException {
        if (loggedInUser == null) {
            System.out.println("Please sign in as a user first.");
            return;
        }

        System.out.println("\nUser Course Registration...");
        System.out.print("Enter Course ID to Register: ");
        int courseId = scanner.nextInt();
        scanner.nextLine(); 

        courseManagerUser.registerUserForCourse(loggedInUser.getId(), courseId);
    }
    
    private static void userWithdrawCourse() throws ClassNotFoundException {
        if (loggedInUser == null) {
            System.out.println("Please sign in as a user first.");
            return;
        }

        System.out.println("\nWithdrawing from a Course...");
        viewUserCourses();
        System.out.print("Enter the Course ID to withdraw from: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();

        courseManagerUser.withdrawCourse(loggedInUser.getId(), courseId);
    }

    private static void viewUserCourses() throws ClassNotFoundException {
        if (loggedInUser == null) {
            System.out.println("Please sign in as a user first.");
            return;
        }

        System.out.println("\nViewing registered courses for User: " + loggedInUser.getName());
        List<Course> userCourses = courseManagerUser.getCoursesByUserId(loggedInUser.getId());
		for (Course course : userCourses) {
		    System.out.println(course + "\n");
		}
    }
    
    private static void logOut(Admin admin) {
    	loggedInAdmin = null;
	}
    
    private static void logOut(User user) {
    	loggedInUser = null;
	}

}
