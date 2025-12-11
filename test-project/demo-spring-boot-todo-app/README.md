# Awesome To-Do App

## Overview

**Awesome To-Do App** is a versatile task management application tailored to assist users in organizing tasks, managing deadlines, and prioritizing activities. The app offers an intuitive user interface complete with a spectrum of features including task creation, deletion, pagination, and advanced error handling.

In this README, you'll find a comprehensive guide that details your project, enumerates its key functionalities, lists the technologies employed, provides installation instructions, and articulates usage instructions.

![Tasks Empty](https://github.com/adampeer/spring-boot-todo-app/assets/90769663/aed896df-0c77-4fe2-845a-e12460ea5b2b)

![Tasks Full](https://github.com/adampeer/spring-boot-todo-app/assets/90769663/3bac6e08-6e5a-4c2c-a69a-520c5a8ff4ec)

## Features

### Task Management

- Create and manage tasks with essential details such as titles, descriptions, and due dates.
- Tasks are elegantly presented in card format, enhancing visibility and comprehension.
- Effortlessly delete tasks with permanent removal from the application.

### Pagination

- Enhance user experience by paginating tasks, ensuring a clutter-free view.
- Navigate seamlessly through the task list with "Previous" and "Next" buttons for effortless organization.

### Error Handling

- Robust error handling, encompassing gracefully displayed custom error pages and user-friendly messages.
- Guard against requests that seek pages beyond the total available count, offering a polished and secure user experience.

### Advanced Features

- Responsive design adapting to diverse devices, guaranteeing a harmonious experience on any platform.
- Intuitive pop-up modals for confirming task deletion, enriching user interaction.

## Technologies Used

**Frontend:**

- HTML
- Thymeleaf (for server-side rendering)
- JavaScript
- jQuery
- Bootstrap (for styling and modals)

**Backend:**

- Spring Boot (Java-based framework)
- Spring MVC
- Spring Data JPA (for database access)
- MySQL (as the database)

## Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/adampeer/spring-boot-todo-app.git
   cd awesome-todo-app
   ```

2. **Database Configuration:**

   - Install MySQL and create a database.
   - Update the `application.properties` file with your database jdtLSConfiguration such as username, password, database name and port number.

3. **Build and Run the Application:**

   ```bash
   ./mvnw clean package
   java -jar target/awesome-todo-app-0.1.jar
   ```

4. **Access the Application:**

   Open a web browser and go to `http://localhost:8080` or whatever port you've set in application.properties file.

## Usage

1. **Create a Task:**

   - Fill out the task creation form, providing a title, description, and due date.
   - Click the "Create Task" button.

2. **Pagination:**

   - Use the "Previous" and "Next" buttons to navigate through your task list.
   - Each page typically displays 6 tasks.

3. **Delete a Task:**

   - Each task card includes a "Delete" button.
   - Click the "Delete" button to trigger a confirmation modal.
   - Confirm the task deletion by clicking "Yes" in the modal.

4. **Error Handling:**

   - Error pages and messages are displayed for various error scenarios.
   - Friendly error messages are shown to users.

5. **Advanced Features:**

   - Responsive design ensures a seamless experience on different devices.
   - Confirmation modal for task deletion adds a layer of user interaction.

## Feedback and Support

We welcome your feedback and suggestions. If you encounter any issues or have ideas for improvements, please open an issue on our GitHub repository.

## License

This project is licensed under the MIT License. Feel free to use it, modify it, and share it as you see fit.

## Author

- [Adam Peer](https://github.com/adampeer)

---
