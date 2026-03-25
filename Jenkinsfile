pipeline {
    agent any

    environment {
        JAVA_HOME = "C:\\Users\\Atharv Jaiswal\\Downloads\\jdk-21_windows-x64_bin\\jdk-21.0.10"
        PATH = "${JAVA_HOME}\\bin;${env.PATH}"
    }

    stages {

        stage('Clone Repository') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/AtharvJaiswal5/student_Feedback_form-test.git'
            }
        }

        stage('Compile Java') {
            steps {
                bat '''
                javac -version
                javac -cp ".;lib\\*" -d bin src\\com\\feedback\\tests\\FeedbackTest.java
                '''
            }
        }

        stage('Run Selenium Tests') {
            steps {
                // Tests run headless — no display or ChromeDriver install needed.
                // Selenium Manager (bundled in lib) auto-downloads the correct chromedriver.
                bat '''
                java -version
                java -cp ".;lib\\*;bin" com.feedback.tests.FeedbackTest
                '''
            }
        }

    }

    post {
        success {
            echo 'Build SUCCESS - All Selenium Tests Passed'
        }
        failure {
            echo 'Build FAILED - Check Console Output'
        }
    }
}
