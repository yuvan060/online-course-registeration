package com.online_course_registration.main.entity;

public class Course {
	private int id;
	private String title;
    private String instructor;
    private String description;
    private int enrolledStudents;
    private int capacity;
    private int adminId;

    public Course(String title, String instructor, String description, int enrolledStudents, int capacity, int adminId) {
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.enrolledStudents = enrolledStudents;
        this.capacity = capacity;
        this.adminId = adminId;
    }

    
    public Course() {
	}

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    
    public String getInstructor() {
		return instructor;
	}


	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public int getEnrolledStudents() {
		return enrolledStudents;
	}


	public void setEnrolledStudents(int enrolledStudents) {
		this.enrolledStudents = enrolledStudents;
	}


	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}


	public int getAdminId() {
		return adminId;
	}


	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}


	@Override
    public String toString() {
        return "Course_Id: " + id +
        		"\nTitle: " + title +
                "\nInstructor: " + instructor +
                "\nDescription: " + description;
    }
}
