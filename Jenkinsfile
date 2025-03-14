pipeline {
    agent any
    environment {
        APP_NAME = "ecommerce-app-0.0.1-SNAPSHOT.jar"
        APP_DIR = "/opt/product-management"
        SERVICE_NAME = "product-management.service"
    }
    stages {
        stage('Checkout') {
            steps {
                echo "Cloning repository..."
                git branch: 'main', url: 'https://github.com/Malik-VTI/Java-App.git'
            }
        }
        stage('Build') {
            steps {
                echo "Building application..."
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                echo "Running unit tests..."
                sh 'mvn test'
            }
        }
        stage('Package') {
            steps {
                echo "Preparing deployment directory..."
                sh """
                    if [ ! -d \${APP_DIR} ]; then
                        sudo mkdir -p \${APP_DIR}
                        sudo chown $(whoami):$(whoami) \${APP_DIR}
                    fi
                """
                echo "Copying artifact to deployment directory..."
                sh """
                    sudo rm -f "${APP_DIR}/${APP_NAME}"
                    sudo cp "target/${APP_NAME}" "${APP_DIR}/"
                    sudo chmod 755 "${APP_DIR}/${APP_NAME}"
                """
            }
        }
        stage('Restart Application') {
            steps {
                echo "Restarting application service..."
                sh """
                    if systemctl list-units --full -all | grep -q ${SERVICE_NAME}; then
                        echo "P@ssw0rd" | sudo -S systemctl restart ${SERVICE_NAME}
                        echo "Service restarted successfully!"
                    else
                        echo "Service ${SERVICE_NAME} not found. Please check your systemd configuration."
                        exit 1
                    fi
                """
            }
        }
    }
}
