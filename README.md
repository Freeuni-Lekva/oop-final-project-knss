# Ikodave

A Java web application for practicing coding problems and algorithms. Built with Java 17, Maven, MySQL, and Docker for secure code execution.

## Features

- **Coding Problems**: Practice algorithmic challenges with varying difficulty levels
- **Code Execution**: Submit and run code in Java, Python, C, and C++ using Docker containers
- **User System**: Registration, login, and email verification
- **Progress Tracking**: View submission history and problem status
- **Leaderboard**: Competitive rankings based on problem solving
- **Admin Panel**: Manage problems and users
- **User Profiles**: Track personal statistics and achievements

## Tech Stack

- **Backend**: Java 17, Maven, MySQL 8.0
- **Web Server**: Apache Tomcat 9
- **Code Execution**: Docker containers
- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap 5
- **Testing**: JUnit 5

## Quick Start

1. **Clone and setup database**
   ```bash
   git clone <repository-url>
   cd ikodave
   mysql -u root -p < src/main/java/com/example/sql/database.sql
   ```

2. **Configure database connection**
   Update `src/main/java/com/example/constants/DBConnectionConstants.java` with your MySQL credentials.

3. **Build and run**
   ```bash
   mvn clean install
   mvn tomcat9:run
   ```

4. **Access the application**
   Open `http://localhost:8081` in your browser.

## Project Structure

```
ikodave/
├── src/main/java/com/example/
│   ├── admin/          # Admin panel functionality
│   ├── problems/       # Problem management and filtering
│   ├── submissions/    # Code execution and submission handling
│   ├── registration/   # User authentication and verification
│   ├── leaderboard/    # Rankings and scoring
│   ├── user_profile/   # User statistics and profiles
│   └── sql/           # Database scripts
├── src/main/webapp/
│   ├── static/        # Frontend assets (HTML, CSS, JS)
│   └── WEB-INF/       # Web configuration
└── src/test/          # Unit tests
```

## Authors

**K.N.S.S. Team**
- Konstantine Endeladze
- Nickolas Metreveli  
- Sandro Bolkvadze
- Saba Losaberidze

## License

MIT License
