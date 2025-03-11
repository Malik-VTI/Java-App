pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                // Ambil kode dari GitHub
                git branch: 'main', url: 'https://github.com/Malik-VTI/Java-App.git'
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
                    sudo mkdir -p /opt/product-management
                    sudo cp target/ecommerce-app-0.0.1-SNAPSHOT.jar /opt/product-management/
                '''
            }
        }
        stage('Restart Application') {
            steps {
                // Restart aplikasi menggunakan systemctl
                sh '''
                    echo "P@ssw0rd" | sudo -S systemctl restart product-management.service
                '''
            }
        }
    }
}
