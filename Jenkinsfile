pipeline {
    agent any
    environment {
        // 定义 Docker 镜像名称，账号/镜像名
        DOCKER_IMAGE_NAME = "k8sshuceshi/springboot-hello-world-kubernetes"
    }
    stages {
        stage('Build') {
            steps {
                echo '运行构建自动化'
                sh './gradlew build --no-daemon'
                archiveArtifacts artifacts: 'build/libs/spring-boot-0.0.1-SNAPSHOT.jar'
            }
        }
        stage('Build Docker Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    // 构建 Docker 镜像
                    app = docker.build(DOCKER_IMAGE_NAME)
                    app.inside {
                        sh 'echo 你好，世界！'
                    }
                }
            }
        }
        stage('Push Docker Image') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker_hub_login') {
                        app.push("${env.BUILD_NUMBER}")
                        app.push("latest")
                    }
                }
            }
        }
        stage('CanaryDeploy') {
            when {
                branch 'master'
            }
            // 金丝雀部署 1 个 Pod
            environment {
                CANARY_REPLICAS = 1
            }
            steps {
                // 根据配置的 springboot-hello-world-kube-canary.yml 模版来部署镜像至 Kubernetes
                kubernetesDeploy(
                    kubeconfigId: 'kubeconfig',
                    configs: 'springboot-hello-world-kube-canary.yml',
                    enableConfigSubstitution: true
                )
            }
        }
        stage('DeployToProduction') {
            when {
                branch 'master'
            }
            // 金丝雀部署 0 个 Pod
            environment {
                CANARY_REPLICAS = 0
            }
            steps {
                input 'Deploy to Production?'
                milestone(1)
                kubernetesDeploy(
                    kubeconfigId: 'kubeconfig',
                    configs: 'springboot-hello-world-kube-canary.yml',
                    enableConfigSubstitution: true
                )
                // 根据配置的 springboot-hello-world-kube.yml 模版来部署镜像至 Kubernetes
                kubernetesDeploy(
                    kubeconfigId: 'kubeconfig',
                    configs: 'springboot-hello-world-kube.yml',
                    enableConfigSubstitution: true
                )
            }
        }
    }
}
