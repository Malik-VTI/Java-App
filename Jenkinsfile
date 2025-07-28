pipeline {
    agent any

    environment {
        REGISTRY = "docker.io/malikvti"
        IMAGE_NAME = "retail-service"
        IMAGE_TAG = "2.1"
        KUBECONFIG_CREDENTIAL = 'kubeconfig' // ID credential di Jenkins
        DOCKERHUB_CREDENTIAL = 'dockerhub-credential' // ID credential DockerHub
    }
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
                sh '/opt/apache-maven-3.9.10/bin/mvn clean install'
            }
        }
        stage('Test') {
            steps {
                // Jalankan unit test
                sh '/opt/apache-maven-3.9.10/bin/mvn test'
            }
        }

        stage('Package') {
            steps {
                // Pindahkan hasil build ke direktori aplikasiMore actions
                sh '''
                    mkdir -p /opt/sample-application
                    cp target/retail-service-1.5.jar /opt/sample-application/
                '''
            }
        }
//         stage('Build Image') {
//             steps {
//                 withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIAL}", usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
//                     // Login ke DockerHub
//                     sh '''
//                         echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
//                         docker build -t $REGISTRY/$IMAGE_NAME:$IMAGE_TAG .
//                         docker push $REGISTRY/$IMAGE_NAME:$IMAGE_TAG
//                     '''
//                 }
//             }
//         }
//         stage('Deploy to Kubernetes') {
//             steps {
//                 withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
//                     sh '''
//                         kubectl apply -f src/main/java/com/project/ecommerceapp/k8s/configmap.yaml
//                         kubectl apply -f src/main/java/com/project/ecommerceapp/k8s/secret.yaml
//                         kubectl apply -f src/main/java/com/project/ecommerceapp/k8s/redis-secret.yaml
//                         kubectl apply -f src/main/java/com/project/ecommerceapp/k8s/deployment.yaml
//                         kubectl apply -f src/main/java/com/project/ecommerceapp/k8s/service.yaml
//                     '''
//                 }
//             }
//         }
    }
}
