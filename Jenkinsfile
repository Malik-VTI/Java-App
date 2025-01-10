pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                // Ambil kode dari GitHub
                git branch: 'main', url: 'git@github.com:Malik-VTI/Java-App.git'
            }
        }
        stage('Build') {
            steps {
                // Build aplikasi (contoh menggunakan Maven)
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                // Jalankan unit test
                sh 'mvn test'
            }
        }
        stage('Package') {
            steps {
                // Pindahkan hasil build ke direktori aplikasi
                sh '''
                    mkdir -p /opt/sample-application
                    cp target/ecommerce-app-0.0.1-SNAPSHOT.jar /opt/sample-application/
                '''
            }
        }
        stage('Restart Application') {
            steps {
                // Restart aplikasi menggunakan systemctl
                sh '''
                    echo "M@B#26L2KUns" | sudo -S systemctl restart sample-application.service
                '''
            }
        }
    }
}
